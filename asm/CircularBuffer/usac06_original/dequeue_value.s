    .section .text
    .globl dequeue_value
    .type dequeue_value, @function

# ==========================================================
# FUNCTION: dequeue_value
# ----------------------------------------------------------
# int dequeue_value(int* buffer, int length, int* nelem,
#                   int* tail, int* head, int* value)
#
# Descrição:
#   Remove e retorna o valor mais antigo do buffer circular.
#   Implementa comportamento FIFO (First-In, First-Out).
#
# Entradas:
#   a0 → ponteiro para o buffer (int*)
#   a1 → tamanho do buffer (length)
#   a2 → ponteiro para número de elementos (int* nelem)
#   a3 → ponteiro para índice tail (int* tail)
#   a4 → ponteiro para índice head (int* head)
#   a5 → ponteiro para armazenar o valor removido (int* value)
#
# Saída:
#   a0 = 1 → sucesso (valor removido)
#   a0 = 0 → falha (buffer vazio)
# ==========================================================

dequeue_value:
    # --- Reservar espaço na pilha e salvar registos ---
    addi sp, sp, -8         # Reserva espaço para 2 words
    sw s0, 4(sp)            # Guarda s0
    sw s1, 0(sp)            # Guarda s1

    # --- Carregar nelem atual ---
    lw t0, 0(a2)            # t0 = *nelem

    # --- Verificar se buffer está vazio ---
    beqz t0, buffer_empty   # Se nelem == 0, buffer está vazio

    # --- Carregar o valor atual de tail ---
    lw t1, 0(a3)            # t1 = *tail (índice do elemento mais antigo)

    # --- Calcular o endereço buffer[tail] ---
    slli t2, t1, 2          # t2 = tail * 4 (cada int tem 4 bytes)
    add t2, a0, t2          # t2 = &buffer[tail]

    # --- Ler o valor do buffer ---
    lw t3, 0(t2)            # t3 = buffer[tail]

    # --- Armazenar o valor no ponteiro de saída ---
    sw t3, 0(a5)            # *value = buffer[tail]

    # --- Incrementar tail com wrap-around ---
    addi t1, t1, 1          # tail++
    blt t1, a1, tail_no_wrap # Se tail < length, não precisa wrap
    li t1, 0                # tail = 0 (wrap-around)
tail_no_wrap:
    sw t1, 0(a3)            # Atualizar *tail na memória

    # --- Decrementar nelem ---
    addi t0, t0, -1         # nelem--
    sw t0, 0(a2)            # Atualizar *nelem na memória

    # --- Retornar sucesso ---
    li a0, 1                # Retornar 1 (sucesso)
    j restore_and_return

buffer_empty:
    # --- Buffer vazio - retornar falha ---
    li a0, 0                # Retornar 0 (falha)

restore_and_return:
    # --- Restaurar registos e retornar ---
    lw s0, 4(sp)            # Restaura s0
    lw s1, 0(sp)            # Restaura s1
    addi sp, sp, 8          # Liberta espaço da pilha
    ret                     # Retorna

