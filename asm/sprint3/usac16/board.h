#ifndef BOARD_H
#define BOARD_H

#include "tracks.h"
#include "lightsigns.h"

// Color codes for terminal
#define COLOR_RESET   "\033[0m"
#define COLOR_RED     "\033[1;31m"
#define COLOR_GREEN   "\033[1;32m"
#define COLOR_YELLOW  "\033[1;33m"
#define COLOR_BLUE    "\033[1;34m"
#define COLOR_MAGENTA "\033[1;35m"
#define COLOR_CYAN    "\033[1;36m"
#define COLOR_WHITE   "\033[1;37m"
#define BG_BLACK      "\033[40m"
#define BG_RED        "\033[41m"
#define BG_GREEN      "\033[42m"
#define BG_YELLOW     "\033[43m"
#define BG_BLUE       "\033[44m"

// Board functions
void board_init(void);
void board_clear_screen(void);
void board_show_header(void);
void board_show_tracks(const TrackSystem *system);
void board_show_statistics(const TrackSystem *system);
void board_show_light_status(const LightSignal *signals, int num_tracks);
void board_show_system_message(const char *msg, int is_error);
void board_refresh(const TrackSystem *system, const LightSignal *signals);

#endif // BOARD_H
