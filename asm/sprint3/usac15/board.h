#ifndef BOARD_H
#define BOARD_H

#include <stdbool.h>

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
#define BG_BLUE       "\033[44m"

// Icons and symbols
#define ICON_TEMP     "[TEMP] "
#define ICON_HUMIDITY "[HUM]"
#define ICON_WARNING  "[WARN] "
#define ICON_ERROR    "[ERR]"
#define ICON_OK       "[OK]"
#define ICON_TRAIN    "[TRAIN]"
#define ICON_LIGHT    "[LIGHT]"
#define ICON_INFO     "[INFO] "

// Data structures
typedef struct {
    float temperature;
    float humidity;
    bool  error;
} SensorData;

typedef struct {
    SensorData last;
    int        count;
    int        readingsToday;
    float      avgTemp;
    float      avgHum;
} SensorsState;

typedef struct {
    char cmd[4];   // "RE", "YE", "GE", "RB"
    int  track;
} LightCmd;

typedef struct {
    int totalCommands;
    int redLights;
    int yellowLights;
    int greenLights;
    int blueLights;
} LightStats;

// Board display functions
void board_init(void);
void board_clear_screen(void);
void board_show_header(void);
void board_show_env(const SensorsState *s);
void board_show_light_status(const LightCmd *lc, const LightStats *stats);
void board_show_system_status(const char *message, bool isError);
void board_show_footer(void);
void board_display_fancy_box(const char *title, const char *content, const char *color);

#endif // BOARD_H
