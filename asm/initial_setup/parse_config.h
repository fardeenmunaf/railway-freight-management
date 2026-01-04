#ifndef PARSE_CONFIG_H
#define PARSE_CONFIG_H

#include "config_types.h"

// Global static arrays (accessible from C and Assembly)
extern User users[MAX_USERS];
extern SensorConfig sensors[MAX_SENSORS];
extern Track tracks[MAX_TRACKS];
extern Train trains[MAX_TRAINS];
extern LogEntry logs[MAX_LOGS];

// Counters for each array
extern int user_count;
extern int sensor_count;
extern int track_count;
extern int train_count;
extern int log_count;

/**
 * Load configuration from file and populate global arrays.
 *
 * @param filename Path to config.txt
 * @return 0 on success, -1 if file cannot be opened
 *
 * Behavior:
 * - Skips comment lines (starting with #) and empty lines
 * - Logs warnings for malformed lines but continues parsing
 * - Stops adding entries if max capacity is reached
 */
int load_config(const char *filename);

#endif
