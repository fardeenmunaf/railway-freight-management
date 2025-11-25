#include <stdio.h>
#include "asm.h"

static void print_array(const char *label, int *v, int n) {
    printf("%s", label);
    for (int i = 0; i < n; i++) {
        printf("%s%d", (i == 0 ? "" : " "), v[i]);
    }
    printf("\n");
}

int main(void) {
    int vec1[] = {4, 1, 7, 3, 2};
    int len1   = sizeof(vec1) / sizeof(vec1[0]);

    printf("=== Ordem crescente (order = 1) ===\n");
    print_array("Antes:  ", vec1, len1);
    int r1 = sort_array(vec1, len1, 1);
    printf("retorno = %d\n", r1);
    print_array("Depois: ", vec1, len1);

    int vec2[] = {4, 1, 7, 3, 2};
    int len2   = sizeof(vec2) / sizeof(vec2[0]);

    printf("\n=== Ordem decrescente (order = 0) ===\n");
    print_array("Antes:  ", vec2, len2);
    int r2 = sort_array(vec2, len2, 0);
    printf("retorno = %d\n", r2);
    print_array("Depois: ", vec2, len2);

    return 0;
}
