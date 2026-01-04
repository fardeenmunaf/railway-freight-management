#include "ui.h"
#include "board.h"
#include "config.h"
#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include <unistd.h>
#include <sys/select.h>
#include <termios.h>

// Global config
static Config globalConfig;
static int configLoaded = 0;

// Function to read password without echo
static void read_password(char *password, size_t maxlen) {
    struct termios old, new;
    
    // Get current terminal settings
    tcgetattr(0, &old);
    new = old;
    
    // Disable echo
    new.c_lflag &= ~ECHO;
    tcsetattr(0, TCSANOW, &new);
    
    // Read password
    if (fgets(password, maxlen, stdin)) {
        size_t len = strlen(password);
        if (len > 0 && password[len-1] == '\n') {
            password[len-1] = '\0';
        }
    }
    
    // Restore terminal settings
    tcsetattr(0, TCSANOW, &old);
    printf("\n");
}

int ui_login(char *username, size_t usize) {
    // Load config if not already loaded
    if (!configLoaded) {
        printf("Loading configuration...\n");
        if (!config_load("config.txt", &globalConfig)) {
            printf(COLOR_RED "ERROR: Failed to load config.txt\n" COLOR_RESET);
            return 0;
        }
        configLoaded = 1;
    }
    
    printf(COLOR_CYAN "\n╔════════════════════════════════════════════════════════════════════╗\n" COLOR_RESET);
    printf(COLOR_CYAN "║" COLOR_YELLOW "                           USER LOGIN                              " COLOR_CYAN "║\n" COLOR_RESET);
    printf(COLOR_CYAN "╚════════════════════════════════════════════════════════════════════╝\n" COLOR_RESET);
    printf("\n");
    
    int attempts = 0;
    const int MAX_ATTEMPTS = 3;
    
    while (attempts < MAX_ATTEMPTS) {
        char inputUsername[64];
        char inputPassword[64];
        char fullName[64];
        
        printf(COLOR_WHITE "Enter username: " COLOR_RESET);
        fflush(stdout);
        
        if (!fgets(inputUsername, sizeof(inputUsername), stdin)) {
            return 0;
        }
        
        // Remove newline
        size_t len = strlen(inputUsername);
        if (len > 0 && inputUsername[len-1] == '\n') {
            inputUsername[len-1] = '\0';
        }
        
        if (strlen(inputUsername) == 0) {
            return 0;
        }
        
        printf(COLOR_WHITE "Enter password: " COLOR_RESET);
        fflush(stdout);
        read_password(inputPassword, sizeof(inputPassword));
        
        // Verify credentials
        if (config_verify_user(&globalConfig, inputUsername, inputPassword, fullName)) {
            printf(COLOR_GREEN "\nLogin successful!\n" COLOR_RESET);
            printf(COLOR_GREEN "Welcome, %s!\n" COLOR_RESET, fullName);
            printf(COLOR_YELLOW "Loading system...\n" COLOR_RESET);
            fflush(stdout);
            
            // Store full name in username
            strncpy(username, fullName, usize - 1);
            username[usize - 1] = '\0';
            
            sleep(2);
            return 1;
        } else {
            attempts++;
            printf(COLOR_RED "\nInvalid username or password.\n" COLOR_RESET);
            if (attempts < MAX_ATTEMPTS) {
                printf(COLOR_YELLOW "Attempts remaining: %d\n\n" COLOR_RESET, MAX_ATTEMPTS - attempts);
            }
        }
    }
    
    printf(COLOR_RED "\nMaximum login attempts exceeded. Access denied.\n" COLOR_RESET);
    return 0;
}

Instruction ui_get_instruction(void) {
    Instruction inst = {INST_NONE, 0, ""};
    
    board_show_footer();
    printf(COLOR_WHITE "\nEnter command: " COLOR_RESET);
    fflush(stdout);
    
    char line[100];
    memset(line, 0, sizeof(line));  // Clear buffer
    
    // Clear any leftover input
    int c;
    fd_set readfds;
    struct timeval tv;
    FD_ZERO(&readfds);
    FD_SET(0, &readfds);  // stdin is fd 0
    tv.tv_sec = 0;
    tv.tv_usec = 0;
    if (select(1, &readfds, NULL, NULL, &tv) > 0) {
        // There's leftover input, consume it
        while ((c = getchar()) != '\n' && c != EOF);
    }
    
    if (!fgets(line, sizeof(line), stdin)) {
        // EOF or error - exit gracefully
        inst.type = INST_EXIT;
        return inst;
    }
    
    // Remove newline
    size_t len = strlen(line);
    if (len > 0 && line[len-1] == '\n') {
        line[len-1] = '\0';
    }
    
    // Check if empty - just return INST_NONE and loop again
    if (strlen(line) == 0) {
        inst.type = INST_NONE;
        printf(COLOR_YELLOW "Empty command. Please try again.\n" COLOR_RESET);
        sleep(1);
        return inst;
    }
    
    // Convert to uppercase for comparison
    for (size_t i = 0; i < strlen(line); i++) {
        line[i] = toupper(line[i]);
    }
    
    if (strcmp(line, "S") == 0 || strcmp(line, "SHOW") == 0) {
        inst.type = INST_SHOW_ENV;
    } 
    else if (strcmp(line, "L") == 0 || strcmp(line, "LIGHT") == 0 || strcmp(line, "LIGHTS") == 0) {
        inst.type = INST_CONTROL_LIGHTS;
        
        // Ask for track number
        printf(COLOR_WHITE "  Enter track number (1-8): " COLOR_RESET);
        if (scanf("%d", &inst.track) != 1) {
            inst.track = 1;
        }
        getchar(); // consume newline
        
        // Ask for light color
        printf(COLOR_WHITE "  Enter light color (RE/YE/GE/RB): " COLOR_RESET);
        if (scanf("%2s", inst.color) != 1) {
            strcpy(inst.color, "RE");
        }
        getchar(); // consume newline
        
        // Convert to uppercase
        for (int i = 0; inst.color[i]; i++) {
            inst.color[i] = toupper(inst.color[i]);
        }
    }
    else if (strcmp(line, "E") == 0 || strcmp(line, "EXIT") == 0 || strcmp(line, "Q") == 0 || strcmp(line, "QUIT") == 0) {
        inst.type = INST_EXIT;
    }
    else {
        inst.type = INST_NONE;
        printf(COLOR_RED "\nUnknown command '%s'. Please try again.\n" COLOR_RESET, line);
        sleep(1);
    }
    
    return inst;
}
