   .section .text
    .globl encrypt_data
    .type encrypt_data, @function

#With errors because of ide but works on antix/wsl
# ==========================================================
# FUNCTION: encrypt_data
# ----------------------------------------------------------
# int encrypt_data(char* in, int key, char* out)
#
# Descrição:
#   Função que implementa a encriptação do tipo César Cipher.
#   Só processa letras maiúsculas (A–Z) e chaves entre 1 e 26.
#   Qualquer outro caso leva a falha (retorna 0).
#
# Entradas:
#   a0 → ponteiro para string de entrada (in)
#   a1 → chave (key)
#   a2 → ponteiro para string de saída (out)
#
# Saída:
#   a0 = 1 → sucesso
#   a0 = 0 → falha
# ==========================================================

encrypt_data:
    # --- Reservar espaço na pilha e salvar registos ---
    addi sp, sp, -20        # Reserva espaço para 5 words (20 bytes)
    sw ra, 16(sp)           # Guarda endereço de retorno
    sw s0, 12(sp)           # Guarda s0
    sw s1, 8(sp)            # Guarda s1
    sw s2, 4(sp)            # Guarda s2
    sw s3, 0(sp)            # Guarda s3

    # --- Guardar parâmetros em registos salvos ---
    mv s0, a2               # s0 = out (Endereço original de out)
    mv s1, a0               # s1 = in (Endereço original de in)
    mv s2, a1               # s2 = key
    li s3, 1                # s3 = return_code = 1 (assume sucesso)

    # --- Verificar se a string está vazia ---
    lb t0, 0(a0)
    beqz t0, check_key_for_empty_string   # Se *in == '\0', verifica a chave antes de falhar

    # --- Verificar se a key ∈ [1, 26] ---
    li t0, 1
    blt s2, t0, failure_key # key < 1 → falha
    li t0, 26
    bgt s2, t0, failure_key # key > 26 → falha

    j loop_start # Continua para o loop principal

check_key_for_empty_string:
    # A string está vazia. Apenas a chave precisa de ser válida para retornar sucesso (1).
    # Se a chave for inválida, salta para failure_key.
    li t0, 1
    blt s2, t0, failure_key # key < 1 → falha
    li t0, 26
    bgt s2, t0, failure_key # key > 26 → falha
    
    # Se a chave é válida, salta para o final do loop (sucesso)
    j loop_end

# ================= LOOP PRINCIPAL =================
loop_start:
    lb t0, 0(a0)            # Ler próximo caractere da string de entrada
    beqz t0, loop_end       # Se for '\0', acabou a string

    # --- Validar caractere ---
    li t1, 'A'              # Limite inferior = 'A'
    blt t0, t1, failure_char
    li t1, 'Z'              # Limite superior = 'Z'
    bgt t0, t1, failure_char

    # --- Converter caractere ASCII → índice 0–25 ---
    li t1, 'A'
    sub t0, t0, t1          # t0 = (char - 'A')

    # --- Aplicar fórmula do César Cipher ---
    add t0, t0, s2          # t0 = t0 + key
    li t1, 26
    rem t0, t0, t1          # t0 = (t0 + key) % 26

    # --- Converter índice → ASCII ---
    li t1, 'A'
    add t0, t0, t1          # t0 = t0 + 'A'

    # --- Guardar resultado no buffer de saída ---
    sb t0, 0(a2)

    # --- Avançar ponteiros ---
    addi a0, a0, 1          # in++
    addi a2, a2, 1          # out++
    j loop_start            # Repetir para o próximo caractere

# ================= SUCESSO =================
loop_end:
    sb zero, 0(a2)          # Terminar string de saída com '\0'
    j end_function          # Sair com sucesso

# ================= FALHA =================
failure_key:
failure_char:
    li s3, 0                # return_code = 0
    sb zero, 0(s0)          # out = "" (string vazia, usando o endereço original s0)

# ================= FIM DA FUNÇÃO =================
end_function:
    mv a0, s3               # a0 = return_code (1 ou 0)
    lw ra, 16(sp)           # Restaura ra
    lw s0, 12(sp)
    lw s1, 8(sp)
    lw s2, 4(sp)
    lw s3, 0(sp)
    addi sp, sp, 20         # Liberta espaço da pilha
    ret                     # Volta ao chamador
