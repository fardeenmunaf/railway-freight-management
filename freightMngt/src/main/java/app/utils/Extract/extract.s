.text
.globl extract_data

extract_data:
    # Salva o registrador de retorno na pilha
    addi    sp, sp, -4
    sw      ra, 0(sp)

    mv      t0, a0          # t0 = ponteiro para a string de entrada (str)

# Loop principal para procurar o token na string
search_loop:
    lb      t1, 0(t0)       # Carrega um caractere da string
    beqz    t1, not_found   # Se for o fim da string, o token não foi encontrado

    # Compara a substring a partir da posição atual (t0) com o token (a1)
    mv      t2, t0          # t2 = ponteiro para a posição atual em str
    mv      t3, a1          # t3 = ponteiro para o token
    
compare_chars:
    lb      t4, 0(t3)       # Carrega um caractere do token
    lb      t5, 0(t2)       # Carrega um caractere da string
    
    beqz    t4, token_is_found 
    bne     t4, t5, no_match_at_this_position
    
    addi    t2, t2, 1
    addi    t3, t3, 1
    j       compare_chars

no_match_at_this_position:
    addi    t0, t0, 1
    j       search_loop

token_is_found:
find_unit_tag:
    lb      t1, 0(t0)
    beqz    t1, not_found
    li      t2, '&'
    beq     t1, t2, check_unit_full_tag
    addi    t0, t0, 1
    j       find_unit_tag

check_unit_full_tag:
    lbu     t1, 1(t0)
    li      t2, 'u'
    bne     t1, t2, find_unit_tag
    lbu     t1, 5(t0)
    li      t2, ':'
    bne     t1, t2, find_unit_tag
    
    addi    t0, t0, 6       # Pula "&unit:"
    
    mv      t3, a2          # t3 = ponteiro para o buffer da unidade
copy_unit_loop:
    lb      t1, 0(t0)
    li      t2, ' '
    beq     t1, t2, unit_copied
    li      t2, '&'
    beq     t1, t2, unit_copied
    beqz    t1, unit_copied
    
    sb      t1, 0(t3)
    addi    t0, t0, 1
    addi    t3, t3, 1
    j       copy_unit_loop

unit_copied:
    sb      x0, 0(t3)

find_value_tag:
    lb      t1, 0(t0)
    beqz    t1, not_found
    li      t2, '&'
    beq     t1, t2, check_value_full_tag
    addi    t0, t0, 1
    j       find_value_tag

check_value_full_tag:
    lbu     t1, 1(t0)
    li      t2, 'v'
    bne     t1, t2, find_value_tag
    lbu     t1, 6(t0)
    li      t2, ':'
    bne     t1, t2, find_value_tag
    
    addi    t0, t0, 7       # Pula "&value:"

    li      t5, 0           # t5 = valor acumulado
parse_value_loop:
    lb      t1, 0(t0)
    li      t2, '0'
    blt     t1, t2, value_parsed
    li      t2, '9'
    bgt     t1, t2, value_parsed
    
    addi    t1, t1, -'0'
    
    li      t4, 10
    mul     t5, t5, t4
    add     t5, t5, t1
    
    addi    t0, t0, 1
    j       parse_value_loop

value_parsed:
    sw      t5, 0(a3)
    li      a0, 1
    j       exit

not_found:
    sb      x0, 0(a2)
    sw      x0, 0(a3)
    li      a0, 0

exit:
    lw      ra, 0(sp)
    addi    sp, sp, 4
    ret
