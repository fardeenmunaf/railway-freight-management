// manager.c - USAC15 - Synoptic Board Display Component

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include <signal.h>
#include <unistd.h>

#include "ui.h"
#include "board.h"

// ----------------- Global state -----------------

static SensorsState sensorsState = {
    .last = {0.0, 0.0, true},
    .count = 0,
    .readingsToday = 0,
    .avgTemp = 0.0,
    .avgHum = 0.0
};

static LightStats lightStats = {
    .totalCommands = 0,
    .redLights = 0,
    .yellowLights = 0,
    .greenLights = 0,
    .blueLights = 0
};

static LightCmd lastLightCmd = {"RE", 1};

static volatile bool running = true;
extern int serial_fd;

// ----------------- Forward declarations -----------------

void lightsigns_send(const LightCmd *lc);
int manager_get_filtered_sensors(SensorData *out);
void log_record(const char *user, const Instruction *inst);

// ----------------- Signal handler -----------------

void signal_handler(int sig) {
    (void)sig;
    running = false;
}

// ----------------- Helper functions -----------------

void update_sensor_statistics(const SensorData *data) {
    if (data->error) return;
    
    sensorsState.count++;
    sensorsState.readingsToday++;
    
    // Update running averages
    float n = (float)sensorsState.count;
    sensorsState.avgTemp = ((sensorsState.avgTemp * (n - 1)) + data->temperature) / n;
    sensorsState.avgHum = ((sensorsState.avgHum * (n - 1)) + data->humidity) / n;
}

void update_light_statistics(const LightCmd *lc) {
    lightStats.totalCommands++;
    
    if (strcmp(lc->cmd, "RE") == 0) {
        lightStats.redLights++;
    } else if (strcmp(lc->cmd, "YE") == 0) {
        lightStats.yellowLights++;
    } else if (strcmp(lc->cmd, "GE") == 0) {
        lightStats.greenLights++;
    } else if (strcmp(lc->cmd, "RB") == 0) {
        lightStats.blueLights++;
    }
}

// ----------------- Implementation -----------------

void lightsigns_send(const LightCmd *lc) {
    // Update last command
    memcpy(&lastLightCmd, lc, sizeof(LightCmd));
    
    // Update statistics
    update_light_statistics(lc);
    
    printf("\n[LightSigns] CMD=%s, track=%d, serial_fd=%d\n", lc->cmd, lc->track, serial_fd);
    
    // Send to hardware if available
    if (serial_fd >= 0) {
        write(serial_fd, lc->cmd, 2);  // "RE", "YE", "GE" (2 bytes)
        write(serial_fd, "\n", 1);      // newline
        fsync(serial_fd);
        printf("[Hardware] Comando %s enviado ao Pico W\n", lc->cmd);
        board_show_system_status("Command sent to hardware", false);
    } else {
        printf("[Simulação] Sem hardware conectado\n");
        board_show_system_status("Simulation mode - no hardware connected", false);
    }
    
    // Display on board
    board_show_light_status(lc, &lightStats);
}

int manager_get_filtered_sensors(SensorData *out) {
    extern SensorData request_sensors(int fd);
    extern bool movingMedian(SensorData *out);
    
    // Get sensor data (real hardware or simulation)
    SensorData sd = request_sensors(serial_fd);
    
    extern SensorData buffer[];
    extern int bufIndex;
    extern int bufCount;
    extern const int BUFFER_SIZE_CONST;
    
    buffer[bufIndex] = sd;
    bufIndex = (bufIndex + 1) % BUFFER_SIZE_CONST;
    if (bufCount < BUFFER_SIZE_CONST) bufCount++;
    
    SensorData med;
    if (movingMedian(&med) && !med.error) {
        *out = med;
        update_sensor_statistics(out);
        return 1;
    } else {
        out->error = true;
        return 0;
    }
}

void log_record(const char *user, const Instruction *inst) {
    FILE *f = fopen("manager.log", "a");
    if (!f) return;
    
    const char *t = "NONE";
    if (inst->type == INST_SHOW_ENV) t = "SHOW_ENV";
    else if (inst->type == INST_CONTROL_LIGHTS) t = "CONTROL_LIGHTS";
    else if (inst->type == INST_EXIT) t = "EXIT";
    
    fprintf(f, "user=%s action=%s", user, t);
    if (inst->type == INST_CONTROL_LIGHTS) {
        fprintf(f, " track=%d color=%s", inst->track, inst->color);
    }
    fprintf(f, "\n");
    fclose(f);
}

// ----------------- Main loop -----------------

int main(void) {
    signal(SIGINT, signal_handler);
    signal(SIGTERM, signal_handler);
    
    // Initialize board display
    board_init();
    
    // FIRST: Initialize serial port (BEFORE login, like usac14)
    extern int open_serial_port(const char *);
    const char *ports[] = {
        "/dev/ttyACM0",
        "/dev/ttyACM1", 
        "/dev/ttyUSB0",
        "/dev/ttyUSB1",
        "/dev/ttyS0",
        "/dev/serial0",
        NULL
    };
    
    printf("Procurando porta serial...\n");
    for (int i = 0; ports[i] != NULL; i++) {
        int fd = open_serial_port(ports[i]);
        if (fd >= 0) {
            serial_fd = fd;
            printf("\n*** Porta serial %s ABERTA com sucesso (fd=%d)! ***\n", ports[i], serial_fd);
            printf("*** LEDs físicos FUNCIONARÃO ***\n\n");
            break;
        }
    }
    
    if (serial_fd < 0) {
        printf("\n*** AVISO: Nenhuma porta serial disponível ***\n");
        printf("*** Continuando em MODO SIMULAÇÃO ***\n\n");
    }
    
    sleep(2);  // Give user time to read
    
    // Login
    char username[64];
    if (!ui_login(username, sizeof(username))) {
        board_show_system_status("Login failed", true);
        if (serial_fd >= 0) close(serial_fd);
        return 1;
    }
    
    // Main loop
    while (running) {
        board_clear_screen();
        board_show_header();
        
        Instruction inst = ui_get_instruction();
        log_record(username, &inst);
        
        switch (inst.type) {
            case INST_SHOW_ENV: {
                SensorData sd;
                if (manager_get_filtered_sensors(&sd)) {
                    sensorsState.last = sd;
                    board_show_env(&sensorsState);
                } else {
                    sensorsState.last.error = true;
                    board_show_env(&sensorsState);
                }
                
                printf(COLOR_YELLOW "\nPress Enter to continue..." COLOR_RESET);
                getchar();
                break;
            }
            
            case INST_CONTROL_LIGHTS: {
                LightCmd lc;
                lc.track = inst.track;
                strncpy(lc.cmd, inst.color, sizeof(lc.cmd) - 1);
                lc.cmd[sizeof(lc.cmd) - 1] = '\0';
                
                lightsigns_send(&lc);
                
                printf(COLOR_YELLOW "\nPress Enter to continue..." COLOR_RESET);
                getchar();
                break;
            }
            
            case INST_EXIT:
                running = false;
                board_show_system_status("Shutting down system...", false);
                break;
                
            case INST_NONE:
            default:
                break;
        }
    }
    
    // Cleanup
    if (serial_fd >= 0) {
        close(serial_fd);
    }
    
    printf(COLOR_GREEN "\n\nSystem shutdown complete. Goodbye, %s!\n\n" COLOR_RESET, username);
    return 0;
}
