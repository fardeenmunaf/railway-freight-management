    .text
    .globl move_n_to_array
    .type  move_n_to_array, @function
# int move_n_to_array(int* buffer, int length,
#                     int *n_elem, int *read, int *write,
#                     int num, int *array)
#
# a0 = buffer
# a1 = length
# a2 = n_elem*
# a3 = read*   (índice do elemento mais antigo)
# a4 = write*  (índice de escrita – NÃO alterar *a4)
# a5 = num     (n elementos a remover/copiar)
# a6 = array   (destino)
#
# retorno:
#   a0 = 1 em sucesso
#   a0 = 0 em falha (*n_elem < num ou length == 0 com num>0)

move_n_to_array:
    # caso trivial: num == 0 → sucesso, não mexe em nada
    beq     a5, x0, .ret_success        # n == 0

    # comprimento inválido
    beq     a1, x0, .ret_fail           # length == 0 e n > 0

    # carregar *n_elem
    lw      t0, 0(a2)                   # t0 = *n_elem

    # se *n_elem < num → falha
    blt     t0, a5, .ret_fail

    # carregar read (índice do mais antigo)
    lw      t1, 0(a3)                   # t1 = *read

    # preparar variáveis
    mv      t2, a1                      # t2 = length
    mv      t3, a5                      # t3 = num (restantes para mover)
    mv      t4, a0                      # t4 = base do buffer
    mv      t5, a6                      # t5 = ptr de escrita no array

    # loop: copiar num elementos
.loop_copy:
    beq     t3, x0, .done               # se t3 == 0 terminou

    # calcular &buffer[t1]
    slli    a7, t1, 2                   # a7 = t1 * 4
    add     a7, t4, a7                  # a7 = buffer + t1*4
    lw      a4, 0(a7)                   # a4 = buffer[t1] (temporário)

    # escrever no array destino
    sw      a4, 0(t5)                   # *t5 = valor
    addi    t5, t5, 4                   # t5++

    # avançar read com wrap
    addi    t1, t1, 1                   # t1++
    bne     t1, t2, .no_wrap
    mv      t1, x0                      # t1 = 0 se chegou a length
.no_wrap:

    # atualizar contadores
    addi    t0, t0, -1                  # *n_elem--
    addi    t3, t3, -1                  # restantes--

    j       .loop_copy

.done:
    # guardar novos *n_elem e *read
    sw      t0, 0(a2)                   # *n_elem = t0
    sw      t1, 0(a3)                   # *read  = t1
    # *write (a4) NÃO é alterado

.ret_success:
    li      a0, 1
    ret

.ret_fail:
    li      a0, 0
    ret
