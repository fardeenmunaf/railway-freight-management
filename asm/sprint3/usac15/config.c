// config.c - Configuration file parser

#define _POSIX_C_SOURCE 200809L

#include "config.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

// Trim whitespace from string
static void trim(char *str) {
    char *start = str;
    char *end;
    
    // Trim leading space
    while(isspace((unsigned char)*start)) start++;
    
    if(*start == 0) {
        *str = 0;
        return;
    }
    
    // Trim trailing space
    end = start + strlen(start) - 1;
    while(end > start && isspace((unsigned char)*end)) end--;
    
    // Write new null terminator
    size_t len = (end - start) + 1;
    memmove(str, start, len);
    str[len] = 0;
}

int config_load(const char *filename, Config *cfg) {
    FILE *f = fopen(filename, "r");
    if (!f) {
        printf("ERROR: Could not open config file: %s\n", filename);
        return 0;
    }
    
    cfg->userCount = 0;
    char line[256];
    
    while (fgets(line, sizeof(line), f)) {
        // Remove newline
        size_t len = strlen(line);
        if (len > 0 && line[len-1] == '\n') {
            line[len-1] = '\0';
        }
        
        // Skip empty lines and comments
        trim(line);
        if (line[0] == '\0' || line[0] == '#') {
            continue;
        }
        
        // Parse USER line: USER,Name,username,password,caesarkey
        if (strncmp(line, "USER,", 5) == 0 || strncmp(line, "USER;", 5) == 0) {
            if (cfg->userCount >= MAX_USERS) {
                printf("WARNING: Maximum users reached, ignoring additional users\n");
                continue;
            }
            
            char *token;
            char lineCopy[256];
            strncpy(lineCopy, line, sizeof(lineCopy));
            lineCopy[sizeof(lineCopy) - 1] = '\0';
            
            // Skip "USER,"
            char *dataStart = strchr(lineCopy, ',');
            if (!dataStart) dataStart = strchr(lineCopy, ';');
            if (!dataStart) continue;
            dataStart++; // Skip the comma/semicolon
            
            // Parse name
            token = strtok(dataStart, ",;");
            if (!token) continue;
            trim(token);
            strncpy(cfg->users[cfg->userCount].name, token, MAX_NAME_LEN - 1);
            cfg->users[cfg->userCount].name[MAX_NAME_LEN - 1] = '\0';
            
            // Parse username
            token = strtok(NULL, ",;");
            if (!token) continue;
            trim(token);
            strncpy(cfg->users[cfg->userCount].username, token, MAX_USERNAME_LEN - 1);
            cfg->users[cfg->userCount].username[MAX_USERNAME_LEN - 1] = '\0';
            
            // Parse password (encrypted)
            token = strtok(NULL, ",;");
            if (!token) continue;
            trim(token);
            strncpy(cfg->users[cfg->userCount].password, token, MAX_PASSWORD_LEN - 1);
            cfg->users[cfg->userCount].password[MAX_PASSWORD_LEN - 1] = '\0';
            
            // Parse caesar key
            token = strtok(NULL, ",;");
            if (!token) continue;
            trim(token);
            cfg->users[cfg->userCount].caesarKey = atoi(token);
            
            printf("Loaded user: %s (username: %s, key: %d)\n", 
                   cfg->users[cfg->userCount].name,
                   cfg->users[cfg->userCount].username,
                   cfg->users[cfg->userCount].caesarKey);
            
            cfg->userCount++;
        }
    }
    
    fclose(f);
    printf("\nLoaded %d users from config file\n\n", cfg->userCount);
    return cfg->userCount > 0;
}

// Helper function to convert string to uppercase
static void to_upper(char *str) {
    for (int i = 0; str[i]; i++) {
        str[i] = toupper((unsigned char)str[i]);
    }
}

bool config_verify_user(const Config *cfg, const char *username, const char *password, char *fullName) {
    for (int i = 0; i < cfg->userCount; i++) {
        if (strcmp(cfg->users[i].username, username) == 0) {
            // Found user, now verify password
            // Convert both passwords to uppercase for comparison
            char upperInputPassword[MAX_PASSWORD_LEN];
            char upperConfigPassword[MAX_PASSWORD_LEN];
            
            strncpy(upperInputPassword, password, MAX_PASSWORD_LEN - 1);
            upperInputPassword[MAX_PASSWORD_LEN - 1] = '\0';
            to_upper(upperInputPassword);
            
            strncpy(upperConfigPassword, cfg->users[i].password, MAX_PASSWORD_LEN - 1);
            upperConfigPassword[MAX_PASSWORD_LEN - 1] = '\0';
            to_upper(upperConfigPassword);
            
            // Compare passwords directly (case-insensitive)
            if (strcmp(upperInputPassword, upperConfigPassword) == 0) {
                // Password matches!
                if (fullName) {
                    strncpy(fullName, cfg->users[i].name, MAX_NAME_LEN - 1);
                    fullName[MAX_NAME_LEN - 1] = '\0';
                }
                return true;
            }
            
            // Username found but password doesn't match
            return false;
        }
    }
    
    // Username not found
    return false;
}
