    .text
    .globl sort_array
    .type  sort_array, @function

# int sort_array(int* ptr, int num, char order)
# a0 = ptr
# a1 = num
# a2 = order

sort_array:
    # falha se num <= 0  (0 >= num)
    bge     x0, a1, .fail

    # asc_flag = (order != 0)  (1 = asc, 0 = desc)
    snez    t6, a2

    # i = 0
    li      t3, 0

.outer_cond:
    # limite: i >= num-1 ?  (usa t0 como temporário)
    addi    t0, a1, -1      # t0 = num - 1
    bge     t3, t0, .outer_done

    li      t4, 0           # j = 0

.inner_loop:
    # se j + i + 1 >= num  -> fim do ciclo interno
    add     t5, t3, t4      # t5 = i + j
    addi    t5, t5, 1       # t5 = i + j + 1
    bge     t5, a1, .inner_done

    # endereço de ptr[j]
    slli    t0, t4, 2       # t0 = j * 4
    add     t0, a0, t0      # t0 = &ptr[j]

    lw      t1, 0(t0)       # t1 = ptr[j]
    lw      t2, 4(t0)       # t2 = ptr[j+1]

    beq     t6, x0, .desc_cmp   # se asc_flag == 0 -> desc

# --- ordem crescente ---
.asc_cmp:
    # se t1 <= t2 não troca  (ble t1,t2)
    bge     t2, t1, .no_swap
    j       .do_swap

# --- ordem decrescente ---
.desc_cmp:
    # se t1 >= t2 não troca  (bge t1,t2)
    bge     t1, t2, .no_swap
    # caso contrário troca

.do_swap:
    sw      t2, 0(t0)       # ptr[j]   = t2
    sw      t1, 4(t0)       # ptr[j+1] = t1

.no_swap:
    addi    t4, t4, 1       # j++
    j       .inner_loop

.inner_done:
    addi    t3, t3, 1       # i++
    j       .outer_cond

.outer_done:
    li      a0, 1           # sucesso
    ret

.fail:
    li      a0, 0           # falha
    ret
