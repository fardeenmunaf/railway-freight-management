#include <stdio.h>
#include <string.h>

extern int encrypt_data(char*, int, char*);
extern int decrypt_data(char*, int, char*);

int main(void) {
    char input[100];
    char encrypted[100];
    char decrypted[100];
    int key;
    int result;

    printf("=== Cifra de César (RISC-V Assembly) ===\n");

    // Ler palavra
    printf("Introduza a palavra (apenas letras maiúsculas A-Z): ");
    if (scanf("%99s", input) != 1) {
        printf("Erro a ler a palavra.\n");
        return 1;
    }

    // Ler chave
    printf("Introduza a chave (1-26): ");
    if (scanf("%d", &key) != 1) {
        printf("Erro a ler a chave.\n");
        return 1;
    }

    // Chamar função de encriptação
    result = encrypt_data(input, key, encrypted);
    if (result == 0) {
        printf("Erro na encriptação! Verifique se a palavra e chave são válidas.\n");
        return 1;
    }

    // Chamar função de desencriptação
    result = decrypt_data(encrypted, key, decrypted);
    if (result == 0) {
        printf("Erro na desencriptação! Verifique os dados.\n");
        return 1;
    }

    // Mostrar resultados
    printf("\n=== Resultados ===\n");
    printf("Original:   %s\n", input);
    printf("Encrypted:  %s\n", encrypted);
    printf("Decrypted:  %s\n", decrypted);

    return 0;
}
