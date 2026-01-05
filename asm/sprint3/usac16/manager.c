// manager.c - USAC16 - Railway Track Management System

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include <signal.h>
#include <unistd.h>
#include <time.h>

#include "tracks.h"
#include "board.h"
#include "lightsigns.h"
#include "ui.h"

// Configuration
#define NUM_TRACKS 6
#define LOG_FILE "track_manager.log"
#define UPDATE_INTERVAL 1  // seconds

// Global state
static TrackSystem trackSystem;
static LightSignal lightSignals[MAX_TRACKS];
static volatile bool running = true;
static char current_user[50] = "Unknown";

// Serial port file descriptor (from sensors_linux.c)
extern int serial_fd;

// Forward declarations
void signal_handler(int sig);
void log_operation(const char *user, const char *operation, const char *details);
void process_instruction(const UserInstruction *inst);
void update_system(void);
int open_serial_port(const char *port_name);

// Signal handler for graceful shutdown
void signal_handler(int sig) {
    (void)sig;
    running = false;
    printf("\n\n[System] Shutdown signal received...\n");
}

// Log operation to file
void log_operation(const char *user, const char *operation, const char *details) {
    FILE *fp = fopen(LOG_FILE, "a");
    if (fp == NULL) {
        fprintf(stderr, "[Warning] Could not open log file\n");
        return;
    }
    
    time_t now = time(NULL);
    struct tm *t = localtime(&now);
    
    fprintf(fp, "[%04d-%02d-%02d %02d:%02d:%02d] User: %-15s | Operation: %-25s | %s\n",
            t->tm_year + 1900, t->tm_mon + 1, t->tm_mday,
            t->tm_hour, t->tm_min, t->tm_sec,
            user, operation, details);
    
    fclose(fp);
}

// Process user instruction
void process_instruction(const UserInstruction *inst) {
    char log_details[256];
    int result;
    
    switch (inst->type) {
        case OP_ASSIGN_TRACK:
            if (strlen(inst->train_id) == 0) {
                board_show_system_message("Error: Train ID cannot be empty", 1);
                break;
            }
            
            // Call assembly function to assign track
            result = assign_track_asm(&trackSystem, inst->train_id);
            
            if (result > 0) {
                // Track assigned successfully
                snprintf(log_details, sizeof(log_details), 
                        "Train %s assigned to track %d", inst->train_id, result);
                log_operation(current_user, "ASSIGN_TRACK", log_details);
                
                // Update light signals
                lightsigns_update_all(lightSignals, &trackSystem);
                
                snprintf(log_details, sizeof(log_details), 
                        "Train %s successfully assigned to Track %d", inst->train_id, result);
                board_show_system_message(log_details, 0);
            } else {
                // Emergency stop - no tracks available
                snprintf(log_details, sizeof(log_details), 
                        "EMERGENCY STOP issued for train %s - no tracks available", inst->train_id);
                log_operation(current_user, "EMERGENCY_STOP", log_details);
                
                board_show_system_message("EMERGENCY STOP - No tracks available!", 1);
            }
            break;
            
        case OP_SET_NON_OPERATIONAL:
            result = tracks_set_non_operational(&trackSystem, inst->track_id);
            
            if (result == 0) {
                snprintf(log_details, sizeof(log_details), 
                        "Track %d set as non-operational", inst->track_id);
                log_operation(current_user, "SET_NON_OPERATIONAL", log_details);
                
                // Update light signals
                lightsigns_update_all(lightSignals, &trackSystem);
                
                snprintf(log_details, sizeof(log_details), 
                        "Track %d successfully set as non-operational", inst->track_id);
                board_show_system_message(log_details, 0);
            } else {
                snprintf(log_details, sizeof(log_details), 
                        "Failed to set track %d as non-operational", inst->track_id);
                board_show_system_message(log_details, 1);
            }
            break;
            
        case OP_SET_FREE:
            result = tracks_set_free(&trackSystem, inst->track_id);
            
            if (result == 0) {
                snprintf(log_details, sizeof(log_details), 
                        "Track %d set as free", inst->track_id);
                log_operation(current_user, "SET_FREE", log_details);
                
                // Update light signals
                lightsigns_update_all(lightSignals, &trackSystem);
                
                snprintf(log_details, sizeof(log_details), 
                        "Track %d successfully set as free", inst->track_id);
                board_show_system_message(log_details, 0);
            } else {
                snprintf(log_details, sizeof(log_details), 
                        "Failed to set track %d as free", inst->track_id);
                board_show_system_message(log_details, 1);
            }
            break;
            
        case OP_GIVE_DEPARTURE:
            result = tracks_give_departure(&trackSystem, inst->track_id);
            
            if (result == 0) {
                Track *track = tracks_get_track(&trackSystem, inst->track_id);
                if (track) {
                    snprintf(log_details, sizeof(log_details), 
                            "Departure order given for track %d", inst->track_id);
                } else {
                    snprintf(log_details, sizeof(log_details), 
                            "Departure order given for track %d", inst->track_id);
                }
                log_operation(current_user, "GIVE_DEPARTURE", log_details);
                
                // Update light signals
                lightsigns_update_all(lightSignals, &trackSystem);
                
                snprintf(log_details, sizeof(log_details), 
                        "Departure order successfully issued for Track %d", inst->track_id);
                board_show_system_message(log_details, 0);
            } else {
                snprintf(log_details, sizeof(log_details), 
                        "Failed to issue departure order for track %d", inst->track_id);
                board_show_system_message(log_details, 1);
            }
            break;
            
        case OP_REFRESH_DISPLAY:
            log_operation(current_user, "REFRESH_DISPLAY", "Display refreshed");
            board_show_system_message("Display refreshed", 0);
            break;
            
        case OP_EXIT:
            log_operation(current_user, "EXIT", "User logged out");
            running = false;
            printf(COLOR_GREEN "\n[System] Shutting down gracefully...\n" COLOR_RESET);
            break;
            
        default:
            break;
    }
}

