    .section .text
    .global format_command

format_command:

    # Config Stack

    addi    sp, sp, -16
    sw      ra, 12(sp)
    sw      s0, 8(sp)
    sw      s1, 4(sp)
    sw      s2, 0(sp)

    mv      s0, a0      # s0 = op pointer (input)
    mv      s1, a2      # s1 = cmd pointer (output)
    mv      s2, a1      # s2 = n (temporarily saved)


#-----------------------------------------------------TRIM--------------------------------------------------------------------------

# Primeiro, encontra a posiçao do primeiro char nao-espaço e altera o pointer para essa posiçao

    mv t0, s0           # passar o ponteiro para t0 

trim_before:
    lbu t1, 0(t0)                       # lê o byte atual da string
    beq t1, zero, trim_before_done      # se for '\0', string vazia, termina
    li t2, 0x20                         # ASCII do espaço ' '
    beq t1, t2, trim_next               # se for espaço, avança
    j trim_before_done                  # encontrou caractere não-espaço, termina

trim_next:
    addi t0, t0, 1              # avança o ponteiro
    j trim_before               # repete o ciclo

trim_before_done:
    mv s0, t0                   # s0 agora aponta para o primeiro caractere não-espaço

# ----------------------------------------------------------------------------------------------------------------------------------
# De seguida precisamos de dar encontrar o final da string e remover todos os espaços ate encontrar uma letra

    mv t3, s0                   # passar o ponteiro para t3
    
find_end:
    lbu t4, 0(t3)                               # lê byte
    beq t4, zero, back_to_last_valid_char       # se for '\0' chegamos ao final
    addi t3, t3, 1                              # avança o ponteiro
    j find_end                                  # volta ao loop

back_to_last_valid_char:
    addi t3, t3, -1                 # recua para o último caractere válido (antes de '\0')

trim_after:
    blt t3, s0, trim_after_done     # se recuou até antes do início, parar (verificaçao de string vazia ou so com espaços)
    lbu t4, 0(t3)                   # lê o caractere atual
    li t5, 0x20                     # ASCII do espaço ' '
    bne t4, t5, trim_after_done     # se não é espaço, terminou
    addi t3, t3, -1                 # recua uma posiçao no ponteiro
    j trim_after

trim_after_done:
    addi t3, t3, 1                  # nova posiçao de '\0'
    sb zero, 0(t3)                  # adiciona '\0' a t3

#------------------------------------------------------UPPER CASE-----------------------------------------------------------------
    
    mv t5, s0                       # copia ponteiro para t5

upper_case:
    lbu t6, 0(t5)                   # carrega byte para t6
    beqz t6, compare_RE             # Se t6 igual a '\0', acabou o upper
    li t1, 0x61                     # codigo ASCII de 'a'
    li t2, 0x7A                     # codigo ASCII de 'z'  
    bltu t6, t1, skip_upper_case    # nao é minuscula 
    bgtu t6, t2, skip_upper_case    # nao é minuscula
    addi t6, t6, -32                # converte em maiuscula
    sb t6, 0(t5)                    # carrega o byte para t5
    addi t5, t5, 1                  # anda o ponteiro para frente
    j upper_case                    # continua o loop

skip_upper_case:
    addi t5, t5, 1                  # anda o ponteiro para frente
    j upper_case                    # volta ao loop

#-----------------------------------------------------COMPARE--------------------------------------------------------------------

compare_RE:
    lbu t0, 0(s0)                   # carrega primeiro char de op
    li t3, 0x52                     # 'R'
    bne t0, t3, compare_YE          # nao é RE

    lbu t1, 1(s0)                   # carrega segundo char de op
    li t4, 0x45                     # 'E'
    bne t1, t4, compare_YE          # nao é RE

    lbu t2, 2(s0)                   # carrega o 3 char (deve ser \0)
    beqz t2, cmd_two_letter         # se for 0 é RE

