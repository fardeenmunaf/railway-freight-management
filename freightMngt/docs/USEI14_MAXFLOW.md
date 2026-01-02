# USEI14 - Fluxo Máximo (Max Flow)

## O que foi implementado

Foi implementado o algoritmo de **Edmonds-Karp** para calcular o fluxo máximo entre duas estações numa rede de transporte. Basicamente, o algoritmo descobre qual é a capacidade máxima que conseguimos enviar de um ponto A para um ponto B, considerando as capacidades de cada ligação.

## Como funciona o algoritmo

O Edmonds-Karp é uma variação do Ford-Fulkerson que usa BFS para encontrar caminhos:

1. Começa com uma rede residual igual ao grafo original
2. Procura um caminho da origem ao destino usando BFS
3. Descobre qual é a capacidade mínima nesse caminho (bottleneck)
4. Atualiza a rede residual:
   - Diminui a capacidade no sentido do fluxo
   - Adiciona capacidade no sentido contrário (para poder "desfazer" se necessário)
5. Repete até não haver mais caminhos possíveis
6. Retorna o fluxo total acumulado

## Análise de Complexidade

### Complexidade Temporal

A complexidade principal do algoritmo é **O(V·E²)**, onde:
- V = número de vértices
- E = número de arestas

**Porquê?**
- Cada BFS demora O(V+E) ≈ O(E) em grafos densos
- Podem existir no máximo O(V·E) caminhos de aumento
- Logo: O(E) × O(V·E) = O(V·E²)

**Casos especiais:**
- Source = Sink: **O(1)** (deteção imediata)
- Aresta direta entre source e sink: **O(1)**
- Vértices inválidos: **O(1)**
- BFS individual: **O(V+E)**
- Construção da rede residual: **O(E)**

### Complexidade Espacial

- **O(V²)** para a rede residual (HashMaps)
- **O(V)** para estruturas auxiliares (queue do BFS, array de parents)

## Implementação

### Classes principais

**MaxFlow.java** (`app.utils.graph`)
- Método `edmondsKarp()` - implementação do algoritmo
- Método `compute()` - wrapper que retorna um objeto Result
- Suporta tipos genéricos para vértices e capacidades
- Funciona com Integer, Double, Long, Float, BigInteger, BigDecimal

**MaxFlowService.java** (`app.service.sprint3`)
- Integra o algoritmo com o resto do projeto
- Resolve vértices por facilityId das estações
- Suporta EdgeData com capacidades
- Validações de grafo e capacidades

**MaxFlow.Result**
- Objeto que encapsula o resultado
- Contém: source, sink, maxFlow, message, complexity

## Testes realizados

Foram desenvolvidos 4 testes principais:

1. **Source = Sink**: Retorna fluxo 0 corretamente 
2. **Sem caminho**: Deteta quando não há ligação entre origem e destino 
3. **Exemplo clássico**: Grafo com fluxo máximo = 5
   ```
   s --3--> a --2--> t
   |        |
   2        1
   ↓        ↓
   b ------3------> t
   ```
   Resultado esperado: 5 

4. **Capacidades negativas**: Lança IllegalArgumentException como esperado 

## Resumo das Complexidades

| Operação | Temporal | Espacial |
|----------|----------|----------|
| Edmonds-Karp (caso geral) | O(V·E²) | O(V²) |
| Source = Sink | O(1) | O(1) |
| Aresta direta | O(1) | O(1) |
| Sem caminho válido | O(V·E²) | O(V²) |
| BFS individual | O(V+E) | O(V) |

