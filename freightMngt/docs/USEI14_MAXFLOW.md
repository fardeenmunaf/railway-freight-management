# USEI14 — Maximum Throughput (Max Flow)

## Objetivo

Calcular o fluxo máximo entre duas estações usando as capacidades das arestas do grafo do projeto para estimar a vazão máxima do corredor.

## Requisitos Implementados

### Entrada
- Grafo dirigido com capacidades nas arestas (numéricas ou `EdgeData`).
- Identificadores de origem e destino (facilityId ou chave interna do grafo).

### Computação
- Algoritmo: **Edmonds–Karp** (variante de Ford–Fulkerson com BFS).
- Integração com o grafo do projeto via `app.service.sprint3.MaxFlowService`.
- Suporta capacidades em tipos numéricos comuns e em `app.model.EdgeData` (mapeado para Integer).
- Validações: vértices inválidos, source == target, capacidades negativas.

### Saída
- `MaxFlow.Result` contendo: source, sink, sourceKey, sinkKey, maxFlow (valor), message, complexity.

## Estrutura de ficheiros (principais)

```
src/main/java/app/
├─ service/sprint3/MaxFlowService.java      # Serviço que resolve vértices e chama a implementação
├─ utils/graph/MaxFlow.java                 # Implementação Edmonds–Karp genérica
├─ view/MaxFlowView.java                    # Visualização/console
```

## Cenários de Teste (implementados)

1. Source == Sink → retorna fluxo 0.
2. Sem caminho entre source e sink → retorna 0 + mensagem informativa.
3. Caso exemplo pequeno (fluxo conhecido) → valida fluxo esperado.
4. Capacidades negativas → lança `IllegalArgumentException`.

## Critérios de Aceitação (mapeamento)

- Input: grafo + source + target — implementado.
- Algoritmo: Edmonds–Karp — implementado (`app.utils.graph.MaxFlow`).
- Respeita capacidades das arestas — implementado (rede residual).
- Output: resultado com maxFlow e mensagem — implementado (`MaxFlow.Result`).
- Validações e testes — implementados (4 cenários).

## Análise de Complexidade (detalhada)

### Notação e pressupostos
- V = número de vértices
- E = número de arestas (para grafos dirigidos)
- Assume-se uma representação da rede residual por listas/Mapas de adjacência como em `app.utils.graph.MaxFlow`.

---

### Operações principais do Edmonds–Karp (implementação do projeto)

1. Construção da rede residual a partir das arestas do grafo:
   - Percorre `g.edges()` e inicializa capacidades residuais (soma capacidades de arestas paralelas) → custo O(E).

2. BFS para encontrar caminho aumentante (por iteração):
   - Cada BFS faz uma travessia tipo nível na rede residual, examinando arestas com capacidade > 0.
   - Custo de um BFS: O(V + E) (varredura de vértices mais arestas acessíveis).

3. Determinar o bottleneck num caminho encontrado:
   - Recorre o caminho via o array `parent` do sink até ao source: O(L) onde L ≤ V (comprimento do caminho).

4. Actualizar capacidades residuais ao longo do caminho:
   - Para cada aresta do caminho faz subtract/add nas estruturas residuais → O(L) por atualização.

5. Repetir enquanto houver caminho aumentante.

---

### Limite do número de iterações (nº de caminhos aumentantes)

- Em Edmonds–Karp provado que cada aresta residual pode trocar de sentido O(V) vezes no pior caso. Isso resulta num limite teórico de O(V·E) caminhos aumentantes no pior caso.

---

### Complexidade temporal total (composição)

- Cada iteração (um caminho aumentante) executa um BFS: O(V + E).
- Número máximo de iterações: O(V · E).

Portanto, tempo total no pior caso: O(V·E · (V + E)). Em grafos onde E = O(V^2) (grafo denso) isso reduz para O(V·E^2) na notação usual dada a dominância de E em muitas representações. A forma simplificada mais comum para Edmonds–Karp é:

- Tempo (Edmonds–Karp, pior caso): O(V · E^2)

Observações:
- Em grafos esparsos (E = O(V)) podemos expressar como O(V^2 · E).
- A implementação prática frequentemente observa desempenho melhor do que o pior caso teórico em grafos reais de transporte.

---

### Complexidade espacial

- Rede residual (Map<Integer, Map<Integer, E>>): O(E) entradas de capacidade (ou até O(V^2) em grafos densos).
- Estruturas auxiliares (fila BFS, parent array): O(V).
- Espaço total: O(V + E) (ou O(V^2) no pior caso de grafo denso).

---

### Casos especiais e custos associados

- `source == sink`: custo O(1) — retorno imediato com fluxo 0.
- Grafo sem arestas/capacidades: custo O(1) para detectar e retornar mensagem (verificações iniciais).
- Aresta directa entre source e sink: serviço tenta optimizar retornando a capacidade directa em O(1) quando disponível.

---

### Exemplo numérico (ordem de grandeza)

Suponha uma rede com V = 500 vértices e E = 1500 arestas (ex.: rede ferroviária regional):
- Complexidade teórica (pior caso): O(V·E^2) ≈ O(500 · 1500^2) = O(500 · 2_250_000) ≈ O(1.125×10^9) operações.
- Na prática, Edmonds–Karp em grafos de transporte costuma terminar bem antes do pior caso teórico.

---

## Observações

- Quando existir uma aresta directa entre source e sink com capacidade conhecida, o serviço tenta otimizar e devolve essa capacidade em O(1).
