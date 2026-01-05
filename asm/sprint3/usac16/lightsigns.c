// lightsigns.c - Light signal control implementation

#include "lightsigns.h"
#include <stdio.h>
#include <string.h>
#include <unistd.h>

// External serial file descriptor
extern int serial_fd;

// Send command to hardware (if connected)
static void send_hardware_command(int track_id, LightColor color) {
    if (serial_fd < 0) {
        return;  // No hardware, silent fail
    }
    
    char cmd[32];
    const char *color_str;
    
    switch (color) {
        case LIGHT_RED: color_str = "RED"; break;
        case LIGHT_YELLOW: color_str = "YELLOW"; break;
        case LIGHT_GREEN: color_str = "GREEN"; break;
        case LIGHT_BLUE: color_str = "BLUE"; break;
        default: color_str = "OFF"; break;
    }
    
    snprintf(cmd, sizeof(cmd), "LIGHT:%d:%s\n", track_id, color_str);
    write(serial_fd, cmd, strlen(cmd));
    fsync(serial_fd);
}

// Initialize light signals
void lightsigns_init(LightSignal *signals, int num_tracks) {
    for (int i = 0; i < num_tracks; i++) {
        signals[i].track_id = i + 1;
        signals[i].color = LIGHT_GREEN;  // Start with green (free)
        signals[i].last_change_time = 0;
    }
    printf("[LightSigns] %d signals initialized\n", num_tracks);
}

// Update light signal based on track state
void lightsigns_update_from_track(LightSignal *signal, const Track *track) {
    LightColor new_color;
    
    switch (track->state) {
        case TRACK_FREE:
            new_color = LIGHT_GREEN;  // Green = go, track is free
            break;
        case TRACK_OCCUPIED:
            new_color = LIGHT_RED;    // Red = stop, track occupied
            break;
        case TRACK_NON_OPERATIONAL:
            new_color = LIGHT_BLUE;   // Blue = maintenance, not operational
            break;
        case TRACK_EMERGENCY:
            new_color = LIGHT_RED;    // Red = emergency stop
            break;
        default:
            new_color = LIGHT_OFF;
            break;
    }
    
    // Update if changed
    if (signal->color != new_color) {
        signal->color = new_color;
        signal->last_change_time = 0;  // Reset timer
        
        const char *color_name;
        switch (new_color) {
            case LIGHT_RED: color_name = "RED"; break;
            case LIGHT_YELLOW: color_name = "YELLOW"; break;
            case LIGHT_GREEN: color_name = "GREEN"; break;
            case LIGHT_BLUE: color_name = "BLUE"; break;
            default: color_name = "OFF"; break;
        }
        
        printf("[LightSigns] Track %d signal changed to %s\n", 
               signal->track_id, color_name);
        
        // Send to hardware
        send_hardware_command(signal->track_id, new_color);
    }
}

// Update all light signals based on track system
void lightsigns_update_all(LightSignal *signals, const TrackSystem *system) {
    for (int i = 0; i < system->total_tracks; i++) {
        lightsigns_update_from_track(&signals[i], &system->tracks[i]);
        signals[i].last_change_time++;
    }
}

// Set specific light signal color
void lightsigns_set_color(LightSignal *signal, LightColor color) {
    if (signal->color != color) {
        signal->color = color;
        signal->last_change_time = 0;
        
        const char *color_name;
        switch (color) {
            case LIGHT_RED: color_name = "RED"; break;
            case LIGHT_YELLOW: color_name = "YELLOW"; break;
            case LIGHT_GREEN: color_name = "GREEN"; break;
            case LIGHT_BLUE: color_name = "BLUE"; break;
            default: color_name = "OFF"; break;
        }
        
        // Send to hardware
        send_hardware_command(signal->track_id, color);
        
        printf("[LightSigns] Track %d signal manually set to %s\n", 
               signal->track_id, color_name);
    }
}
