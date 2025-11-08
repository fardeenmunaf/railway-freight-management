    .section .text
    .globl decrypt_data
    .type decrypt_data, @function

#With errors because of ide but works on antix/wsl
# ==========================================================
# FUNCTION: decrypt_data
# ----------------------------------------------------------
# int decrypt_data(char* in, int key, char* out)
#
# Descrição:
#   Implementa a desencriptação do tipo César Cipher.
#   Aceita apenas letras maiúsculas ('A'..'Z') e key ∈ [1, 26].
#   A decifração é feita por: D(x) = (x - key + 26) mod 26.
#
# Entradas:
#   a0 → ponteiro para string encriptada (in)
#   a1 → chave (key)
#   a2 → ponteiro para saída (out)
#
# Saída:
#   a0 = 1 → sucesso
#   a0 = 0 → falha
# ==========================================================

decrypt_data:
    # --- Reserva espaço na pilha e salva contexto ---
    addi sp, sp, -20
    sw ra, 16(sp)
    sw s0, 12(sp)
    sw s1, 8(sp)
    sw s2, 4(sp)
    sw s3, 0(sp)

    # --- Guardar parâmetros ---
    mv s0, a2               # s0 = out
    mv s1, a0               # s1 = in
    mv s2, a1               # s2 = key
    li s3, 1                # s3 = return_code = 1 (sucesso)

    # --- Verificar se entrada é vazia ---
    lb t0, 0(a0)
    beqz t0, failure_char   # Se string vazia, falha

    # --- Verificar se key ∈ [1, 26] ---
    li t0, 1
    blt s2, t0, failure_key
    li t0, 26
    bgt s2, t0, failure_key

# ================= LOOP PRINCIPAL =================
loop_start:
    lb t0, 0(a0)            # Lê próximo caractere da entrada
    beqz t0, loop_end       # Fim da string?

    # --- Validar caractere ---
    li t1, 'A'
    blt t0, t1, failure_char
    li t1, 'Z'
    bgt t0, t1, failure_char

    # --- Converter ASCII → índice (0–25) ---
    li t1, 'A'
    sub t0, t0, t1

    # --- Aplicar D(x) = (x - key + 26) mod 26 ---
    sub t0, t0, s2          # t0 = t0 - key
    bltz t0, fix_negative   # Se negativo, corrige com +26
    j do_mod

fix_negative:
    addi t0, t0, 26         # t0 += 26

do_mod:
    li t1, 26
    rem t0, t0, t1          # t0 = t0 % 26

    # --- Converter de volta para ASCII ---
    li t1, 'A'
    add t0, t0, t1

    # --- Escrever caractere decifrado ---
    sb t0, 0(a2)

    # --- Avançar ponteiros ---
    addi a0, a0, 1
    addi a2, a2, 1
    j loop_start

# ================= SUCESSO =================
loop_end:
    sb zero, 0(a2)          # out termina com '\0'
    j end_function

# ================= FALHA =================
failure_key:
failure_char:
    li s3, 0                # return_code = 0 (falha)
    sb zero, 0(s0)          # out = "" (string vazia)

# ================= FIM DA FUNÇÃO =================
end_function:
    mv a0, s3               # a0 = return_code
    lw ra, 16(sp)
    lw s0, 12(sp)
    lw s1, 8(sp)
    lw s2, 4(sp)
    lw s3, 0(sp)
    addi sp, sp, 20
    ret
