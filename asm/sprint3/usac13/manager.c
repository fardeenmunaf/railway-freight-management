// manager.c  (versão mínima para Sprint 3)

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include <signal.h>
#include <unistd.h>

#include "ui.h"   // Instruction / ui_*

// ----------------- Tipos comuns -----------------

typedef struct {
    float temperature;
    float humidity;
    bool  error;
} SensorData;

typedef struct {
    SensorData last;
    int        count;
} SensorsState;

typedef struct {
    char cmd[4];   // "RE", "YE", "GE", "RB"
    int  track;
} LightCmd;

// ----------------- Interface externa -----------------

void board_show_env(const SensorsState *s);
void lightsigns_send(const LightCmd *lc);
int  manager_get_filtered_sensors(SensorData *out);
void log_record(const char *user, const Instruction *inst);

// ----------------- Implementações simples -----------------

void board_show_env(const SensorsState *s) {
    printf("\n+------------------------------+\n");
    printf("|    ESTADO AMBIENTE ATUAL    |\n");
    printf("+------------------------------+\n");

    if (s->last.error) {
        printf("|   SENSOR INDISPONIVEL       |\n");
    } else {
        printf("| Temp: %5.1f C  Hum: %5.1f %% |\n",
               s->last.temperature, s->last.humidity);
    }
    printf("+------------------------------+\n");
}

void lightsigns_send(const LightCmd *lc) {
    printf("[LightSigns] CMD=%s, track=%d\n", lc->cmd, lc->track);
}

int manager_get_filtered_sensors(SensorData *out) {
    extern SensorData request_sensors(int fd);
    extern int  serial_fd;
    extern bool movingMedian(SensorData *out);

    // se a serial estiver fechada, não tenta ler
    if (serial_fd < 0) {
        out->error = true;
        return 0;
    }

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
    else if (inst->type == INST_EXIT) t = "EXIT";

    fprintf(f, "user=%s action=%s\n", user, t);
    fclose(f);
}

// ----------------- MAIN -----------------

volatile sig_atomic_t running = 1;

static void sig_handler(int sig) {
    (void)sig;
    running = 0;
}

int main(void) {
    extern int open_serial(const char *port);
    extern int serial_fd;

    signal(SIGINT, sig_handler);
    signal(SIGHUP, sig_handler);

    // abrir porta série
    serial_fd = open_serial("/dev/ttyACM0");
    if (serial_fd < 0) {
        fprintf(stderr, "Erro ao abrir porta série\n");
        return 1;
    }

    char user[32];
    if (!ui_login(user, sizeof user)) {
        printf("Login falhou.\n");
        close(serial_fd);
        serial_fd = -1;
        return 1;
    }

    SensorsState sensors = (SensorsState){0};
    LightCmd light = (LightCmd){0};

    while (running) {
        Instruction inst = ui_get_instruction();
        if (!running) break;

        if (inst.type == INST_EXIT) {
            log_record(user, &inst);
            break;
        }
        if (inst.type == INST_NONE) {
            printf("Instrucao invalida.\n");
            continue;
        }

        if (inst.type == INST_SHOW_ENV) {
            SensorData current = {0};

            printf("A recolher dados dos sensores...\n");

            // fica à espera até ter valor válido e temperatura > 0,
            // mas sai se running ficar a 0
            while (running) {
                manager_get_filtered_sensors(&current);

                if (!running) break;  // sinal recebido durante leitura

                if (!current.error && current.temperature > 0.0f) {
                    break;  // temos dados bons
                }

                if (!running) break;
                sleep(2);  // DHT11 precisa de 2 segundos entre leituras
            }

            if (!running) break;  // sai também do while principal

            sensors.last = current;
            if (!current.error) sensors.count++;

            // Decide comando simples para LightSigns com base na temperatura
            if (!current.error && current.temperature > 30.0f) {
                strcpy(light.cmd, "RE");
                light.track = 1;
            } else {
                strcpy(light.cmd, "GE");
                light.track = 1;
            }

            board_show_env(&sensors);
            lightsigns_send(&light);
            log_record(user, &inst);
        }
    }

    printf("\nManager encerrado.\n");
    if (serial_fd >= 0) {
        close(serial_fd);
        serial_fd = -1;
    }
    return 0;
}
