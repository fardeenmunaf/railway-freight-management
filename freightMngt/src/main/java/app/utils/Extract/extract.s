.text
.globl extract_data

extract_data:
    # Guarda o registo de retorno (ra) na pilha para não perder ao chamar funções
    addi    sp, sp, -4
    sw      ra, 0(sp)

    mv      t0, a0          # t0 = ponteiro para a string de entrada (str)

# Loop principal para procurar o token na string
search_loop:
    lb      t1, 0(t0)       # Carrega um caractere da string atual
    beqz    t1, not_found   # Se for '\0' (fim da string), o token não foi encontrado

    # Compara a substring a partir da posição atual (t0) com o token (a1)
    mv      t2, t0          # t2 = ponteiro para a posição atual em str
    mv      t3, a1          # t3 = ponteiro para o token

compare_chars:
    lb      t4, 0(t3)       # Carrega um caractere do token
    lb      t5, 0(t2)       # Carrega um caractere da string

    beqz    t4, token_is_found   # Se o caractere do token for '\0' (fim), o token foi encontrado
    bne     t4, t5, no_match_at_this_position  # Se os caracteres forem diferentes, não é o token

    addi    t2, t2, 1       # Avança para o próximo caractere na string
    addi    t3, t3, 1       # Avança para o próximo caractere no token
    j       compare_chars   # Volta para comparar o próximo caractere

no_match_at_this_position:
    addi    t0, t0, 1       # Avança para o próximo caractere na string
    j       search_loop     # Volta para procurar o token a partir da nova posição

token_is_found:
find_unit_tag:
    lb      t1, 0(t0)       # Carrega o caractere atual da string
    beqz    t1, not_found   # Se for '\0', não encontrou a tag
    li      t2, '&'         # Carrega o caractere '&'
    beq     t1, t2, check_unit_full_tag  # Se for '&', verifica se é a tag completa
    addi    t0, t0, 1       # Avança para o próximo caractere
    j       find_unit_tag   # Volta para procurar a tag

check_unit_full_tag:
    lbu     t1, 1(t0)       # Carrega o caractere seguinte (deve ser 'u')
    li      t2, 'u'
    bne     t1, t2, find_unit_tag  # Se não for 'u', continua procurando
    lbu     t1, 5(t0)       # Carrega o caractere na posição 5 (deve ser ':')
    li      t2, ':'
    bne     t1, t2, find_unit_tag  # Se não for ':', continua procurando

    addi    t0, t0, 6       # Pula "&unit:" (avança 6 posições)

    mv      t3, a2          # t3 = ponteiro para o buffer da unidade
copy_unit_loop:
    lb      t1, 0(t0)       # Carrega o caractere atual da string
    li      t2, ' '         # Carrega o caractere espaço
    beq     t1, t2, unit_copied  # Se for espaço, termina a cópia
    li      t2, '&'         # Carrega o caractere '&'
    beq     t1, t2, unit_copied  # Se for '&', termina a cópia
    beqz    t1, unit_copied      # Se for '\0', termina a cópia

    sb      t1, 0(t3)       # Copia o caractere para o buffer da unidade
    addi    t0, t0, 1       # Avança para o próximo caractere na string
    addi    t3, t3, 1       # Avança para o próximo espaço no buffer
    j       copy_unit_loop  # Volta para copiar o próximo caractere

unit_copied:
    sb      x0, 0(t3)       # Coloca '\0' no final do buffer da unidade

find_value_tag:
    lb      t1, 0(t0)       # Carrega o caractere atual da string
    beqz    t1, not_found   # Se for '\0', não encontrou a tag
    li      t2, '&'         # Carrega o caractere '&'
    beq     t1, t2, check_value_full_tag  # Se for '&', verifica se é a tag completa
    addi    t0, t0, 1       # Avança para o próximo caractere
    j       find_value_tag  # Volta para procurar a tag

check_value_full_tag:
    lbu     t1, 1(t0)       # Carrega o caractere seguinte (deve ser 'v')
    li      t2, 'v'
    bne     t1, t2, find_value_tag  # Se não for 'v', continua procurando
    lbu     t1, 6(t0)       # Carrega o caractere na posição 6 (deve ser ':')
    li      t2, ':'
    bne     t1, t2, find_value_tag  # Se não for ':', continua procurando

    addi    t0, t0, 7       # Pula "&value:" (avança 7 posições)

    li      t5, 0           # t5 = valor acumulado (inicializa a 0)
parse_value_loop:
    lb      t1, 0(t0)       # Carrega o caractere atual da string
    li      t2, '0'         # Carrega o caractere '0'
    blt     t1, t2, value_parsed  # Se for menor que '0', termina a leitura
    li      t2, '9'         # Carrega o caractere '9'
    bgt     t1, t2, value_parsed  # Se for maior que '9', termina a leitura

    addi    t1, t1, -'0'    # Converte o caractere para número
    li      t4, 10          # Carrega o número 10
    mul     t5, t5, t4      # Multiplica o valor acumulado por 10
    add     t5, t5, t1      # Adiciona o novo dígito

    addi    t0, t0, 1       # Avança para o próximo caractere
    j       parse_value_loop  # Volta para ler o próximo dígito

value_parsed:
    sw      t5, 0(a3)       # Guarda o valor lido no endereço fornecido
    li      a0, 1           # Retorna 1 (sucesso)
    j       exit

not_found:
    sb      x0, 0(a2)       # Coloca '\0' no buffer da unidade
    sw      x0, 0(a3)       # Coloca 0 no valor
    li      a0, 0           # Retorna 0 (falha)

exit:
    lw      ra, 0(sp)       # Recupera o registo de retorno da pilha
    addi    sp, sp, 4       # Restaura o ponteiro da pilha
    ret                     # Retorna da função