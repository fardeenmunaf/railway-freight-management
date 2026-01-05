// tracks.c - Track management implementation

#include "tracks.h"
#include <stdio.h>
#include <string.h>

// Initialize track system
void tracks_init(TrackSystem *system, int num_tracks) {
    if (num_tracks > MAX_TRACKS) {
        num_tracks = MAX_TRACKS;
    }
    
    system->total_tracks = num_tracks;
    system->free_tracks = num_tracks;
    system->occupied_tracks = 0;
    system->non_operational_tracks = 0;
    system->total_arrivals = 0;
    system->total_departures = 0;
    system->emergency_stops = 0;
    
    // Initialize all tracks
    for (int i = 0; i < MAX_TRACKS; i++) {
        system->tracks[i].track_id = i + 1;
        if (i < num_tracks) {
            system->tracks[i].state = TRACK_FREE;
        } else {
            system->tracks[i].state = TRACK_NON_OPERATIONAL;
            system->non_operational_tracks++;
            system->free_tracks--;
        }
        memset(system->tracks[i].train_id, 0, MAX_TRAIN_ID);
        system->tracks[i].occupied_time = 0;
    }
    
    printf("[Tracks] System initialized with %d tracks\n", num_tracks);
}

// Set a track as non-operational
int tracks_set_non_operational(TrackSystem *system, int track_id) {
    if (track_id < 1 || track_id > system->total_tracks) {
        printf("[Error] Invalid track ID: %d\n", track_id);
        return -1;
    }
    
    Track *track = &system->tracks[track_id - 1];
    
    if (track->state == TRACK_OCCUPIED) {
        printf("[Error] Cannot set track %d as non-operational - occupied by train %s\n",
               track_id, track->train_id);
        return -1;
    }
    
    if (track->state == TRACK_NON_OPERATIONAL) {
        printf("[Warning] Track %d already non-operational\n", track_id);
        return 0;
    }
    
    // Update state
    TrackState old_state = track->state;
    track->state = TRACK_NON_OPERATIONAL;
    memset(track->train_id, 0, MAX_TRAIN_ID);
    track->occupied_time = 0;
    
    // Update statistics
    if (old_state == TRACK_FREE) {
        system->free_tracks--;
    }
    system->non_operational_tracks++;
    
    printf("[Tracks] Track %d set as NON-OPERATIONAL\n", track_id);
    return 0;
}

// Set a track as free
int tracks_set_free(TrackSystem *system, int track_id) {
    if (track_id < 1 || track_id > system->total_tracks) {
        printf("[Error] Invalid track ID: %d\n", track_id);
        return -1;
    }
    
    Track *track = &system->tracks[track_id - 1];
    
    if (track->state == TRACK_FREE) {
        printf("[Warning] Track %d already free\n", track_id);
        return 0;
    }
    
    if (track->state == TRACK_OCCUPIED) {
        printf("[Warning] Track %d occupied by train %s - forcing free\n",
               track_id, track->train_id);
        system->occupied_tracks--;
    } else if (track->state == TRACK_NON_OPERATIONAL) {
        system->non_operational_tracks--;
    }
    
    // Update state
    track->state = TRACK_FREE;
    memset(track->train_id, 0, MAX_TRAIN_ID);
    track->occupied_time = 0;
    system->free_tracks++;
    
    printf("[Tracks] Track %d set as FREE\n", track_id);
    return 0;
}

// Give departure order to a train
int tracks_give_departure(TrackSystem *system, int track_id) {
    if (track_id < 1 || track_id > system->total_tracks) {
        printf("[Error] Invalid track ID: %d\n", track_id);
        return -1;
    }
    
    Track *track = &system->tracks[track_id - 1];
    
    if (track->state != TRACK_OCCUPIED) {
        printf("[Error] Track %d is not occupied - cannot give departure order\n", track_id);
        return -1;
    }
    
    printf("[Tracks] Departure order given to train %s on track %d\n",
           track->train_id, track_id);
    
    // Free the track
    track->state = TRACK_FREE;
    memset(track->train_id, 0, MAX_TRAIN_ID);
    track->occupied_time = 0;
    
    // Update statistics
    system->occupied_tracks--;
    system->free_tracks++;
    system->total_departures++;
    
    return 0;
}

// Update occupied times (call periodically)
void tracks_update_times(TrackSystem *system) {
    for (int i = 0; i < system->total_tracks; i++) {
        if (system->tracks[i].state == TRACK_OCCUPIED) {
            system->tracks[i].occupied_time++;
        }
    }
}

// Get track by ID
Track* tracks_get_track(TrackSystem *system, int track_id) {
    if (track_id < 1 || track_id > MAX_TRACKS) {
        return NULL;
    }
    return &system->tracks[track_id - 1];
}
