    .section .text
    .globl enqueue_value
    .type enqueue_value, @function

# ==========================================================
# FUNCTION: enqueue_value
# ----------------------------------------------------------
# int enqueue_value(int* buffer, int length, int* nelem,
#                   int* tail, int* head, int value)
#
# Descrição:
#   Insere um novo valor num buffer circular (ring buffer).
#   Se o buffer estiver cheio, sobrescreve o elemento mais antigo.
#
# Entradas:
#   a0 → ponteiro para o buffer (int*)
#   a1 → tamanho do buffer (length)
#   a2 → ponteiro para número de elementos (int* nelem)
#   a3 → ponteiro para índice tail (int* tail)
#   a4 → ponteiro para índice head (int* head)
#   a5 → valor a inserir (value)
#
# Saída:
#   a0 = 1 → buffer ficou cheio após inserção
#   a0 = 0 → buffer não está cheio
# ==========================================================

enqueue_value:
    # --- Reservar espaço na pilha e salvar registos ---
    addi sp, sp, -8         # Reserva espaço para 2 words
    sw s0, 4(sp)            # Guarda s0
    sw s1, 0(sp)            # Guarda s1

    # --- Carregar o valor atual de head ---
    lw t0, 0(a4)            # t0 = *head (índice atual do head)

    # --- Calcular o endereço buffer[head] ---
    slli t1, t0, 2          # t1 = head * 4 (cada int tem 4 bytes)
    add t1, a0, t1          # t1 = &buffer[head]

    # --- Inserir o valor no buffer ---
    sw a5, 0(t1)            # buffer[head] = value

    # --- Incrementar head com wrap-around ---
    addi t0, t0, 1          # head++
    blt t0, a1, head_no_wrap # Se head < length, não precisa wrap
    li t0, 0                # head = 0 (wrap-around)
head_no_wrap:
    sw t0, 0(a4)            # Atualizar *head na memória

    # --- Carregar nelem atual ---
    lw t2, 0(a2)            # t2 = *nelem

    # --- Verificar se buffer estava cheio antes da inserção ---
    bge t2, a1, buffer_was_full # Se nelem >= length, buffer está cheio

    # --- Buffer não estava cheio - incrementar nelem ---
    addi t2, t2, 1          # nelem++
    sw t2, 0(a2)            # Atualizar *nelem na memória

    # --- Verificar se ficou cheio agora ---
    bge t2, a1, return_full # Se nelem >= length, retornar 1

    # --- Buffer não está cheio ---
    li a0, 0                # Retornar 0 (não cheio)
    j restore_and_return

buffer_was_full:
    # --- Buffer estava cheio - incrementar tail com wrap-around ---
    lw t3, 0(a3)            # t3 = *tail
    addi t3, t3, 1          # tail++
    blt t3, a1, tail_no_wrap # Se tail < length, não precisa wrap
    li t3, 0                # tail = 0 (wrap-around)
tail_no_wrap:
    sw t3, 0(a3)            # Atualizar *tail na memória

return_full:
    li a0, 1                # Retornar 1 (cheio)

restore_and_return:
    # --- Restaurar registos e retornar ---
    lw s0, 4(sp)            # Restaura s0
    lw s1, 0(sp)            # Restaura s1
    addi sp, sp, 8          # Liberta espaço da pilha
    ret                     # Retorna

