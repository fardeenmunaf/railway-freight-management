.text
.global extract_data

extract_data:
    addi    sp, sp, -4          # Reserva espaço na pilha
    sw      ra, 0(sp)           # Guarda o registo de retorno
    mv      t0, a0              # t0 = ponteiro para a string de entrada

# Procura o token na string (apenas no início ou após '#')
search_loop:
    lb      t1, 0(t0)           # Carrega o caractere atual
    beqz    t1, not_found       # Se for '\0', token não encontrado
    beq     t0, a0, try_match   # Se está no início, tenta match
    lb      t6, -1(t0)          # Carrega o caractere anterior
    li      t4, '#'             # Verifica se é '#'
    bne     t6, t4, skip_match  # Se não for '#', avança sem match

# Compara o token com a substring atual
try_match:
    mv      t2, t0              # t2 = posição atual na string
    mv      t3, a1              # t3 = posição atual no token

compare_chars:
    lb      t4, 0(t3)           # Carrega caractere do token
    lb      t5, 0(t2)           # Carrega caractere da string
    beqz    t4, check_end       # Se token acabou, verifica delimitador
    bne     t4, t5, skip_match  # Se diferentes, não é match
    addi    t2, t2, 1           # Avança na string
    addi    t3, t3, 1           # Avança no token
    j       compare_chars       # Continua comparando

check_end:
    lb      t6, 0(t2)           # Carrega próximo caractere
    li      t4, '&'             # Verifica se é '&'
    beq     t6, t4, found       # Se for '&', token válido encontrado
    beqz    t6, found           # Se for '\0', também é válido

skip_match:
    addi    t0, t0, 1           # Avança para próximo caractere
    j       search_loop         # Continua procurando

# Token encontrado, agora procura "&unit:"
found:
    lb      t1, 0(t0)           # Carrega caractere atual
    beqz    t1, not_found       # Se '\0', não há dados
    li      t2, '&'             # Procura '&'
    bne     t1, t2, next_char   # Se não for '&', avança
    lb      t4, 1(t0)           # Carrega próximo caractere
    li      t5, 'u'             # Verifica 'u' de "unit"
    bne     t4, t5, next_char   # Se não for 'u', avança
    lb      t4, 5(t0)           # Carrega 6º caractere
    li      t5, ':'             # Verifica ':'
    beq     t4, t5, copy_unit   # Se for ':', copia unidade

next_char:
    addi    t0, t0, 1           # Avança caractere
    j       found               # Continua procurando "&unit:"

# Copia a unidade para o buffer
copy_unit:
    addi    t0, t0, 6           # Salta "&unit:"
    mv      t3, a2              # t3 = buffer da unidade

copy_unit_loop:
    lb      t1, 0(t0)           # Carrega caractere
    li      t2, ' '             # Verifica espaço
    beq     t1, t2, end_unit    # Se espaço, termina
    li      t2, '&'             # Verifica '&'
    beq     t1, t2, end_unit    # Se '&', termina
    beqz    t1, end_unit        # Se '\0', termina
    sb      t1, 0(t3)           # Copia caractere
    addi    t0, t0, 1           # Avança na string
    addi    t3, t3, 1           # Avança no buffer
    j       copy_unit_loop      # Continua copiando

end_unit:
    sb      x0, 0(t3)           # Termina string com '\0'

# Procura "&value:"
find_value:
    lb      t1, 0(t0)           # Carrega caractere atual
    beqz    t1, not_found       # Se '\0', não há valor
    li      t2, '&'             # Procura '&'
    bne     t1, t2, next_val    # Se não for '&', avança
    lb      t4, 1(t0)           # Carrega próximo caractere
    li      t5, 'v'             # Verifica 'v' de "value"
    bne     t4, t5, next_val    # Se não for 'v', avança
    lb      t4, 6(t0)           # Carrega 7º caractere
    li      t5, ':'             # Verifica ':'
    beq     t4, t5, parse_val   # Se for ':', faz parse do valor

next_val:
    addi    t0, t0, 1           # Avança caractere
    j       find_value          # Continua procurando "&value:"

# Converte string numérica para inteiro
parse_val:
    addi    t0, t0, 7           # Salta "&value:"
    li      t5, 0               # t5 = acumulador do valor

parse_loop:
    lb      t1, 0(t0)           # Carrega caractere
    li      t2, '0'             # Verifica se >= '0'
    blt     t1, t2, done_parse  # Se < '0', termina
    li      t2, '9'             # Verifica se <= '9'
    bgt     t1, t2, done_parse  # Se > '9', termina
    addi    t1, t1, -'0'        # Converte ASCII para número
    li      t4, 10              # Multiplica por 10
    mul     t5, t5, t4          # valor = valor * 10
    add     t5, t5, t1          # valor = valor + dígito
    addi    t0, t0, 1           # Avança caractere
    j       parse_loop          # Continua lendo dígitos

done_parse:
    sw      t5, 0(a3)           # Guarda valor na memória
    li      a0, 1               # Retorna 1 (sucesso)
    j       exit_func           # Sai da função

# Token não encontrado ou dados incompletos
not_found:
    sb      x0, 0(a2)           # Limpa buffer da unidade
    sw      x0, 0(a3)           # Zera o valor
    li      a0, 0               # Retorna 0 (falha)

exit_func:
    lw      ra, 0(sp)           # Recupera registo de retorno
    addi    sp, sp, 4           # Liberta espaço da pilha
    ret                         # Retorna da função