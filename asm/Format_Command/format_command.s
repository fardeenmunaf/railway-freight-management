    .section .text
    .global format_command

format_command:

    # Configuração da pilha
    addi    sp, sp, -16             # Reserva 16 bytes na pilha
    sw      ra, 12(sp)              # Guarda endereço de retorno
    sw      s0, 8(sp)               # Guarda s0
    sw      s1, 4(sp)               # Guarda s1
    sw      s2, 0(sp)               # Guarda s2

    mv      s0, a0                  # s0 = ponteiro da string de entrada (op) - READ ONLY
    mv      s1, a2                  # s1 = ponteiro da string de saída (cmd)
    mv      s2, a1                  # s2 = valor n

#-----------------------------------------------------ENCONTRAR INÍCIO (IGNORAR ESPAÇOS INICIAIS)----------------------------------------

    mv t0, s0                       # t0 = posição atual na string de entrada

find_start:
    lbu t1, 0(t0)                   # lê o byte atual
    beqz t1, unsuccess              # se '\0', string vazia ou só espaços → erro
    li t2, 0x20                     # ASCII do espaço
    beq t1, t2, skip_leading_space  # se for espaço, ignorar
    j found_start                   # encontrou o primeiro caractere não-espaço

skip_leading_space:
    addi t0, t0, 1                  # avançar para o próximo carácter
    j find_start                    # repetir loop

found_start:
    # t0 aponta para o primeiro caractere não-espaço
    # Agora vamos encontrar o último carácter válido

    mv t3, t0                       # t3 = posição do último carácter válido
    mv t4, t0                       # t4 = posição de varrimento atual

find_last_valid:
    lbu t5, 0(t4)                   # lê o byte atual
    beqz t5, process_command        # se '\0', terminou a varredura
    li t6, 0x20                     # ASCII do espaço
    bne t5, t6, update_last_valid   # se não for espaço, actualizar último válido
    addi t4, t4, 1                  # avançar ponteiro
    j find_last_valid               # continuar loop

update_last_valid:
    mv t3, t4                       # actualiza a posição do último carácter válido
    addi t4, t4, 1                  # avançar ponteiro
    j find_last_valid               # continuar loop

#-----------------------------------------------------PROCESSAR COMANDO----------------------------------------------------------

process_command:
    # t0 = primeiro carácter não-espaço
    # t3 = último carácter não-espaço
    # Calcula comprimento
    sub t4, t3, t0                  # t4 = t3 - t0
    addi t4, t4, 1                  # t4 = comprimento real

    # Verifica se o comprimento é 2 ou 3
    li t5, 2
    beq t4, t5, check_two_letter    # se 2 → comando de 2 letras
    li t5, 3
    beq t4, t5, check_three_letter# se 3 → comando de 3 letras
    j unsuccess                     # comprimento inválido → erro

check_two_letter:
    # Carrega e converte primeira letra para maiúscula
    lbu t1, 0(t0)                   # primeiro char
    jal ra, to_upper                # converte em maiúscula
    mv a3, t1                       # guarda resultado em a3

    # Carrega e converte segunda letra para maiúscula
    lbu t1, 1(t0)                   # segundo char
    jal ra, to_upper                # converte em maiúscula
    mv a4, t1                       # guarda resultado em a4

    # Verifica comandos válidos de duas letras: RE, YE, GE, RB
    li t2, 0x52                     # 'R'
    bne a3, t2, check_ye_cmd        # se não for 'R' → verifica YE
    li t2, 0x45                     # 'E'
    beq a4, t2, valid_two_letter    # RE encontrado
    li t2, 0x42                     # 'B'
    beq a4, t2, valid_two_letter    # RB encontrado
    j unsuccess                     # inválido

check_ye_cmd:
    li t2, 0x59                     # 'Y'
    bne a3, t2, check_ge_cmd        # se não for 'Y' → verifica GE
    li t2, 0x45                     # 'E'
    beq a4, t2, valid_two_letter    # YE encontrado
    j unsuccess                     # inválido

check_ge_cmd:
    li t2, 0x47                     # 'G'
    bne a3, t2, unsuccess           # se não for 'G' → inválido
    li t2, 0x45                     # 'E'
    beq a4, t2, valid_two_letter    # GE encontrado
    j unsuccess                     # inválido

check_three_letter:
    # Carrega e converte caracteres para maiúscula
    lbu t1, 0(t0)                   # primeiro char
    jal ra, to_upper
    mv a3, t1
    lbu t1, 1(t0)                   # segundo char
    jal ra, to_upper
    mv a4, t1
    lbu t1, 2(t0)                   # terceiro char
    jal ra, to_upper
    mv a5, t1

    # Verifica se é GTH
    li t2, 0x47                     # 'G'
    bne a3, t2, unsuccess
    li t2, 0x54                     # 'T'
    bne a4, t2, unsuccess
    li t2, 0x48                     # 'H'
    bne a5, t2, unsuccess

    # GTH é válido - não precisa de número
    sb a3, 0(s1)                    # cmd[0] = 'G'
    sb a4, 1(s1)                    # cmd[1] = 'T'
    sb a5, 2(s1)                    # cmd[2] = 'H'
    sb zero, 3(s1)                  # cmd[3] = '\0'
    j success

valid_two_letter:
    # Verifica se n está no intervalo [0, 99]
    bltz s2, unsuccess              # n < 0 → erro
    li t5, 99
    bgt s2, t5, unsuccess           # n > 99 → erro

    # Escreve comando no buffer de saída
    sb a3, 0(s1)                    # cmd[0] = primeira letra
    sb a4, 1(s1)                    # cmd[1] = segunda letra
    li t5, 0x2C                     # ','
    sb t5, 2(s1)                    # cmd[2] = ','

    # Converte n em dois dígitos ASCII
    li t5, 10
    div t6, s2, t5                  # t6 = dezenas
    rem a6, s2, t5                  # a6 = unidades
    addi t6, t6, 0x30               # converte dezenas para ASCII
    addi a6, a6, 0x30               # converte unidades para ASCII
    sb t6, 3(s1)                    # cmd[3] = dezena
    sb a6, 4(s1)                    # cmd[4] = unidade
    sb zero, 5(s1)                  # cmd[5] = '\0'
    j success

#-----------------------------------------------------HELPER: TO_UPPER--------------------------------------------------------------------
# Entrada: t1 = carácter
# Saída: t1 = carácter em maiúscula
to_upper:
    li t2, 0x61                     # 'a'
    li t6, 0x7A                     # 'z'
    bltu t1, t2, to_upper_done      # se menor que 'a' → não altera
    bgtu t1, t6, to_upper_done      # se maior que 'z' → não altera
    addi t1, t1, -32                # converte minúscula para maiúscula
to_upper_done:
    ret

#----------------------------------------------------RETURN-------------------------------------------------------------------------

success:
    lw      ra, 12(sp)              # restaura ra
    lw      s0, 8(sp)               # restaura s0
    lw      s1, 4(sp)               # restaura s1
    lw      s2, 0(sp)               # restaura s2
    addi    sp, sp, 16              # desaloca pilha
    li a0, 1                        # return 1 (sucesso)
    ret

unsuccess:
    sb zero, 0(s1)                  # limpa buffer de saída (cmd[0] = '\0')
    lw      ra, 12(sp)              # restaura ra
    lw      s0, 8(sp)               # restaura s0
    lw      s1, 4(sp)               # restaura s1
    lw      s2, 0(sp)               # restaura s2
    addi    sp, sp, 16              # desaloca pilha
    li a0, 0                        # return 0 (erro)
    ret