// Update system state (called periodically)
void update_system(void) {
    // Update occupied times
    tracks_update_times(&trackSystem);
    
    // Update light signals
    lightsigns_update_all(lightSignals, &trackSystem);
}

// Main function
int main(void) {
    // Setup signal handler
    signal(SIGINT, signal_handler);
    
    // Initialize board
    board_init();
    
    // Try to open serial port (for hardware integration)
    const char *ports[] = {
        "/dev/ttyACM0",
        "/dev/ttyACM1", 
        "/dev/ttyUSB0",
        "/dev/ttyUSB1",
        "/dev/ttyS0",
        "/dev/serial0",
        NULL
    };
    
    printf("\n[System] Searching for serial port...\n");
    for (int i = 0; ports[i] != NULL; i++) {
        int fd = open_serial_port(ports[i]);
        if (fd >= 0) {
            serial_fd = fd;
            printf(COLOR_GREEN "[System] Serial port %s opened successfully (fd=%d)\n" COLOR_RESET, ports[i], serial_fd);
            printf(COLOR_GREEN "[System] Hardware integration enabled\n" COLOR_RESET);
            break;
        }
    }
    
    if (serial_fd < 0) {
        printf(COLOR_YELLOW "[System] No serial port available\n" COLOR_RESET);
        printf(COLOR_YELLOW "[System] Running in SIMULATION MODE\n" COLOR_RESET);
    }
    
    sleep(2);
    
    // Login
    if (ui_login(current_user, sizeof(current_user)) != 0) {
        fprintf(stderr, "Login failed!\n");
        if (serial_fd >= 0) close(serial_fd);
        return 1;
    }
    
    // Log login
    log_operation(current_user, "LOGIN", "User logged in");
    
    // Initialize track system
    tracks_init(&trackSystem, NUM_TRACKS);
    
    // Initialize light signals
    lightsigns_init(lightSignals, NUM_TRACKS);
    
    // Initial display
    board_refresh(&trackSystem, lightSignals);
    
    printf(COLOR_GREEN "\n[System] Track Management System ready!\n" COLOR_RESET);
    printf(COLOR_CYAN "Press Ctrl+C to exit gracefully\n\n" COLOR_RESET);
    
    sleep(2);
    if (serial_fd >= 0) {
        close(serial_fd);
        printf(COLOR_YELLOW "\n[System] Serial port closed\n" COLOR_RESET);
    }
    
    
    // Main loop
    time_t last_update = time(NULL);
    
    while (running) {
        // Get user instruction
        UserInstruction inst = ui_get_instruction();
        
        if (!running) break;
        
        // Process instruction
        if (inst.type != OP_NONE) {
            process_instruction(&inst);
            
            // Refresh display after operation
            if (inst.type != OP_EXIT) {
                usleep(500000);  // 0.5 second pause to show message
                board_refresh(&trackSystem, lightSignals);
            }
        }
        
        // Periodic updates
        time_t now = time(NULL);
        if (now - last_update >= UPDATE_INTERVAL) {
            update_system();
            last_update = now;
        }
    }
    
    // Cleanup
    printf("\n");
    printf(COLOR_CYAN "════════════════════════════════════════════════════════════════════\n" COLOR_RESET);
    printf(COLOR_CYAN "                    SYSTEM SHUTDOWN COMPLETE\n" COLOR_RESET);
    printf(COLOR_CYAN "════════════════════════════════════════════════════════════════════\n" COLOR_RESET);
    printf("\n");
    printf(COLOR_WHITE "Final Statistics:\n" COLOR_RESET);
    printf("  Total Arrivals:   %d\n", trackSystem.total_arrivals);
    printf("  Total Departures: %d\n", trackSystem.total_departures);
    printf("  Emergency Stops:  %d\n", trackSystem.emergency_stops);
    printf("\n");
    printf(COLOR_GREEN "Thank you for using the Track Management System, %s!\n" COLOR_RESET, current_user);
    printf("\n");
    
    log_operation(current_user, "SHUTDOWN", "System shutdown complete");
    
    return 0;
}
