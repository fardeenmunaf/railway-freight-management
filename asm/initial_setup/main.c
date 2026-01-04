#include <stdio.h>
#include <time.h>
#include "parse_config.h"

void print_summary(void) {
    printf("\n========================================\n");
    printf("  CONFIG LOADING SUMMARY\n");
    printf("========================================\n");
    printf("  Users loaded:   %d / %d\n", user_count, MAX_USERS);
    printf("  Sensors loaded: %d / %d\n", sensor_count, MAX_SENSORS);
    printf("  Tracks loaded:  %d / %d\n", track_count, MAX_TRACKS);
    printf("  Trains loaded:  %d / %d\n", train_count, MAX_TRAINS);
    printf("  Logs loaded:    %d / %d\n", log_count, MAX_LOGS);
    printf("========================================\n\n");
}

void print_sample_data(void) {
    // Print first user
    if (user_count > 0) {
        printf("Sample USER #1:\n");
        printf("  Name:      %s\n", users[0].name);
        printf("  Username:  %s\n", users[0].username);
        printf("  Password:  %s\n", users[0].password);
        printf("  CaesarKey: %d\n\n", users[0].caesarKey);
    }

    // Print first sensor
    if (sensor_count > 0) {
        printf("Sample SENSOR #1:\n");
        printf("  Type:       %s\n", sensors[0].type);
        printf("  BufferSize: %d\n", sensors[0].bufferSize);
        printf("  WindowSize: %d\n\n", sensors[0].windowSize);
    }

    // Print first track
    if (track_count > 0) {
        printf("Sample TRACK #1:\n");
        printf("  TrackId: %d\n", tracks[0].trackId);
        printf("  State:   %d\n", tracks[0].state);
        printf("  TrainId: %d%s\n\n",
               tracks[0].trainId,
               tracks[0].trainId == -1 ? " (not assigned)" : "");
    }

    // Print first train
    if (train_count > 0) {
        printf("Sample TRAIN #1:\n");
        printf("  TrainId: %d\n\n", trains[0].trainId);
    }

    // Print first log
    if (log_count > 0) {
        printf("Sample LOG #1:\n");
        printf("  ID:        %d\n", logs[0].id);
        printf("  Username:  %s\n", logs[0].username);
        printf("  Action:    %s\n", logs[0].action);
        printf("  Timestamp: %ld", logs[0].timestamp);

        // Convert timestamp to human-readable format
        time_t t = (time_t)logs[0].timestamp;
        struct tm *tm_info = localtime(&t);
        if (tm_info) {
            char buffer[32];
            strftime(buffer, sizeof(buffer), " (%Y-%m-%d %H:%M:%S)", tm_info);
            printf("%s", buffer);
        }
        printf("\n\n");
    }
}

void demonstrate_access(void) {
    printf("========================================\n");
    printf("  DEMONSTRATION: Data Access Pattern\n");
    printf("========================================\n");
    printf("All users:\n");
    for (int i = 0; i < user_count; i++) {
        printf("  [%d] %s (@%s)\n", i, users[i].name, users[i].username);
    }
    printf("\n");

    printf("All tracks:\n");
    for (int i = 0; i < track_count; i++) {
        printf("  Track %d: State=%d, Train=%d\n",
               tracks[i].trackId,
               tracks[i].state,
               tracks[i].trainId);
    }
    printf("\n");
}

int main(void) {
    printf("USAC11 - Initial Setup from Config File\n");
    printf("========================================\n\n");

    // Load configuration
    printf("Loading config.txt...\n");
    if (load_config("config.txt") != 0) {
        printf("Failed to load configuration!\n");
        return 1;
    }

    printf("Configuration loaded successfully!\n");

    // Print summary
    print_summary();

    // Print sample data
    print_sample_data();

    // Demonstrate access
    demonstrate_access();

    printf("========================================\n");
    printf("Setup complete! Data structures ready.\n");
    printf("========================================\n");

    return 0;
}