compare_YE:
    lbu t0, 0(s0)                   # carrega primeiro char de op
    li t3, 0x59                     # 'Y'
    bne t0, t3, compare_GE          # nao é YE

    lbu t1, 1(s0)                   # carrega segundo char de op
    li t4, 0x45                     # 'E'
    bne t1, t4, compare_GE          # nao é YE

    lbu t2, 2(s0)                   # carrega o 3 char (deve ser \0)
    beqz t2, cmd_two_letter         # se for 0 é YE

compare_GE:
    lbu t0, 0(s0)                   # carrega primeiro char de op
    li t3, 0x47                     # 'G'
    bne t0, t3, compare_RB          # nao é GE

    lbu t1, 1(s0)                   # carrega segundo char de op
    li t4, 0x45                     # 'E'
    bne t1, t4, compare_RB          # nao é GE

    lbu t2, 2(s0)                   # carrega o 3 char (deve ser \0)
    beqz t2, cmd_two_letter         # se for 0 é GE

compare_RB:
    lbu t0, 0(s0)                   # carrega primeiro char de op
    li t3, 0x52                     # 'R'
    bne t0, t3, compare_GTH         # nao é RB 

    lbu t1, 1(s0)                   # carrega segundo char de op
    li t4, 0x42                     # 'B'
    bne t1, t4, compare_GTH         # nao é RB

    lbu t2, 2(s0)                   # carrega o 3 char (deve ser \0)
    beqz t2, cmd_two_letter         # se for 0 é RB

compare_GTH:
    lbu t0, 0(s0)                   # carrega primeiro char de op
    li t3, 0x47                     # 'G'
    bne t0, t3, unsuccess           # nao é GTH

    lbu t1, 1(s0)                   # carrega segundo char de op
    li t4, 0x54                     # 'T'
    bne t1, t4, unsuccess           # nao é GTH

    lbu t2, 2(s0)                   # carrega o 3 char
    li t5, 0x48                     # 'H'
    bne t2, t5, unsuccess           # nao é GTH

    lbu t3, 3(s0)                   # carrega o 4 char (deve ser \0)
    beqz t3, cmd_three_letter       # se for 0 é GTH

cmd_two_letter:

    mv t3, s2                   # carrega n para t3
    bltz t3, unsuccess          # n < 0
    li t4, 99                   # t4 = 99
    bgt t3, t4, unsuccess       # n > 99
    sb t0, 0(s1)                # cmd[0] = primeiro char de op
    sb t1, 1(s1)                # cmd[1] = segundo char de op
    li t4, 0x2C                 # t4 = ','
    sb t4, 2(s1)                # cmd [2] = ','
    li t4, 10                   # t4 = 10
    div t5, t3, t4              # t5 = t3 / 10 (dezenas)
    rem t6, t3, t4              # t6 = t3 % 10 (unidades)
    addi t5, t5, 0x30           # converte para ASCII
    addi t6, t6, 0x30           # converte para ASCII
    sb t5, 3(s1)                # cmd[3] = dezenas
    sb t6, 4(s1)                # cmd[4] = unidades
    sb zero, 5(s1)              # cmd [5] = '\0'
    j success

cmd_three_letter:
    sb t0, 0(s1)                # cmd[0] = primeiro char de op
    sb t1, 1(s1)                # cmd[1] = segundo char de op
    sb t2, 2(s1)                # cmd[2] = terceiro char de op
    sb zero, 3(s1)              # cmd [3] = '\0'
    j success

#----------------------------------------------------RETURN-------------------------------------------------------------------------
# Restaurar a pilha, return 1 se bem sucedido, 0 se houver erro

success:
    lw      ra, 12(sp)
    lw      s0, 8(sp)
    lw      s1, 4(sp)
    lw      s2, 0(sp)
    addi    sp, sp, 16
    li a0, 1
    ret

unsuccess:
    sb zero, 0(s1)              # Clear cmd buffer (cmd[0] = '\0')
    lw      ra, 12(sp)
    lw      s0, 8(sp)
    lw      s1, 4(sp)
    lw      s2, 0(sp)
    addi    sp, sp, 16
    li a0, 0
    ret
