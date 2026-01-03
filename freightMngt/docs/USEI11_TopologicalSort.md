# Topological Sort com Deteção de Ciclos

Este documento descreve a implementação do algoritmo **Topological Sort com deteção de ciclos**, bem como os **testes unitários** associados e a respetiva **análise de complexidade**.

---

## 📌 Objetivo

O objetivo deste algoritmo é:

* Calcular a **ordem topológica** de um grafo dirigido **se e apenas se** o grafo for acíclico (DAG).
* **Detetar ciclos** caso existam, identificando:

    * Os **vértices** que pertencem a ciclos (`cycleStations`)
    * As **arestas** que fazem parte desses ciclos (`cycleEdges`)

Caso o grafo contenha ciclos, **não é devolvida ordem topológica**.

---

## 🧠 Visão Geral da Abordagem

A solução baseia-se em:

* **DFS (Depth-First Search)**
* Uma **pilha de recursão** para deteção de *back-edges*
* Uma **pilha auxiliar** para identificar todos os vértices envolvidos num ciclo

A abordagem segue estes passos:

1. Percorrer todos os vértices do grafo (incluindo grafos desconexos)
2. Executar DFS a partir de cada vértice não visitado
3. Detetar ciclos através da pilha de recursão
4. Caso não existam ciclos, construir a ordem topológica
5. Caso existam ciclos, recolher vértices e arestas envolvidas

---

## 🧩 Método `topologicalSortWithCycles`

```java
public static <V, E> TopologicalSortResult<V, E> topologicalSortWithCycles(Graph<V, E> g)
```

### 🔎 Função

* Executa um **topological sort** num grafo dirigido
* Deteta ciclos durante a execução

### 🧪 Comportamento

* Se o grafo **não for dirigido**, lança exceção
* Se **não houver ciclos**:

    * Retorna uma ordem topológica válida
* Se **existirem ciclos**:

    * Retorna a ordem vazia
    * Indica os vértices e arestas que assumem parte em ciclos

### 🗂️ Estruturas usadas

* `visited` – vértices já explorados
* `recStack` – vértices na pilha de recursão (deteção de ciclos)
* `stack` – pilha de DFS para identificar vértices do ciclo
* `topoOrder` – lista com a ordem topológica
* `cycleStations` – conjunto de vértices em ciclos
* `cycleEdges` – conjunto de arestas em ciclos

---

## 🔁 Método `dfsTopological`

```java
private static <V, E> void dfsTopological(...)
```

### 🔎 Função

Executa DFS a partir de um vértice, com os seguintes objetivos:

* Marcar vértices visitados
* Detetar *back-edges* (`v -> adj` onde `adj` está na pilha de recursão)
* Identificar todos os vértices que formam o ciclo
* Construir a ordem topológica (caso não haja ciclos)

### ⚠️ Deteção de ciclos

Um ciclo é detetado quando:

```java
recStack.contains(adj)
```

Nesse caso:

* Percorre-se a pilha (`stack`) até ao vértice `adj`
* Todos os vértices encontrados são marcados como pertencentes ao ciclo

---

## 🔗 Método `collectCycleEdges`

```java
private static <V, E> void collectCycleEdges(...)
```

### 🔎 Função

Após identificar os vértices em ciclos:

* Percorre todas as arestas de saída desses vértices
* Seleciona as arestas cujo destino também pertence ao conjunto `cycleStations`

Estas arestas constituem os **ciclos explícitos do grafo**.

---

## ✅ Testes Unitários

Os testes garantem o correto funcionamento do algoritmo em diferentes cenários.

### 1️⃣ Grafo Acíclico Simples

**Estrutura:**

```
A -> B
A -> C
B -> D
C -> D
```

**Verificações:**

* Não existem ciclos
* Ordem topológica válida
* Restrições de precedência são respeitadas

---

### 2️⃣ Ciclo Simples

**Estrutura:**

```
A -> B -> C -> A
```

**Verificações:**

* Ciclo corretamente detetado
* Ordem topológica vazia
* `cycleStations = {A, B, C}`
* Todas as arestas fazem parte do ciclo

---

### 3️⃣ Grafo Desconexo com Ciclo

**Estrutura:**

* Componente 1 (DAG): `A -> B -> C`
* Componente 2 (ciclo): `D <-> E`

**Verificações:**

* Ciclo detetado apenas na segunda componente
* Ordem topológica global vazia
* `cycleStations = {D, E}`

---

### 4️⃣ Grafo Vazio

**Verificações:**

* Nenhum ciclo
* Ordem topológica vazia
* Conjuntos de ciclos vazios

---

### 5️⃣ Teste de Fluxo Completo

Valida o comportamento completo do algoritmo:

* Execução correta num DAG
* Execução correta num grafo com ciclo
* Resultados consistentes de ponta a ponta

---

## ⏱️ Análise de Complexidade

### ⌛ Complexidade Temporal

O algoritmo tem complexidade:

```
O(V + E)
```

Onde:

* `V` é o número de vértices
* `E` é o número de arestas

### 📌 Justificação

* Cada vértice é visitado **uma única vez** pela DFS
* Cada aresta é explorada **no máximo uma vez**
* A recolha de arestas de ciclo percorre apenas as arestas associadas aos vértices em ciclo

Assim, o custo total mantém-se linear relativamente ao tamanho do grafo.

---

### 💾 Complexidade Espacial

Também é:

```
O(V + E)
```

Devido a:

* Estruturas auxiliares (`visited`, `recStack`, `stack`)
* Armazenamento da ordem topológica
* Armazenamento de vértices e arestas em ciclos