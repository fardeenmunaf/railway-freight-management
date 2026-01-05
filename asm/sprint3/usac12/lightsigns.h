#ifndef LIGHTSIGNS_H
#define LIGHTSIGNS_H

#include <stdbool.h>

// Estrutura para controlar LEDs
typedef struct
{
    int led_id; // ID do LED (1-10)
    int state;  // Estado: 0=OFF, 1=ON
    int count;  // Contador de LEDs ligados
} LedControl;

// Função em assembly - liga/desliga LED e atualiza contador
// Parâmetro: ponteiro para struct LedControl
// Retorna: número de LEDs atualmente ligados
extern int control_led(LedControl *led);

// Função em C - liga todos os LEDs de uma vez
void turn_on_all_lights(void);

#endif
