#include "board.h"
#include <stdio.h>
#include <string.h>
#include <time.h>
#include <unistd.h>

// Initialize board display
void board_init(void) {
    board_clear_screen();
    printf("\n");
    printf(COLOR_CYAN "╔════════════════════════════════════════════════════════════════════╗\n" COLOR_RESET);
    printf(COLOR_CYAN "║" COLOR_YELLOW "              RAILWAY SYNOPTIC BOARD SYSTEM                    " COLOR_CYAN "║\n" COLOR_RESET);
    printf(COLOR_CYAN "╚════════════════════════════════════════════════════════════════════╝\n" COLOR_RESET);
    printf("\n");
    // No delay needed here
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
    printf(COLOR_CYAN "│" COLOR_WHITE " Time: %02d:%02d:%02d                      Railway Control Center   " COLOR_CYAN "│\n" COLOR_RESET,
           t->tm_hour, t->tm_min, t->tm_sec);
    printf(COLOR_CYAN "└────────────────────────────────────────────────────────────────────┘\n" COLOR_RESET);
}

// Display environment sensor data with fancy formatting
void board_show_env(const SensorsState *s) {
    printf("\n");
    printf(COLOR_GREEN "╔════════════════════════════════════════════════════════════════════╗\n" COLOR_RESET);
    printf(COLOR_GREEN "║" COLOR_YELLOW "                     ENVIRONMENTAL STATUS                          " COLOR_GREEN "║\n" COLOR_RESET);
    printf(COLOR_GREEN "╠════════════════════════════════════════════════════════════════════╣\n" COLOR_RESET);
    
    if (s->last.error) {
        printf(COLOR_GREEN "║" COLOR_RED "                                                                    " COLOR_GREEN "║\n" COLOR_RESET);
        printf(COLOR_GREEN "║" COLOR_RED "             SENSOR ERROR - DATA UNAVAILABLE                       " COLOR_GREEN "║\n" COLOR_RESET);
        printf(COLOR_GREEN "║" COLOR_RED "                                                                    " COLOR_GREEN "║\n" COLOR_RESET);
    } else {
        // Temperature bar
        printf(COLOR_GREEN "║  " COLOR_WHITE "Temperature: " COLOR_RESET);
        if (s->last.temperature < 15.0) {
            printf(COLOR_BLUE "%5.1f°C  [" COLOR_RESET, s->last.temperature);
        } else if (s->last.temperature < 25.0) {
            printf(COLOR_GREEN "%5.1f°C  [" COLOR_RESET, s->last.temperature);
        } else if (s->last.temperature < 30.0) {
            printf(COLOR_YELLOW "%5.1f°C  [" COLOR_RESET, s->last.temperature);
        } else {
            printf(COLOR_RED "%5.1f°C  [" COLOR_RESET, s->last.temperature);
        }
        
        // Temperature visualization bar
        int tempBars = (int)((s->last.temperature / 40.0) * 20);
        for (int i = 0; i < 20; i++) {
            if (i < tempBars) {
                if (s->last.temperature < 15.0) printf(COLOR_BLUE "█" COLOR_RESET);
                else if (s->last.temperature < 25.0) printf(COLOR_GREEN "█" COLOR_RESET);
                else if (s->last.temperature < 30.0) printf(COLOR_YELLOW "█" COLOR_RESET);
                else printf(COLOR_RED "█" COLOR_RESET);
            } else {
                printf("░");
            }
        }
        printf("]  " COLOR_GREEN "║\n" COLOR_RESET);
        
        // Humidity bar
        printf(COLOR_GREEN "║  " COLOR_WHITE "Humidity:    " COLOR_RESET);
        if (s->last.humidity < 30.0) {
            printf(COLOR_YELLOW "%5.1f%%  [" COLOR_RESET, s->last.humidity);
        } else if (s->last.humidity < 70.0) {
            printf(COLOR_GREEN "%5.1f%%  [" COLOR_RESET, s->last.humidity);
        } else {
            printf(COLOR_CYAN "%5.1f%%  [" COLOR_RESET, s->last.humidity);
        }
        
        // Humidity visualization bar
        int humBars = (int)((s->last.humidity / 100.0) * 20);
        for (int i = 0; i < 20; i++) {
            if (i < humBars) {
                if (s->last.humidity < 30.0) printf(COLOR_YELLOW "█" COLOR_RESET);
                else if (s->last.humidity < 70.0) printf(COLOR_GREEN "█" COLOR_RESET);
                else printf(COLOR_CYAN "█" COLOR_RESET);
            } else {
                printf("░");
            }
        }
        printf("]  " COLOR_GREEN "║\n" COLOR_RESET);
        
        printf(COLOR_GREEN "║                                                                    ║\n" COLOR_RESET);
        printf(COLOR_GREEN "║  " COLOR_WHITE "Statistics:" COLOR_RESET "                                                      " COLOR_GREEN "║\n" COLOR_RESET);
        printf(COLOR_GREEN "║     • Total Readings: " COLOR_CYAN "%d" COLOR_RESET "                                         " COLOR_GREEN "║\n" COLOR_RESET, s->count);
        printf(COLOR_GREEN "║     • Average Temp:   " COLOR_CYAN "%.1f°C" COLOR_RESET "                                      " COLOR_GREEN "║\n" COLOR_RESET, s->avgTemp);
        printf(COLOR_GREEN "║     • Average Hum:    " COLOR_CYAN "%.1f%%" COLOR_RESET "                                       " COLOR_GREEN "║\n" COLOR_RESET, s->avgHum);
    }
    
    printf(COLOR_GREEN "╚════════════════════════════════════════════════════════════════════╝\n" COLOR_RESET);
}

