#ifndef LIGHTSIGNS_H
#define LIGHTSIGNS_H

#include "tracks.h"

// Light signal colors
typedef enum {
    LIGHT_OFF = 0,
    LIGHT_RED = 1,      // Stop / Occupied / Emergency
    LIGHT_YELLOW = 2,   // Caution
    LIGHT_GREEN = 3,    // Go / Free
    LIGHT_BLUE = 4      // Maintenance / Non-operational
} LightColor;

// Light signal structure
typedef struct {
    int track_id;
    LightColor color;
    int last_change_time;
} LightSignal;

// Initialize light signals
void lightsigns_init(LightSignal *signals, int num_tracks);

// Update light signal based on track state
void lightsigns_update_from_track(LightSignal *signal, const Track *track);

// Update all light signals based on track system
void lightsigns_update_all(LightSignal *signals, const TrackSystem *system);

// Set specific light signal color
void lightsigns_set_color(LightSignal *signal, LightColor color);

#endif // LIGHTSIGNS_H
