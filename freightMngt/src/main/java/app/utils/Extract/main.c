#include <stdio.h>
#include <string.h>
#include <ctype.h>

// Declaração da função externa que está em Assembly
extern int extract_data(char *str, char *token, char *unit, int *value);

// Função para remover espaços em branco do início e do fim de uma string
void trim(char *s) {
    int start = 0, end = strlen(s) - 1;
    while (start <= end && isspace((unsigned char)s[start])) start++;
    while (end >= start && isspace((unsigned char)s[end])) end--;
    if (start > 0) memmove(s, s + start, end - start + 1);
    s[end - start + 1] = '\0';
}

// Função para converter uma string para maiúsculas
void str_to_upper(char *s) {
    while (*s) {
        *s = toupper((unsigned char)*s);
        s++;
    }
}

int main() {
    char str[256];
    char token[20];
    char unit_input[20];
    char value_str[20];
    char unit[20];
    int value;
    int res;
    int value_input;
    char sensor_choice[5]; // Usado para guardar a escolha original (1 ou 2)

    printf("=== Data Extractor ===\n\n");

    // Input 1: Selecionar o sensor
    printf("Select sensor (1=TEMP, 2=HUM): ");
    fgets(sensor_choice, sizeof(sensor_choice), stdin);
    trim(sensor_choice);

    if (strcmp(sensor_choice, "1") == 0) {
        strcpy(token, "TEMP");
    } else if (strcmp(sensor_choice, "2") == 0) {
        strcpy(token, "HUM");
    } else {
        printf("Error: Invalid choice. Use 1 or 2.\n");
        return 1;
    }

    // Input 2: Inserir a unidade
    printf("Enter unit (e.g., celsius for TEMP, percentage for HUM): ");
    fgets(unit_input, sizeof(unit_input), stdin);
    trim(unit_input);

    if (strlen(unit_input) == 0) {
        printf("Error: Unit cannot be empty.\n");
        return 1;
    }

    // --- NOVA VERIFICAÇÃO: Validar a unidade com base no sensor escolhido ---
    int is_temp = (strcmp(sensor_choice, "1") == 0);
    int is_hum = (strcmp(sensor_choice, "2") == 0);
    int unit_is_celsius = (strcmp(unit_input, "celsius") == 0);
    int unit_is_percentage = (strcmp(unit_input, "percentage") == 0);

    if (is_temp && !unit_is_celsius) {
        printf("\nError: Invalid unit. For TEMP sensor, the only valid unit is 'celsius'.\n");
        return 1;
    }

    if (is_hum && !unit_is_percentage) {
        printf("\nError: Invalid unit. For HUM sensor, the only valid unit is 'percentage'.\n");
        return 1;
    }
    // --- FIM DA NOVA VERIFICAÇÃO ---


    // Input 3: Inserir o valor
    printf("Enter value (0-99): ");
    fgets(value_str, sizeof(value_str), stdin);
    trim(value_str);

    if (sscanf(value_str, "%d", &value_input) != 1 || value_input < 0 || value_input > 99) {
        printf("Error: Invalid value. Use 0-99.\n");
        return 1;
    }

    // Construir a string formatada
    sprintf(str, "%s &unit:%s &value:%02d", token, unit_input, value_input);

    printf("\n[DEBUG] Built string: \"%s\"\n", str);
    printf("[DEBUG] Token to search: \"%s\"\n\n", token);

    // Chamar a função Assembly
    res = extract_data(str, token, unit, &value);

    // Exibir os resultados
    printf("=== Result ===\n");
    if (res == 1) {
        printf("✓ Success: Found!\n");
        printf("  Unit: %s\n", unit);
        printf("  Value: %d\n", value);
    } else {
        printf("✗ Failed: Token not found\n");
        printf("  Unit: (empty)\n");
        printf("  Value: 0\n");
    }

    return 0;
}
