#ifndef CONFIG_TYPES_H
#define CONFIG_TYPES_H

// Maximum capacities for static arrays
#define MAX_USERS 50
#define MAX_SENSORS 10
#define MAX_TRACKS 20
#define MAX_TRAINS 20
#define MAX_LOGS 100

// User structure
typedef struct {
    char name[64];
    char username[32];
    char password[32];
    int caesarKey;
} User;

// Sensor configuration structure
typedef struct {
    char type[16];
    int bufferSize;
    int windowSize;
} SensorConfig;

// Track structure
typedef struct {
    int trackId;
    int state;
    int trainId;  // -1 if not assigned
} Track;

// Train structure
typedef struct {
    int trainId;
} Train;

// Log entry structure
typedef struct {
    int id;
    char username[32];
    char action[128];
    long timestamp;  // Unix epoch
} LogEntry;

#endif

