    .text
    .globl filter_user_logs
    .type  filter_user_logs, @function

# int filter_user_logs(UserFilter filter,
#                      LogEntry *all_logs,
#                      int total_logs,
#                      LogEntry *out,
#                      int max_out)
#
# a0 = filter.user_id   (struct tem só 1 int)
# a1 = all_logs
# a2 = total_logs
# a3 = out
# a4 = max_out
#
# retorno em a0 = número de logs copiados

filter_user_logs:
    # guardar user_id do filtro
    mv      t0, a0          # t0 = filter.user_id

    li      t5, 0           # t5 = i (índice no array de entrada)
    li      t6, 0           # t6 = count (nº de logs copiados)

loop_start:
    # se i >= total_logs -> termina
    bge     t5, a2, finish

    # calcular endereço de all_logs[i]
    # sizeof(LogEntry) = 136 bytes = 128 + 8
    # offset = i * 136 = (i<<7) + (i<<3)
    slli    a5, t5, 7       # a5 = i * 128
    slli    a7, t5, 3       # a7 = i * 8
    add     a5, a5, a7      # a5 = i * 136
    add     a5, a1, a5      # a5 = &all_logs[i]

    # ler user_id da entrada (primeiro campo da struct)
    lw      t1, 0(a5)       # t1 = all_logs[i].user_id

    # comparar com filtro
    bne     t1, t0, next_log

    # se count >= max_out -> termina (não cabe mais)
    bge     t6, a4, finish

    # copiar LogEntry de all_logs[i] para 'out'
    # 136 bytes = 34 palavras de 4 bytes
    mv      a6, a5          # a6 = src = &all_logs[i]
    mv      a7, a3          # a7 = dst = out atual
    li      a0, 0           # a0 = k (contador de palavras)

    li      t2, 34          # t2 = limite (34 palavras)

copy_loop:
    bge     a0, t2, copy_done   # se k >= 34 -> acabou de copiar
    lw      t3, 0(a6)           # ler palavra
    sw      t3, 0(a7)           # escrever palavra
    addi    a6, a6, 4           # src += 4
    addi    a7, a7, 4           # dst += 4
    addi    a0, a0, 1           # k++
    j       copy_loop

copy_done:
    # atualizar ponteiro de saída (a3) para próximo slot
    mv      a3, a7              # a7 já está em out + 136
    addi    t6, t6, 1           # count++

next_log:
    addi    t5, t5, 1           # i++
    j       loop_start

finish:
    # devolver count em a0
    mv      a0, t6
    ret
