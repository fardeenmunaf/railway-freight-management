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
    /* ============================
       Exemplo 1: sem wrap-around
       ============================ */
    int buf1[8]  = {10, 20, 30, 40, 50, 60, 70, 80};
    int out1[8]  = {0};
    int n_elem1  = 5;   /* há 5 elementos válidos no buffer */
    int read1    = 2;   /* mais antigo está em buf1[2] -> 30 */
    int write1   = 7;   /* posição de escrita (não é alterada) */
    int num1     = 3;   /* queremos mover 3 elementos */

    printf("=== Exemplo 1 (sem wrap, sucesso) ===\n");
    print_array("buffer antes: ", buf1, 8);
    printf("n_elem = %d, read = %d, write = %d, num = %d\n",
           n_elem1, read1, write1, num1);

    int r1 = move_n_to_array(buf1, 8, &n_elem1, &read1, &write1, num1, out1);

    printf("return = %d\n", r1);
    print_array("out:          ", out1, num1);
    printf("n_elem = %d, read = %d, write = %d\n\n",
           n_elem1, read1, write1);

    /* Situação esperada:
       - elementos movidos: 30, 40, 50
       - n_elem passa de 5 para 2
       - read passa de 2 para 5
       - write não muda
    */

    /* ============================
       Exemplo 2: com wrap-around
       buffer lógico: 50, 60, 10, 20
       ============================ */
    int buf2[6]  = {10, 20, 30, 40, 50, 60};
    int out2[6]  = {0};
    int n_elem2  = 4;   /* 4 elementos válidos */
    int read2    = 4;   /* começa em 50 (buf2[4]) */
    int write2   = 2;   /* qualquer valor, não é alterado */
    int num2     = 3;   /* vamos mover 50, 60, 10 */

    printf("=== Exemplo 2 (com wrap, sucesso) ===\n");
    print_array("buffer antes: ", buf2, 6);
    printf("n_elem = %d, read = %d, write = %d, num = %d\n",
           n_elem2, read2, write2, num2);

    int r2 = move_n_to_array(buf2, 6, &n_elem2, &read2, &write2, num2, out2);

    printf("return = %d\n", r2);
    print_array("out:          ", out2, num2);
    printf("n_elem = %d, read = %d, write = %d\n\n",
           n_elem2, read2, write2);

    /* Situação esperada:
       - elementos movidos: 50, 60, 10
       - n_elem: 4 -> 1
       - read: 4 -> 1  (4->5->0->1)
       - write não muda
    */

    /* ============================
       Exemplo 3: falha (n_elem < num)
       ============================ */
    int buf3[4]  = {1, 2, 3, 4};
    int out3[4]  = {0};
    int n_elem3  = 2;   /* só 2 elementos válidos */
    int read3    = 1;
    int write3   = 3;
    int num3     = 3;   /* queríamos 3 -> deve falhar */

    printf("=== Exemplo 3 (falha: n_elem < num) ===\n");
    print_array("buffer antes: ", buf3, 4);
    printf("n_elem = %d, read = %d, write = %d, num = %d\n",
           n_elem3, read3, write3, num3);

    int r3 = move_n_to_array(buf3, 4, &n_elem3, &read3, &write3, num3, out3);

    printf("return = %d\n", r3);
    print_array("out (ignorado): ", out3, 4);
    printf("n_elem = %d, read = %d, write = %d\n",
           n_elem3, read3, write3);

    /* Situação esperada:
       - return = 0
       - n_elem, read, write NÃO mudam
       - out3 continua tudo a 0
    */

    return 0;
}
