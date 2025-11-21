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
    /* exemplo 1: remoção sem wrap */
    int buf1[8] = {10, 20, 30, 40, 50, 60, 70, 80};
    int out1[8] = {0};
    int n_elem1 = 5;     /* elementos válidos no buffer */
    int read1   = 2;     /* índice do mais antigo: 30 */
    int write1  = 7;     /* ignorado pela função */
    int n1      = 3;     /* quantos queremos mover */

    printf("=== Teste 1 (sem wrap, suficiente) ===\n");
    print_array("buffer antes: ", buf1, 8);
    printf("n_elem = %d, read = %d, write = %d, n = %d\n",
           n_elem1, read1, write1, n1);

    int res1 = move_n_to_array(buf1, 8, &n_elem1, &read1, &write1, n1, out1);

    printf("return = %d\n", res1);
    print_array("out:          ", out1, n1);
    printf("n_elem = %d, read = %d, write = %d\n\n",
           n_elem1, read1, write1);

    /* exemplo 2: com wrap-around */
    int buf2[6] = {10, 20, 30, 40, 50, 60};
    int out2[6] = {0};
    int n_elem2 = 4;    /* elementos válidos: 50,60,10,20 */
    int read2   = 4;    /* começa em 50 */
    int write2  = 2;
    int n2      = 3;    /* vamos tirar 50,60,10 */

    printf("=== Teste 2 (com wrap) ===\n");
    print_array("buffer antes: ", buf2, 6);
    printf("n_elem = %d, read = %d, write = %d, n = %d\n",
           n_elem2, read2, write2, n2);

    int res2 = move_n_to_array(buf2, 6, &n_elem2, &read2, &write2, n2, out2);

    printf("return = %d\n", res2);
    print_array("out:          ", out2, n2);
    printf("n_elem = %d, read = %d, write = %d\n\n",
           n_elem2, read2, write2);

    /* exemplo 3: não há elementos suficientes → falha */
    int buf3[4] = {1, 2, 3, 4};
    int out3[4] = {0};
    int n_elem3 = 2;
    int read3   = 1;
    int write3  = 3;
    int n3      = 3;

    printf("=== Teste 3 (falha: n_elem < n) ===\n");
    print_array("buffer antes: ", buf3, 4);
    printf("n_elem = %d, read = %d, write = %d, n = %d\n",
           n_elem3, read3, write3, n3);

    int res3 = move_n_to_array(buf3, 4, &n_elem3, &read3, &write3, n3, out3);

    printf("return = %d\n", res3);
    print_array("out (ignorado): ", out3, 4);
    printf("n_elem = %d, read = %d, write = %d\n",
           n_elem3, read3, write3);

    return 0;
}
