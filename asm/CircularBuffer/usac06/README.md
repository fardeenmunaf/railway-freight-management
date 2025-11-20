# Circular Buffer - Dequeue Value (USAC06) - RISC-V 32I Implementation

## Descrição

Implementação da função **dequeue_value** para remover valores de um buffer circular (ring buffer) em assembly RISC-V 32I. A função implementa comportamento FIFO (First-In, First-Out).

## User Story

**Como** Product Owner, **quero** que o sistema seja capaz de remover e recuperar o valor mais antigo do buffer circular.

## Acceptance Criteria

✅ Implementar função `int dequeue_value(int* buffer, int length, int* nelem, int* tail, int* head, int* value)` em assembly (convenção RISC-V)

✅ Remover o elemento mais antigo (apontado por `tail`) do buffer e armazená-lo no ponteiro `value`

✅ Atualizar corretamente os ponteiros `nelem`, `tail` e `head` após remoção

✅ Retornar `1` em sucesso e `0` em falha (buffer vazio)

## Estrutura dos Ficheiros

```
CircularBuffer/usac06/
├── dequeue_value.s       # Implementação em assembly RISC-V 32I
├── main.c                # Programa de teste em C
├── asm.h                 # Header com declaração da função
├── callfunc.s            # Helper para chamadas de função
├── unity.c               # Framework de testes (copiar da usac05)
├── unity.h               # Header do Unity (copiar da usac05)
├── unity_internals.h     # Internals do Unity (copiar da usac05)
├── Makefile              # Script de compilação
└── README.md             # Esta documentação
```

## Assinatura da Função

```c
int dequeue_value(int* buffer, int length, int* nelem, int* tail, int* head, int* value)
```

### Parâmetros

- **`buffer`** (a0): Ponteiro para o array de inteiros (buffer circular)
- **`length`** (a1): Capacidade máxima do buffer
- **`nelem`** (a2): Ponteiro para o número atual de elementos
- **`tail`** (a3): Ponteiro para o índice do elemento mais antigo
- **`head`** (a4): Ponteiro para o índice onde inserir o próximo elemento
- **`value`** (a5): Ponteiro para armazenar o valor removido

### Retorno

- **`1`**: Sucesso - valor foi removido
- **`0`**: Falha - buffer está vazio

## Lógica do Algoritmo

1. **Verificar se buffer está vazio** (`nelem == 0`)
   - Se vazio, retornar `0` (falha)
2. **Ler valor** na posição `buffer[tail]`
3. **Armazenar valor** no ponteiro `*value`
4. **Incrementar tail** com wrap-around (volta a 0 quando atinge `length`)
5. **Decrementar nelem** (`nelem--`)
6. **Retornar** `1` (sucesso)

## Diferenças entre Enqueue e Dequeue

### Enqueue (USAC05)
- Insere em `head`
- Incrementa `head` e `nelem`
- Pode mover `tail` se buffer cheio
- Verifica se ficou cheio

### Dequeue (USAC06)
- Remove de `tail`
- Incrementa `tail`
- Decrementa `nelem`
- Verifica se está vazio

## Compilação e Execução

### Pré-requisitos

Antes de compilar, copie os arquivos Unity da pasta usac05:

```bash
cp ../usac05/unity.c .
cp ../usac05/unity.h .
cp ../usac05/unity_internals.h .
```

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

### Teste 1: Buffer Vazio
Remove de um buffer vazio:
- Resultado esperado: Falha (retorna 0)

### Teste 2: Remover Um Elemento
Remove único elemento do buffer:
- Buffer: [5, 0, 0], nelem=1, tail=0
- Resultado esperado: value=5, nelem=0, tail=1, sucesso

### Teste 3: Remover com Wrap-around
Remove elemento quando tail está no final:
- Buffer: [0, 0, 5], nelem=1, tail=2
- Resultado esperado: value=5, tail=0 (wrap), sucesso

### Teste 4: Remover de Buffer Cheio
Remove primeiro elemento de buffer cheio:
- Buffer: [1, 2, 3, 4], nelem=4, tail=0
- Resultado esperado: value=1, nelem=3, tail=1

### Teste 5: Remover Múltiplos
Testa remoção sequencial:
- Buffer: [10, 20, 30], nelem=3, tail=0
- Resultado esperado: value=10, nelem=2, tail=1

### Teste 6: Remover Último
Remove o último elemento restante:
- Buffer: [99, 0, 0], nelem=1, tail=0
- Resultado esperado: value=99, nelem=0, buffer vazio

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

### Verificação de Buffer Vazio

```assembly
lw t0, 0(a2)            # t0 = *nelem
beqz t0, buffer_empty   # Se nelem == 0, buffer vazio
```

### Wrap-around Logic

```assembly
addi t1, t1, 1          # Incrementar tail
blt t1, a1, no_wrap     # Se tail < length, não wrap
li t1, 0                # Caso contrário, tail = 0
no_wrap:
```

### Atualização de Ponteiros

Todos os ponteiros (`nelem`, `tail`) são atualizados na memória usando `sw` (store word), garantindo que as mudanças persistem após a função retornar.

## Complexidade

- **Tempo**: O(1) - todas as operações são de tempo constante
- **Espaço**: O(1) - usa apenas registos temporários e pilha fixa

## Exemplo de Uso em C

```c
#include "asm.h"

int main() {
    int buffer[5] = {10, 20, 30, 0, 0};
    int length = 5;
    int nelem = 3;
    int tail = 0;
    int head = 3;
    int value;
    
    // Remover valor
    int result = dequeue_value(buffer, length, &nelem, &tail, &head, &value);
    
    if (result == 1) {
        printf("Valor removido: %d\n", value);  // Output: 10
        printf("Elementos restantes: %d\n", nelem);  // Output: 2
        printf("Novo tail: %d\n", tail);  // Output: 1
    } else {
        printf("Buffer vazio!\n");
    }
    
    return 0;
}
```

## Compatibilidade com USAC05

Esta implementação funciona em conjunto com a USAC05 (enqueue_value), permitindo:
- Inserir valores com `enqueue_value`
- Remover valores com `dequeue_value`
- Manter comportamento FIFO completo

## Autor

Implementado para a disciplina de **Laboratório de Programação 3** (LP3) - ISEP 2025

## Licença

Projeto académico - Sem3-PI-25-26-G113

