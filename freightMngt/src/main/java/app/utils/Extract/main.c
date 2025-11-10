#include <stdio.h>
#include <string.h>
#include <ctype.h>

// Declaração da função em Assembly que extrai dados da string
extern int extract_data(char *str, char *token, char *unit, int *value);

// Função para remover espaços no início e fim da string
void trim(char *s) {
    int start = 0, end = strlen(s) - 1;
    // Avança "start" enquanto for espaço
    while (start <= end && isspace((unsigned char)s[start])) start++;
    // Retrocede "end" enquanto for espaço
    while (end >= start && isspace((unsigned char)s[end])) end--;
    // Se houver espaços no início, move a substring para o início
    if (start > 0) memmove(s, s + start, end - start + 1);
    s[end - start + 1] = '\0';  // Termina a string
}

// Função para validar unidades para TEMP (celsius ou fahrenheit) e HUM (percentage)
int unidade_valida(const char *token, const char *unit) {
    if (strcmp(token, "TEMP") == 0) {
        if (strcmp(unit, "celsius") == 0 || strcmp(unit, "fahrenheit") == 0) {
            return 1; // Unidade válida para TEMP
        } else {
            printf("Unidade inválida para TEMP! Use apenas: celsius, fahrenheit\n");
            return 0;
        }
    }
    else if (strcmp(token, "HUM") == 0) {
        if (strcmp(unit, "percentage") == 0) {
            return 1; // Unidade válida para HUM
        } else {
            printf("Unidade inválida para HUM! Use apenas: percentage\n");
            return 0;
        }
    }
    // Caso token desconhecido, aceita unidade (ou pode-se ajustar)
    return 1;
}

int main() {
    char str[256];
    char unit_temp[20] = "";   // Unitário TEMP
    char unit_hum[20] = "";    // Unitário HUM
    int value_temp = 0;
    int value_hum = 0;
    int res_temp, res_hum;
    char token_temp[] = "TEMP";
    char token_hum[] = "HUM";

    printf("=== Data Extractor ===\n\n");

    // Ler a string toda, que pode conter um ou dois blocos (TEMP e/ou HUM)
    printf("Enter input string (ex: \"TEMP &unit:celsius &value:23 #HUM &unit:percentage &value:45\"):\n");
    fgets(str, sizeof(str), stdin);
    trim(str);

    // Extrair blocos TEMP e HUM da string se existirem
    res_temp = extract_data(str, token_temp, unit_temp, &value_temp);
    res_hum = extract_data(str, token_hum, unit_hum, &value_hum);

    // Validar unidade TEMP, se encontrada
    if (res_temp == 1 && !unidade_valida(token_temp, unit_temp)) {
        res_temp = 0; // Invalida a extração por unidade errada
    }
    // Validar unidade HUM, se encontrada
    if (res_hum == 1 && !unidade_valida(token_hum, unit_hum)) {
        res_hum = 0; // Invalida a extração por unidade errada
    }

    // Exibir resultados TEMP
    printf("\n=== TEMP Data ===\n");
    if (res_temp == 1) {
        printf("✓ Found TEMP data!\n");
        printf("  Unit: %s\n", unit_temp);
        printf("  Value: %d\n", value_temp);
    } else {
        printf("✗ TEMP data not found or unit invalid\n");
    }

    // Exibir resultados HUM
    printf("\n=== HUM Data ===\n");
    if (res_hum == 1) {
        printf("✓ Found HUM data!\n");
        printf("  Unit: %s\n", unit_hum);
        printf("  Value: %d\n", value_hum);
    } else {
        printf("✗ HUM data not found or unit invalid\n");
    }

    return 0;
}
