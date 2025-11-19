# Circular Buffer (Ring Buffer) - RISC-V 32I Implementation

## Descrição

Implementação de um **buffer circular** (ring buffer) em assembly RISC-V 32I. O buffer permite inserir valores de forma eficiente com tamanho fixo de memória, sobrescrevendo automaticamente os elementos mais antigos quando está cheio.

## User Story

**Como** Product Owner, **quero** que o sistema seja capaz de inserir novos valores num buffer circular, sobrescrevendo o elemento mais antigo se o buffer estiver cheio (comportamento de ring buffer).

## Acceptance Criteria

✅ Implementar função `int enqueue_value(int* buffer, int length, int* nelem, int* tail, int* head, int value)` em assembly (convenção RISC-V)

✅ Inserir o novo valor na posição `head` do buffer, mantendo a ordem de inserção correta

✅ Atualizar corretamente os ponteiros `nelem`, `tail` e `head` após inserção

✅ Retornar `1` se o buffer está cheio após inserção, `0` caso contrário

## Estrutura dos Ficheiros

```
CircularBuffer/
├── enqueue_value.s    # Implementação em assembly RISC-V 32I
├── main.c             # Programa de teste em C
├── Makefile           # Script de compilação
└── README.md          # Esta documentação
```

## Assinatura da Função

```c
int enqueue_value(int* buffer, int length, int* nelem, int* tail, int* head, int value)
```

### Parâmetros

- **`buffer`** (a0): Ponteiro para o array de inteiros (buffer circular)
- **`length`** (a1): Capacidade máxima do buffer
- **`nelem`** (a2): Ponteiro para o número atual de elementos
- **`tail`** (a3): Ponteiro para o índice do elemento mais antigo
- **`head`** (a4): Ponteiro para o índice onde inserir o próximo elemento
- **`value`** (a5): Valor a inserir no buffer

### Retorno

- **`1`**: Buffer está cheio após a inserção
- **`0`**: Buffer não está cheio

## Lógica do Algoritmo

1. **Inserir valor** na posição `buffer[head]`
2. **Incrementar head** com wrap-around (volta a 0 quando atinge `length`)
3. **Se buffer não estava cheio:**
   - Incrementar `nelem`
   - Verificar se ficou cheio após inserção
4. **Se buffer estava cheio:**
   - Incrementar `tail` com wrap-around (elemento mais antigo é sobrescrito)
   - Manter `nelem = length`
5. **Retornar** estado do buffer (cheio ou não)

## Compilação e Execução

### Requisitos

- **GCC RISC-V 32-bit**: `riscv32-linux-gcc`
- **QEMU RISC-V**: `qemu-riscv32`
- **Ambiente**: Linux, WSL ou AntiX

### Compilar

```bash
make
```

### Executar

```bash
make run
```

### Limpar ficheiros gerados

```bash
make clean
```

## Testes Implementados

### Teste 1: Inserir sem encher
Insere 3 valores num buffer de capacidade 5:
- Valores: 10, 20, 30
- Resultado esperado: Buffer não cheio

### Teste 2: Encher completamente
Insere mais 2 valores:
- Valores: 40, 50
- Resultado esperado: Buffer cheio após último elemento

### Teste 3: Sobrescrever (Ring Buffer)
Insere 3 valores adicionais com buffer cheio:
- Valores: 60, 70, 80
- Resultado esperado: Elementos mais antigos são sobrescritos
- Tail avança automaticamente

### Teste 4: Modo Interativo
Permite ao utilizador inserir valores e observar o comportamento do buffer em tempo real.

## Exemplo de Saída

```
=== Estado do Buffer ===
Capacidade: 5
Elementos:  5
Tail:       3
Head:       3
Buffer:     [60, 70, 80, 40, 50]
Ordem:      [40, 50, 60, 70, 80] (tail->head)
========================
```

## Convenções RISC-V

### Registos Utilizados

- **a0-a5**: Parâmetros de entrada
- **a0**: Valor de retorno
- **t0-t3**: Registos temporários (não preservados)
- **s0-s1**: Registos salvos (preservados na pilha)
- **sp**: Stack pointer
- **ra**: Return address

### Calling Convention

A função segue a convenção de chamada RISC-V:
- Preserva registos salvos (`s0`, `s1`)
- Usa pilha para salvar/restaurar registos
- Retorna via instrução `ret`

## Detalhes da Implementação

### Wrap-around Logic

```assembly
addi t0, t0, 1          # Incrementar índice
blt t0, a1, no_wrap     # Se índice < length, não wrap
li t0, 0                # Caso contrário, índice = 0
no_wrap:
```

### Atualização de Ponteiros

Todos os ponteiros (`nelem`, `tail`, `head`) são atualizados na memória usando `sw` (store word), garantindo que as mudanças persistem após a função retornar.

## Complexidade

- **Tempo**: O(1) - todas as operações são de tempo constante
- **Espaço**: O(1) - usa apenas registos temporários e pilha fixa

## Autor

Implementado para a disciplina de **Laboratório de Programação 3** (LP3) - ISEP 2025

## Licença

Projeto académico - Sem3-PI-25-26-G113
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