// Display light signal status with fancy formatting
void board_show_light_status(const LightCmd *lc, const LightStats *stats) {
    printf("\n");
    printf(COLOR_MAGENTA "╔════════════════════════════════════════════════════════════════════╗\n" COLOR_RESET);
    printf(COLOR_MAGENTA "║" COLOR_YELLOW "                      TRACK LIGHT SIGNALS                          " COLOR_MAGENTA "║\n" COLOR_RESET);
    printf(COLOR_MAGENTA "╠════════════════════════════════════════════════════════════════════╣\n" COLOR_RESET);
    
    // Current command
    printf(COLOR_MAGENTA "║  " COLOR_WHITE "Current Command:" COLOR_RESET "                                                " COLOR_MAGENTA "║\n" COLOR_RESET);
    printf(COLOR_MAGENTA "║                                                                    ║\n" COLOR_RESET);
    printf(COLOR_MAGENTA "║    Track: " COLOR_CYAN "%d" COLOR_RESET "     Signal: ", lc->track);
    
    if (strcmp(lc->cmd, "RE") == 0) {
        printf(COLOR_RED "RED LIGHT - STOP" COLOR_RESET "                            " COLOR_MAGENTA "║\n" COLOR_RESET);
    } else if (strcmp(lc->cmd, "YE") == 0) {
        printf(COLOR_YELLOW "YELLOW LIGHT - CAUTION" COLOR_RESET "                      " COLOR_MAGENTA "║\n" COLOR_RESET);
    } else if (strcmp(lc->cmd, "GE") == 0) {
        printf(COLOR_GREEN "GREEN LIGHT - GO" COLOR_RESET "                             " COLOR_MAGENTA "║\n" COLOR_RESET);
    } else if (strcmp(lc->cmd, "RB") == 0) {
        printf(COLOR_BLUE "BLUE LIGHT - MAINTENANCE" COLOR_RESET "                    " COLOR_MAGENTA "║\n" COLOR_RESET);
    }
    
    printf(COLOR_MAGENTA "║                                                                    ║\n" COLOR_RESET);
    printf(COLOR_MAGENTA "║  " COLOR_WHITE "Signal Statistics:" COLOR_RESET "                                             " COLOR_MAGENTA "║\n" COLOR_RESET);
    printf(COLOR_MAGENTA "║     • Total Commands:    " COLOR_CYAN "%4d" COLOR_RESET "                                    " COLOR_MAGENTA "║\n" COLOR_RESET, stats->totalCommands);
    printf(COLOR_MAGENTA "║     • Red Lights:        " COLOR_RED "%4d" COLOR_RESET "                                      " COLOR_MAGENTA "║\n" COLOR_RESET, stats->redLights);
    printf(COLOR_MAGENTA "║     • Yellow Lights:     " COLOR_YELLOW "%4d" COLOR_RESET "                                      " COLOR_MAGENTA "║\n" COLOR_RESET, stats->yellowLights);
    printf(COLOR_MAGENTA "║     • Green Lights:      " COLOR_GREEN "%4d" COLOR_RESET "                                      " COLOR_MAGENTA "║\n" COLOR_RESET, stats->greenLights);
    printf(COLOR_MAGENTA "║     • Blue Lights:       " COLOR_BLUE "%4d" COLOR_RESET "                                      " COLOR_MAGENTA "║\n" COLOR_RESET, stats->blueLights);
    printf(COLOR_MAGENTA "╚════════════════════════════════════════════════════════════════════╝\n" COLOR_RESET);
}

// Display system status messages
void board_show_system_status(const char *message, bool isError) {
    printf("\n");
    if (isError) {
        printf(COLOR_RED "╔════════════════════════════════════════════════════════════════════╗\n" COLOR_RESET);
        printf(COLOR_RED "║  ERROR: %-60s ║\n" COLOR_RESET, message);
        printf(COLOR_RED "╚════════════════════════════════════════════════════════════════════╝\n" COLOR_RESET);
    } else {
        printf(COLOR_GREEN "╔════════════════════════════════════════════════════════════════════╗\n" COLOR_RESET);
        printf(COLOR_GREEN "║  INFO: %-61s ║\n" COLOR_RESET, message);
        printf(COLOR_GREEN "╚════════════════════════════════════════════════════════════════════╝\n" COLOR_RESET);
    }
}

// Display footer
void board_show_footer(void) {
    printf("\n");
    printf(COLOR_CYAN "┌────────────────────────────────────────────────────────────────────┐\n" COLOR_RESET);
    printf(COLOR_CYAN "│" COLOR_WHITE "  Press [S] Show Env | [L] Control Lights | [E] Exit             " COLOR_CYAN "│\n" COLOR_RESET);
    printf(COLOR_CYAN "└────────────────────────────────────────────────────────────────────┘\n" COLOR_RESET);
    printf(COLOR_RESET);
}

// Display a fancy box with custom content
void board_display_fancy_box(const char *title, const char *content, const char *color) {
    printf("\n");
    printf("%s╔════════════════════════════════════════════════════════════════════╗\n" COLOR_RESET, color);
    printf("%s║  %-65s ║\n" COLOR_RESET, color, title);
    printf("%s╠════════════════════════════════════════════════════════════════════╣\n" COLOR_RESET, color);
    printf("%s║  %-65s ║\n" COLOR_RESET, color, content);
    printf("%s╚════════════════════════════════════════════════════════════════════╝\n" COLOR_RESET, color);
}
