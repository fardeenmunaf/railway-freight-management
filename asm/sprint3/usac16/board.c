// board.c - Synoptic board display implementation

#include "board.h"
#include <stdio.h>
#include <string.h>
#include <time.h>
#include <unistd.h>

// Suppress usleep warning
#define _DEFAULT_SOURCE

// Initialize board display
void board_init(void) {
    board_clear_screen();
    printf("\n");
    printf(COLOR_CYAN "╔════════════════════════════════════════════════════════════════════╗\n" COLOR_RESET);
    printf(COLOR_CYAN "║" COLOR_YELLOW "            RAILWAY TRACK MANAGEMENT SYSTEM - USAC16           " COLOR_CYAN "║\n" COLOR_RESET);
    printf(COLOR_CYAN "╚════════════════════════════════════════════════════════════════════╝\n" COLOR_RESET);
    printf("\n");
    usleep(500000); // 0.5 second pause
}

// Clear screen
void board_clear_screen(void) {
    printf("\033[2J\033[H"); // ANSI escape codes
}

// Display header with current time
void board_show_header(void) {
    time_t now = time(NULL);
    struct tm *t = localtime(&now);
    
    printf("\n");
    printf(COLOR_CYAN "┌────────────────────────────────────────────────────────────────────┐\n" COLOR_RESET);
    printf(COLOR_CYAN "│" COLOR_WHITE " Time: %02d:%02d:%02d                    Track Management Control   " COLOR_CYAN "│\n" COLOR_RESET,
           t->tm_hour, t->tm_min, t->tm_sec);
    printf(COLOR_CYAN "└────────────────────────────────────────────────────────────────────┘\n" COLOR_RESET);
}

// Get color for track state
static const char* get_track_color(TrackState state) {
    switch (state) {
        case TRACK_FREE: return COLOR_GREEN;
        case TRACK_OCCUPIED: return COLOR_YELLOW;
        case TRACK_NON_OPERATIONAL: return COLOR_RED;
        case TRACK_EMERGENCY: return COLOR_MAGENTA;
        default: return COLOR_WHITE;
    }
}

// Get state name
static const char* get_state_name(TrackState state) {
    switch (state) {
        case TRACK_FREE: return "FREE     ";
        case TRACK_OCCUPIED: return "OCCUPIED ";
        case TRACK_NON_OPERATIONAL: return "NON-OPER ";
        case TRACK_EMERGENCY: return "EMERGENCY";
        default: return "UNKNOWN  ";
    }
}

// Display tracks status
void board_show_tracks(const TrackSystem *system) {
    printf("\n");
    printf(COLOR_BLUE "╔════════════════════════════════════════════════════════════════════╗\n" COLOR_RESET);
    printf(COLOR_BLUE "║" COLOR_YELLOW "                        TRACK STATUS                               " COLOR_BLUE "║\n" COLOR_RESET);
    printf(COLOR_BLUE "╠════════════════════════════════════════════════════════════════════╣\n" COLOR_RESET);
    
    for (int i = 0; i < system->total_tracks; i++) {
        const Track *track = &system->tracks[i];
        const char *color = get_track_color(track->state);
        const char *state_name = get_state_name(track->state);
        
        printf(COLOR_BLUE "║ " COLOR_RESET);
        printf(COLOR_WHITE "Track %d: " COLOR_RESET, track->track_id);
        printf("%s%s" COLOR_RESET, color, state_name);
        
        if (track->state == TRACK_OCCUPIED) {
            printf(" │ Train: " COLOR_CYAN "%s" COLOR_RESET, track->train_id);
            printf(" │ Time: " COLOR_YELLOW "%ds" COLOR_RESET, track->occupied_time);
            // Pad to align
            int padding = 25 - strlen(track->train_id);
            for (int j = 0; j < padding; j++) printf(" ");
        } else if (track->state == TRACK_FREE) {
            printf(" │ " COLOR_GREEN "Available for assignment" COLOR_RESET);
            printf("                      ");
        } else {
            printf(" │ " COLOR_RED "Not available" COLOR_RESET);
            printf("                               ");
        }
        
        printf(COLOR_BLUE "║\n" COLOR_RESET);
    }
    
    printf(COLOR_BLUE "╚════════════════════════════════════════════════════════════════════╝\n" COLOR_RESET);
}

