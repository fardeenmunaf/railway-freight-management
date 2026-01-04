#define _DEFAULT_SOURCE
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include "lightsigns.h"

// Declaração externa do file descriptor serial
extern int serial_fd;

// Função para enviar comando ao Arduino via serial
static void send_led_command(int led_id, int state)
{
    if (serial_fd < 0)
    {
        printf("  [Simulação] LED %d %s (sem hardware conectado)\n",
               led_id, state ? "ON" : "OFF");
        return;
    }

    char cmd[16];
    // Comando: "LED:<id>:<state>\n"
    // Exemplo: "LED:1:1\n" liga LED 1, "LED:1:0\n" desliga LED 1
    snprintf(cmd, sizeof(cmd), "LED:%d:%d\n", led_id, state);

    ssize_t w = write(serial_fd, cmd, strlen(cmd));
    if (w < 0)
    {
        perror("  [Erro] write led command");
    }
    else
    {
        printf("  [Hardware] Comando enviado: LED %d %s\n",
               led_id, state ? "ON" : "OFF");
        // Pequeno delay para Arduino processar
        usleep(10000); // 10ms
    }
}

// Função para ligar todos os LEDs
void turn_on_all_lights(void)
{
    printf("\n========================================\n");
    printf("===  TESTE DE LEDS (USAC14)          ===\n");
    printf("========================================\n");

    if (serial_fd < 0)
    {
        printf("ERRO: Porta serial não está aberta!\n");
        return;
    }

    printf("Porta serial OK (fd=%d)\n\n", serial_fd);

    LedControl led = {0};

    // Teste 1: LED VERMELHO (5 segundos)
    printf(">>> TESTE 1: LED VERMELHO <<<\n");
    led.led_id = 1;
    led.state = 1;
    control_led(&led);

    printf("    Enviando: RE (2 bytes) + \\n (1 byte)\n");
    write(serial_fd, "RE", 2);
    write(serial_fd, "\n", 1);
    fsync(serial_fd);
    printf("    >>> OLHE O LED VERMELHO! <<<\n");
    printf("    Aguardando 5 segundos...\n");
    sleep(5);

    // Desligar LED vermelho
    printf("    Desligando LED vermelho...\n\n");
    write(serial_fd, "RB", 2);
    write(serial_fd, "\n", 1);
    fsync(serial_fd);
    sleep(1);

    // Teste 2: LED AMARELO (5 segundos)
    printf(">>> TESTE 2: LED AMARELO <<<\n");
    led.led_id = 2;
    led.state = 1;
    control_led(&led);

    printf("    Enviando: YE (2 bytes) + \\n (1 byte)\n");
    write(serial_fd, "YE", 2);
    write(serial_fd, "\n", 1);
    fsync(serial_fd);
    printf("    >>> OLHE O LED AMARELO! <<<\n");
    printf("    Aguardando 5 segundos...\n");
    sleep(5);

    // Desligar LED amarelo
    printf("    Desligando LED amarelo...\n\n");
    write(serial_fd, "YB", 2);
    write(serial_fd, "\n", 1);
    fsync(serial_fd);
    sleep(1);

    // Teste 3: LED VERDE (5 segundos)
    printf(">>> TESTE 3: LED VERDE <<<\n");
    led.led_id = 3;
    led.state = 1;
    control_led(&led);

    printf("    Enviando: GE (2 bytes) + \\n (1 byte)\n");
    write(serial_fd, "GE", 2);
    write(serial_fd, "\n", 1);
    fsync(serial_fd);
    printf("    >>> OLHE O LED VERDE! <<<\n");
    printf("    Aguardando 5 segundos...\n");
    sleep(5);

    // Desligar LED verde
    printf("    Desligando LED verde...\n\n");
    write(serial_fd, "GB", 2);
    write(serial_fd, "\n", 1);
    fsync(serial_fd);
    sleep(1);

    printf("========================================\n");
    printf("Teste completo! LEDs testados: %d\n", led.count);
    printf("========================================\n");
}
