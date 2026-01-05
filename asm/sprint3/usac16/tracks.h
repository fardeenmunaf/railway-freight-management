#ifndef TRACKS_H
#define TRACKS_H

#include <stdbool.h>

#define MAX_TRACKS 8
#define MAX_TRAIN_ID 20

// Track states
typedef enum {
    TRACK_FREE = 0,
    TRACK_OCCUPIED = 1,
    TRACK_NON_OPERATIONAL = 2,
    TRACK_EMERGENCY = 3
} TrackState;

// Track structure
typedef struct {
    int track_id;                  // Track number (1-8)
    TrackState state;              // Current state
    char train_id[MAX_TRAIN_ID];  // ID of train on track (empty if free)
    int occupied_time;             // Time since occupation (in seconds)
} Track;

// Track system
typedef struct {
    Track tracks[MAX_TRACKS];
    int total_tracks;
    int free_tracks;
    int occupied_tracks;
    int non_operational_tracks;
    int total_arrivals;
    int total_departures;
    int emergency_stops;
} TrackSystem;

// Assembly function: assign track to arriving train
// Parameters: TrackSystem* system, const char* train_id
// Returns: track number (1-8) if successful, -1 if emergency stop needed
extern int assign_track_asm(TrackSystem *system, const char *train_id);

// C functions for track operations
void tracks_init(TrackSystem *system, int num_tracks);
int  tracks_set_non_operational(TrackSystem *system, int track_id);
int  tracks_set_free(TrackSystem *system, int track_id);
int  tracks_give_departure(TrackSystem *system, int track_id);
void tracks_update_times(TrackSystem *system);
Track* tracks_get_track(TrackSystem *system, int track_id);

#endif // TRACKS_H
