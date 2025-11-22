#include <stdio.h>
#include <string.h>

// Declaração da função em Assembly RISC-V
extern int enqueue_value(int* buffer, int length, int* nelem, int* tail, int* head, int value);

// Função auxiliar para imprimir o estado do buffer
void print_buffer_state(int* buffer, int length, int nelem, int tail, int head) {
    printf("\n=== Estado do Buffer ===\n");
    printf("Capacidade: %d\n", length);
    printf("Elementos:  %d\n", nelem);
    printf("Tail:       %d\n", tail);
    printf("Head:       %d\n", head);
    printf("Buffer:     [");
    for (int i = 0; i < length; i++) {
        printf("%d", buffer[i]);
        if (i < length - 1) printf(", ");
    }
    printf("]\n");

    // Mostrar ordem de leitura (do tail ao head)
    if (nelem > 0) {
        printf("Ordem:      [");
        int count = 0;
        int idx = tail;
        while (count < nelem) {
            printf("%d", buffer[idx]);
            if (count < nelem - 1) printf(", ");
            idx = (idx + 1) % length;
            count++;
        }
        printf("] (tail->head)\n");
    }
    printf("========================\n");
}

int main(void) {
    // Configuração do buffer circular
    const int BUFFER_SIZE = 5;
    int buffer[BUFFER_SIZE];
    int nelem = 0;  // Número de elementos no buffer
    int tail = 0;   // Índice do elemento mais antigo
    int head = 0;   // Índice onde inserir o próximo elemento
    int result;

    // Inicializar buffer com zeros
    memset(buffer, 0, sizeof(buffer));

    printf("=== Teste do Circular Buffer (Ring Buffer) em RISC-V ===\n");
    printf("Capacidade do buffer: %d elementos\n", BUFFER_SIZE);

    print_buffer_state(buffer, BUFFER_SIZE, nelem, tail, head);

    // Teste 1: Inserir elementos sem encher o buffer
    printf("\n--- Teste 1: Inserir valores 10, 20, 30 ---\n");
    for (int i = 1; i <= 3; i++) {
        int value = i * 10;
        result = enqueue_value(buffer, BUFFER_SIZE, &nelem, &tail, &head, value);
        printf("Inserido %d, buffer %s\n", value, result ? "CHEIO" : "não cheio");
    }
    print_buffer_state(buffer, BUFFER_SIZE, nelem, tail, head);

    // Teste 2: Encher completamente o buffer
    printf("\n--- Teste 2: Encher o buffer (inserir 40, 50) ---\n");
    for (int i = 4; i <= 5; i++) {
        int value = i * 10;
        result = enqueue_value(buffer, BUFFER_SIZE, &nelem, &tail, &head, value);
        printf("Inserido %d, buffer %s\n", value, result ? "CHEIO" : "não cheio");
    }
    print_buffer_state(buffer, BUFFER_SIZE, nelem, tail, head);

    // Teste 3: Sobrescrever elementos (ring buffer behavior)
    printf("\n--- Teste 3: Sobrescrever elementos antigos (inserir 60, 70, 80) ---\n");
    for (int i = 6; i <= 8; i++) {
        int value = i * 10;
        printf("Antes de inserir %d:\n", value);
        result = enqueue_value(buffer, BUFFER_SIZE, &nelem, &tail, &head, value);
        printf("Inserido %d (sobrescreveu posição), buffer %s\n", value, result ? "CHEIO" : "não cheio");
    }
    print_buffer_state(buffer, BUFFER_SIZE, nelem, tail, head);

    // Teste 4: Modo interativo
    printf("\n--- Teste 4: Modo Interativo ---\n");
    printf("Insira valores (0 para sair):\n");

    int user_value;
    while (1) {
        printf("\nValor a inserir: ");
        if (scanf("%d", &user_value) != 1) {
            printf("Entrada inválida!\n");
            break;
        }

        if (user_value == 0) {
            printf("Encerrando...\n");
            break;
        }

        result = enqueue_value(buffer, BUFFER_SIZE, &nelem, &tail, &head, user_value);
        printf("Inserido %d, buffer %s\n", user_value, result ? "CHEIO" : "não cheio");
        print_buffer_state(buffer, BUFFER_SIZE, nelem, tail, head);
    }

    printf("\n=== Teste Concluído ===\n");
    return 0;
}                                                                                                                     