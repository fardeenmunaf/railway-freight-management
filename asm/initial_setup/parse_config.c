#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include "parse_config.h"

// Global static arrays
User users[MAX_USERS];
SensorConfig sensors[MAX_SENSORS];
Track tracks[MAX_TRACKS];
Train trains[MAX_TRAINS];
LogEntry logs[MAX_LOGS];

// Counters
int user_count = 0;
int sensor_count = 0;
int track_count = 0;
int train_count = 0;
int log_count = 0;

// Helper function to trim whitespace
static char* trim(char *str) {
    char *end;

    // Trim leading space
    while(isspace((unsigned char)*str)) str++;

    if(*str == 0) return str;

    // Trim trailing space
    end = str + strlen(str) - 1;
    while(end > str && isspace((unsigned char)*end)) end--;
    end[1] = '\0';

    return str;
}

// Parse USER line
static void parse_user(char *line, int line_num) {
    if (user_count >= MAX_USERS) {
        printf("Warning: Max users reached, skipping line %d\n", line_num);
        return;
    }

    char *token = strtok(line + 5, ",");  // Skip "USER,"
    if (!token) {
        printf("Warning: Malformed USER line %d (missing name)\n", line_num);
        return;
    }
    strncpy(users[user_count].name, trim(token), sizeof(users[user_count].name) - 1);

    token = strtok(NULL, ",");
    if (!token) {
        printf("Warning: Malformed USER line %d (missing username)\n", line_num);
        return;
    }
    strncpy(users[user_count].username, trim(token), sizeof(users[user_count].username) - 1);

    token = strtok(NULL, ",");
    if (!token) {
        printf("Warning: Malformed USER line %d (missing password)\n", line_num);
        return;
    }
    strncpy(users[user_count].password, trim(token), sizeof(users[user_count].password) - 1);

    token = strtok(NULL, ",\n");
    if (!token) {
        printf("Warning: Malformed USER line %d (missing caesarKey)\n", line_num);
        return;
    }
    users[user_count].caesarKey = atoi(trim(token));

    user_count++;
}

// Parse SENSOR_CONFIG line
static void parse_sensor(char *line, int line_num) {
    if (sensor_count >= MAX_SENSORS) {
        printf("Warning: Max sensors reached, skipping line %d\n", line_num);
        return;
    }

    char *token = strtok(line + 14, ",");  // Skip "SENSOR_CONFIG,"
    if (!token) {
        printf("Warning: Malformed SENSOR_CONFIG line %d (missing type)\n", line_num);
        return;
    }
    strncpy(sensors[sensor_count].type, trim(token), sizeof(sensors[sensor_count].type) - 1);

    token = strtok(NULL, ",");
    if (!token) {
        printf("Warning: Malformed SENSOR_CONFIG line %d (missing bufferSize)\n", line_num);
        return;
    }
    sensors[sensor_count].bufferSize = atoi(trim(token));

    token = strtok(NULL, ",\n");
    if (!token) {
        printf("Warning: Malformed SENSOR_CONFIG line %d (missing windowSize)\n", line_num);
        return;
    }
    sensors[sensor_count].windowSize = atoi(trim(token));

    sensor_count++;
}

// Parse TRACK line
static void parse_track(char *line, int line_num) {
    if (track_count >= MAX_TRACKS) {
        printf("Warning: Max tracks reached, skipping line %d\n", line_num);
        return;
    }

    char *token = strtok(line + 6, ",");  // Skip "TRACK,"
    if (!token) {
        printf("Warning: Malformed TRACK line %d (missing trackId)\n", line_num);
        return;
    }
    tracks[track_count].trackId = atoi(trim(token));

    token = strtok(NULL, ",");
    if (!token) {
        printf("Warning: Malformed TRACK line %d (missing state)\n", line_num);
        return;
    }
    tracks[track_count].state = atoi(trim(token));

    // Optional trainId field
    token = strtok(NULL, ",\n");
    if (token) {
        tracks[track_count].trainId = atoi(trim(token));
    } else {
        tracks[track_count].trainId = -1;  // Not assigned
    }

    track_count++;
}

// Parse TRAIN line
static void parse_train(char *line, int line_num) {
    if (train_count >= MAX_TRAINS) {
        printf("Warning: Max trains reached, skipping line %d\n", line_num);
        return;
    }

    char *token = strtok(line + 6, ",\n");  // Skip "TRAIN,"
    if (!token) {
        printf("Warning: Malformed TRAIN line %d (missing trainId)\n", line_num);
        return;
    }
    trains[train_count].trainId = atoi(trim(token));

    train_count++;
}

// Parse LOG line
static void parse_log(char *line, int line_num) {
    if (log_count >= MAX_LOGS) {
        printf("Warning: Max logs reached, skipping line %d\n", line_num);
        return;
    }

    char *token = strtok(line + 4, ",");  // Skip "LOG,"
    if (!token) {
        printf("Warning: Malformed LOG line %d (missing id)\n", line_num);
        return;
    }
    logs[log_count].id = atoi(trim(token));

    token = strtok(NULL, ",");
    if (!token) {
        printf("Warning: Malformed LOG line %d (missing username)\n", line_num);
        return;
    }
    strncpy(logs[log_count].username, trim(token), sizeof(logs[log_count].username) - 1);

    token = strtok(NULL, ",");
    if (!token) {
        printf("Warning: Malformed LOG line %d (missing action)\n", line_num);
        return;
    }
    strncpy(logs[log_count].action, trim(token), sizeof(logs[log_count].action) - 1);

    token = strtok(NULL, ",\n");
    if (!token) {
        printf("Warning: Malformed LOG line %d (missing timestamp)\n", line_num);
        return;
    }
    logs[log_count].timestamp = atol(trim(token));

    log_count++;
}

// Main parser function
int load_config(const char *filename) {
    FILE *file = fopen(filename, "r");
    if (!file) {
        printf("Error: Cannot open file '%s'\n", filename);
        return -1;
    }

    char line[512];
    int line_num = 0;

    // Reset counters
    user_count = 0;
    sensor_count = 0;
    track_count = 0;
    train_count = 0;
    log_count = 0;

    while (fgets(line, sizeof(line), file)) {
        line_num++;

        // Remove trailing newline
        line[strcspn(line, "\r\n")] = 0;

        // Skip empty lines and comments
        char *trimmed = trim(line);
        if (trimmed[0] == '\0' || trimmed[0] == '#') {
            continue;
        }

        // Parse based on prefix
        if (strncmp(trimmed, "USER,", 5) == 0) {
            parse_user(trimmed, line_num);
        }
        else if (strncmp(trimmed, "SENSOR_CONFIG,", 14) == 0) {
            parse_sensor(trimmed, line_num);
        }
        else if (strncmp(trimmed, "TRACK,", 6) == 0) {
            parse_track(trimmed, line_num);
        }
        else if (strncmp(trimmed, "TRAIN,", 6) == 0) {
            parse_train(trimmed, line_num);
        }
        else if (strncmp(trimmed, "LOG,", 4) == 0) {
            parse_log(trimmed, line_num);
        }
        else {
            printf("Warning: Unknown line type at line %d: %s\n", line_num, trimmed);
        }
    }

    fclose(file);
    return 0;
}

