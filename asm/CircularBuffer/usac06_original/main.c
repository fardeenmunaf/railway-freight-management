#include <stdio.h>
#include <string.h>

// Declaração da função em Assembly RISC-V
extern int dequeue_value(int* buffer, int length, int* nelem, int* tail, int* head, int* value);

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
    int buffer[5] = {10, 20, 30, 40, 50};  // Buffer pré-preenchido
    int nelem = 5;  // Número de elementos no buffer (inicialmente cheio)
    int tail = 0;   // Índice do elemento mais antigo
    int head = 0;   // Índice onde inserir o próximo elemento
    int value = 0;
    int result;

    printf("=== Teste do Dequeue do Circular Buffer (Ring Buffer) em RISC-V ===\n");
    printf("Capacidade do buffer: %d elementos\n", 5);

    print_buffer_state(buffer, 5, nelem, tail, head);

    // Teste 1: Remover elementos do buffer cheio
    printf("\n--- Teste 1: Remover 3 elementos ---\n");
    for (int i = 1; i <= 3; i++) {
        result = dequeue_value(buffer, 5, &nelem, &tail, &head, &value);
        if (result) {
            printf("Removido: %d (sucesso)\n", value);
        } else {
            printf("Falha ao remover (buffer vazio)\n");
        }
    }
    print_buffer_state(buffer, 5, nelem, tail, head);

    // Teste 2: Remover elementos restantes
    printf("\n--- Teste 2: Remover todos os elementos restantes ---\n");
    while (nelem > 0) {
        result = dequeue_value(buffer, 5, &nelem, &tail, &head, &value);
        if (result) {
            printf("Removido: %d (sucesso)\n", value);
        } else {
            printf("Falha ao remover (buffer vazio)\n");
            break;
        }
    }
    print_buffer_state(buffer, 5, nelem, tail, head);

    // Teste 3: Tentar remover de buffer vazio
    printf("\n--- Teste 3: Tentar remover de buffer vazio ---\n");
    result = dequeue_value(buffer, 5, &nelem, &tail, &head, &value);
    if (result) {
        printf("Removido: %d (sucesso)\n", value);
    } else {
        printf("Falha ao remover (buffer vazio) - CORRETO!\n");
    }
    print_buffer_state(buffer, 5, nelem, tail, head);

    // Teste 4: Buffer com wrap-around
    printf("\n--- Teste 4: Buffer com wrap-around ---\n");
    int buffer2[5] = {0, 0, 100, 200, 300};
    int nelem2 = 3;
    int tail2 = 2;
    int head2 = 0;

    printf("\nBuffer com wrap-around (tail=2, head=0):\n");
    print_buffer_state(buffer2, 5, nelem2, tail2, head2);

    for (int i = 0; i < 4; i++) {
        result = dequeue_value(buffer2, 5, &nelem2, &tail2, &head2, &value);
        if (result) {
            printf("Removido: %d (sucesso)\n", value);
        } else {
            printf("Falha ao remover (buffer vazio)\n");
        }
    }
    print_buffer_state(buffer2, 5, nelem2, tail2, head2);

    // Teste 5: Modo interativo
    printf("\n--- Teste 5: Modo Interativo ---\n");
    printf("Criando novo buffer com alguns elementos...\n");

    int buffer3[5] = {999, 888, 777, 666, 555};
    int nelem3 = 5;
    int tail3 = 0;
    int head3 = 0;

    print_buffer_state(buffer3, 5, nelem3, tail3, head3);
    printf("\nPressione ENTER para remover elementos (0 para sair):\n");

    int user_input;
    while (1) {
        printf("\nRemover próximo elemento? (1=sim, 0=não): ");
        if (scanf("%d", &user_input) != 1) {
            printf("Entrada inválida!\n");
            break;
        }

        if (user_input == 0) {
            printf("Encerrando...\n");
            break;
        }

        result = dequeue_value(buffer3, 5, &nelem3, &tail3, &head3, &value);
        if (result) {
            printf("Removido: %d (sucesso)\n", value);
        } else {
            printf("Falha ao remover (buffer vazio)\n");
        }
        print_buffer_state(buffer3, 5, nelem3, tail3, head3);
    }

    printf("\n=== Teste Concluído ===\n");
    return 0;
}