// Display statistics
void board_show_statistics(const TrackSystem *system) {
    printf("\n");
    printf(COLOR_MAGENTA "╔════════════════════════════════════════════════════════════════════╗\n" COLOR_RESET);
    printf(COLOR_MAGENTA "║" COLOR_YELLOW "                      SYSTEM STATISTICS                            " COLOR_MAGENTA "║\n" COLOR_RESET);
    printf(COLOR_MAGENTA "╠════════════════════════════════════════════════════════════════════╣\n" COLOR_RESET);
    
    printf(COLOR_MAGENTA "║  " COLOR_WHITE "Total Tracks:          " COLOR_CYAN "%2d" COLOR_RESET 
           "                                          " COLOR_MAGENTA "║\n" COLOR_RESET, 
           system->total_tracks);
    
    printf(COLOR_MAGENTA "║  " COLOR_WHITE "Free Tracks:           " COLOR_GREEN "%2d" COLOR_RESET 
           "                                          " COLOR_MAGENTA "║\n" COLOR_RESET, 
           system->free_tracks);
    
    printf(COLOR_MAGENTA "║  " COLOR_WHITE "Occupied Tracks:       " COLOR_YELLOW "%2d" COLOR_RESET 
           "                                          " COLOR_MAGENTA "║\n" COLOR_RESET, 
           system->occupied_tracks);
    
    printf(COLOR_MAGENTA "║  " COLOR_WHITE "Non-Operational:       " COLOR_RED "%2d" COLOR_RESET 
           "                                          " COLOR_MAGENTA "║\n" COLOR_RESET, 
           system->non_operational_tracks);
    
    printf(COLOR_MAGENTA "║                                                                    ║\n" COLOR_RESET);
    
    printf(COLOR_MAGENTA "║  " COLOR_WHITE "Total Arrivals:        " COLOR_CYAN "%4d" COLOR_RESET 
           "                                        " COLOR_MAGENTA "║\n" COLOR_RESET, 
           system->total_arrivals);
    
    printf(COLOR_MAGENTA "║  " COLOR_WHITE "Total Departures:      " COLOR_CYAN "%4d" COLOR_RESET 
           "                                        " COLOR_MAGENTA "║\n" COLOR_RESET, 
           system->total_departures);
    
    printf(COLOR_MAGENTA "║  " COLOR_WHITE "Emergency Stops:       " COLOR_RED "%4d" COLOR_RESET 
           "                                        " COLOR_MAGENTA "║\n" COLOR_RESET, 
           system->emergency_stops);
    
    printf(COLOR_MAGENTA "╚════════════════════════════════════════════════════════════════════╝\n" COLOR_RESET);
}

// Display light signals status
void board_show_light_status(const LightSignal *signals, int num_tracks) {
    printf("\n");
    printf(COLOR_YELLOW "╔════════════════════════════════════════════════════════════════════╗\n" COLOR_RESET);
    printf(COLOR_YELLOW "║" COLOR_WHITE "                      LIGHT SIGNALS STATUS                        " COLOR_YELLOW "║\n" COLOR_RESET);
    printf(COLOR_YELLOW "╠════════════════════════════════════════════════════════════════════╣\n" COLOR_RESET);
    
    for (int i = 0; i < num_tracks; i++) {
        const LightSignal *sig = &signals[i];
        const char *light_color;
        const char *light_name;
        
        switch (sig->color) {
            case LIGHT_RED:
                light_color = COLOR_RED;
                light_name = "RED (STOP)   ";
                break;
            case LIGHT_YELLOW:
                light_color = COLOR_YELLOW;
                light_name = "YELLOW (WARN)";
                break;
            case LIGHT_GREEN:
                light_color = COLOR_GREEN;
                light_name = "GREEN (GO)   ";
                break;
            case LIGHT_BLUE:
                light_color = COLOR_BLUE;
                light_name = "BLUE (MAINT) ";
                break;
            default:
                light_color = COLOR_WHITE;
                light_name = "OFF          ";
                break;
        }
        
        printf(COLOR_YELLOW "║ " COLOR_RESET);
        printf(COLOR_WHITE "Track %d Signal: " COLOR_RESET, sig->track_id);
        printf("%s● %s" COLOR_RESET, light_color, light_name);
        printf("                                " COLOR_YELLOW "║\n" COLOR_RESET);
    }
    
    printf(COLOR_YELLOW "╚════════════════════════════════════════════════════════════════════╝\n" COLOR_RESET);
}

// Show system message
void board_show_system_message(const char *msg, int is_error) {
    printf("\n");
    if (is_error) {
        printf(COLOR_RED "┌────────────────────────────────────────────────────────────────────┐\n" COLOR_RESET);
        printf(COLOR_RED "│ [ERROR] %-58s │\n" COLOR_RESET, msg);
        printf(COLOR_RED "└────────────────────────────────────────────────────────────────────┘\n" COLOR_RESET);
    } else {
        printf(COLOR_GREEN "┌────────────────────────────────────────────────────────────────────┐\n" COLOR_RESET);
        printf(COLOR_GREEN "│ [INFO] %-59s │\n" COLOR_RESET, msg);
        printf(COLOR_GREEN "└────────────────────────────────────────────────────────────────────┘\n" COLOR_RESET);
    }
}

// Refresh entire board
void board_refresh(const TrackSystem *system, const LightSignal *signals) {
    board_clear_screen();
    board_show_header();
    board_show_tracks(system);
    board_show_light_status(signals, system->total_tracks);
    board_show_statistics(system);
}
