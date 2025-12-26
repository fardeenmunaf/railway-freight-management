# Caminhos Ferroviários Mais Curtos

## Descrição

Sistema que calcula o **caminho mais curto entre estações ferroviárias belgas**, utilizando:

* **Dijkstra** (pesos positivos)
* **Bellman-Ford** (pesos negativos)

Os dados são carregados a partir dos ficheiros `stations.csv` e `lines.csv`.

---

##  Como Funciona

### 1 Grafo Não Direcionado

* Cada linha do `lines.csv` gera **duas arestas** (A → B e B → A)
* Total de linhas no grafo = `2 × número real de ligações`
* Exemplo:

  ```text
  1266,621,dist,cap,-3.42
  ```

  Gera duas arestas bidirecionais

---

### 2 Lógica de Seleção de Algoritmo

1. Tenta **Dijkstra** primeiro (mais rápido)
2. Se encontrar caminho:

    * Contém arestas negativas? → **Bellman-Ford**
    * Apenas arestas positivas? → **Dijkstra**
3. Se Dijkstra falhar:

    * Tenta **Bellman-Ford**
4. Se Bellman-Ford falhar:

    * Existe **ciclo negativo** (reporta estações e arestas)

---

### 3 Critérios para Bellman-Ford

* Custo total **positivo**, mas caminho contém arestas negativas
* Custo total **negativo**
* Existência de qualquer aresta negativa

 Nestes casos, Bellman-Ford é obrigatório

---

##  Exemplos Práticos

###  Dijkstra (caminho totalmente positivo)

**Input:** `906 → 151`

**Output:**

```text
NIEUWKERKEN(ID:906) → BEVEREN(W)(ID:151) [4.81]
Custo total: 4.81
Algoritmo: Dijkstra
```

---

###  Bellman-Ford (caminho com custo negativo intermédio)

**Input:** `1266 → 1270` (17 estações)

**Output:**

```text
ZELLIK(ID:1266) → JETTE(ID:621) [8.24]
→ BOCKSTAEL(ID:1767) [-2.15] → ...

Custo total: 88.53
Algoritmo: Bellman-Ford
```

> Nota: custo total positivo, mas contém arestas negativas

---

## Estrutura de Ficheiros

```text
src/main/java/app/sampleDataset/
├── stations.csv   # ID, Nome, Latitude, Longitude (559 estações)
└── lines.csv      # ID1, ID2, Distância, Capacidade, Custo
                   # 1382 ligações × 2 (grafo bidirecionado)
```

---

##  Como Usar

1. Colocar os ficheiros `stations.csv` e `lines.csv` em:

   ```text
   src/main/java/app/sampleDataset/
   ```
2. Executar:

   ```java
   ShortestPathUI.display();
   ```
3. Selecionar **Opção 1** e inserir os IDs das estações

---

##  Detalhes Técnicos

---

##  Análise de Complexidade

Esta secção descreve a complexidade temporal e espacial dos algoritmos utilizados, bem como as razões teóricas por trás das suas iterações.

---

###  Dijkstra (sem fila de prioridade)

Esta implementação do algoritmo de **Dijkstra** assume **pesos não negativos** e não utiliza uma fila de prioridade, recorrendo a uma procura linear para selecionar o próximo vértice com menor distância.

#### Excerto de código

```java
while (vOrig != null) {
    vkey = g.key(vOrig);
    visited[vkey] = true;
    for (Edge<V, E> edge : g.outgoingEdges(vOrig)) {
        int vkeyAdj = g.key(edge.getVDest());
        if (!visited[vkeyAdj]) {
            E s = sum.apply(dist[vkey], edge.getWeight());
            if (dist[vkeyAdj] == null || ce.compare(dist[vkeyAdj], s) > 0) {
                dist[vkeyAdj] = s;
                pathKeys[vkeyAdj] = vOrig;
            }
        }
    }
    E minDist = null;
    vOrig = null;
    for (V vert : g.vertices()) {
        int i = g.key(vert);
        if (!visited[i] && dist[i] != null && (minDist == null || ce.compare(dist[i], minDist) < 0)) {
            minDist = dist[i];
            vOrig = vert;
        }
    }
}
```

#### Complexidade temporal

* Cada iteração marca um vértice como visitado → **|V| iterações**
* Relaxamento total das arestas → **O(|E|)**
* Seleção do próximo vértice com menor distância (procura linear) → **O(|V|)** por iteração

[{O(|V|^2 + |E|)}]

Em grafos densos, o termo dominante é **O(|V|²)**. Uma versão com fila de prioridade reduziria a complexidade para **O((|V| + |E|) log |V|)**.

#### Complexidade espacial

* Arrays `visited`, `dist`, `pathKeys` → **O(|V|)**

---

### Bellman-Ford (com deteção de ciclos negativos)

O algoritmo de **Bellman-Ford** permite pesos negativos e deteta ciclos negativos através de uma iteração adicional.

#### Excerto de código — ciclo principal

```java
for (int i = 0; i < numVerts - 1; i++) {
    for (V v : g.vertices()) {
        int vKey = g.key(v);
        if (dist[vKey] != null) {
            for (Edge<V, E> edge : g.outgoingEdges(v)) {
                int adjKey = g.key(edge.getVDest());
                E newDist = sum.apply(dist[vKey], edge.getWeight());
                if (dist[adjKey] == null || ce.compare(newDist, dist[adjKey]) < 0) {
                    dist[adjKey] = newDist;
                    pathKeys[adjKey] = v;
                }
            }
        }
    }
}
```

#### Porque são usadas |V| − 1 iterações?

* O maior caminho simples num grafo sem ciclos tem **|V| − 1 arestas**
* Cada iteração propaga melhorias de distância por mais uma aresta
* Após **|V| − 1 iterações**, todos os caminhos mínimos possíveis já foram considerados

Uma iteração adicional permite verificar se ainda existe melhoria — o que indicaria a existência de um **ciclo negativo**.

#### Excerto de código — deteção de ciclo negativo

```java
for (V v : g.vertices()) {
    int vKey = g.key(v);
    if (dist[vKey] != null) {
        for (Edge<V, E> edge : g.outgoingEdges(v)) {
            int adjKey = g.key(edge.getVDest());
            E newDist = sum.apply(dist[vKey], edge.getWeight());
            if (dist[adjKey] == null || ce.compare(newDist, dist[adjKey]) < 0) {
                shortPath.add(v);
                shortPath.add(edge.getVDest());
                return null; // Ciclo negativo detetado
            }
        }
    }
}
```

#### Complexidade temporal

* Relaxamento das arestas: **(|V| − 1) × |E|**

[oxed{O(|V| · |E|)}]

#### Complexidade espacial

* Arrays `dist`, `pathKeys` e lista `shortPath` → **O(|V|)**

---

#### Comparação Resumida

| Algoritmo    | Pesos Negativos | Ciclos Negativos | Complexidade Temporal |   |     |   |   |
| ------------ | ------------- | -------------- | --------------------- | - | --- | - | - |
| Dijkstra     |  Não          |  Não           | O(                    | V²|+ | E | ) |
| Bellman-Ford |  Sim          |  Sim           | O(                    | V | ·   | E | ) |

### Seleção Automática do Algoritmo

```java
if (dijkstra.encontraCaminho()) {
    if (caminho.temArestaNegativa()) {
        algoritmo = "Bellman-Ford";
    } else {
        algoritmo = "Dijkstra";
    }
} else {
    bellmanFordResultado = bellmanFord.executa();
    if (bellmanFordResultado == null) {
        reportaCicloNegativo();
    }
}
```

---

### Output com Custos Individuais

Cada aresta apresenta o seu custo individual, facilitando a deteção de valores negativos:

```text
ZELLIK → JETTE [8.24] → BOCKSTAEL [-2.15]
                        ↑ NEGATIVO
```

---

## Casos de Teste Validados

| Partida | Chegada | Algoritmo    | Custo Total    | Observações                 |
| ------: | ------: | ------------ | -------------- | --------------------------- |
|     906 |     151 | Dijkstra     | 4.81           | Apenas custos positivos     |
|     351 |     434 | Bellman-Ford | -4.27          | Custo total negativo        |
|    1266 |    1270 | Bellman-Ford | 88.53          | Negativo intermédio         |
|     259 |     258 | Bellman-Ford | Ciclo Negativo | Ciclo detetado corretamente |

---

## Testes Unitários

Os testes unitários foram desenvolvidos com **JUnit** e têm como objetivo validar a correção, robustez e comportamento dos algoritmos em diferentes cenários, incluindo casos normais, casos limite e situações de erro.

Esta abordagem garante que:

* Os caminhos mínimos são corretamente calculados
* Casos inválidos são tratados de forma segura
* Pesos negativos são suportados quando aplicável
* Ciclos são corretamente detetados

---

### Testes ao algoritmo Bellman-Ford

Os testes ao algoritmo **Bellman-Ford** cobrem os seguintes cenários:

* Vértices inexistentes no grafo
* Origem e destino iguais
* Caminhos mínimos válidos em grafos completos e incompletos
* Ausência de caminho possível entre vértices
* Grafos com arestas de peso negativo

```java
@Test
public void testBellmanFord() {
    LinkedList<String> shortPath = new LinkedList<>();

    // Vértice inexistente
    Integer lenPath = Algorithms.bellmanFord(completeMap, "Porto", "LX",
            Integer::compare, Integer::sum, 0, shortPath);
    assertNull(lenPath);
    assertEquals(0, shortPath.size());

    // Origem e destino iguais
    lenPath = Algorithms.bellmanFord(completeMap, "Porto", "Porto",
            Integer::compare, Integer::sum, 0, shortPath);
    assertEquals(Integer.valueOf(0), lenPath);
    assertEquals(Arrays.asList("Porto"), shortPath);

    // Caminho mínimo normal
    lenPath = Algorithms.bellmanFord(incompleteMap, "Porto", "Lisboa",
            Integer::compare, Integer::sum, 0, shortPath);
    assertEquals(Integer.valueOf(335), lenPath);
    assertEquals(Arrays.asList("Porto", "Aveiro", "Coimbra", "Lisboa"), shortPath);

    // Grafo com pesos negativos
    Graph<String, Integer> negativeGraph = new MapGraph<>(true);
    negativeGraph.addVertex("A");
    negativeGraph.addVertex("B");
    negativeGraph.addVertex("C");
    negativeGraph.addEdge("A", "B", 5);
    negativeGraph.addEdge("B", "C", -2);
    negativeGraph.addEdge("A", "C", 4);

    lenPath = Algorithms.bellmanFord(negativeGraph, "A", "C",
            Integer::compare, Integer::sum, 0, shortPath);
    assertEquals(Integer.valueOf(3), lenPath);
    assertEquals(Arrays.asList("A", "B", "C"), shortPath);
}
```

---

### Testes à Ordenação Topológica com Deteção de Ciclos

Os testes à ordenação topológica validam:

* Grafos dirigidos acíclicos (DAG)
* Deteção correta de ciclos
* Identificação dos vértices pertencentes a ciclos
* Lançamento de exceções em grafos não dirigidos

```java
@Test
public void testTopologicalSortWithCycles() {
    Graph<String, Integer> dag = new MapGraph<>(true);
    dag.addVertex("A");
    dag.addVertex("B");
    dag.addVertex("C");
    dag.addVertex("D");
    dag.addVertex("E");
    dag.addEdge("A", "B", 1);
    dag.addEdge("A", "C", 1);
    dag.addEdge("B", "D", 1);
    dag.addEdge("C", "D", 1);
    dag.addEdge("D", "E", 1);

    Algorithms.TopologicalSortResult<String, Integer> result =
            Algorithms.topologicalSortWithCycles(dag);

    assertFalse(result.hasCycles());
    assertEquals("A", result.getTopoOrder().getFirst());
    assertEquals("E", result.getTopoOrder().getLast());

    // Grafo com ciclo
    Graph<String, Integer> cyclicGraph = new MapGraph<>(true);
    cyclicGraph.addVertex("A");
    cyclicGraph.addVertex("B");
    cyclicGraph.addVertex("C");
    cyclicGraph.addVertex("D");
    cyclicGraph.addEdge("A", "B", 1);
    cyclicGraph.addEdge("B", "C", 1);
    cyclicGraph.addEdge("C", "D", 1);
    cyclicGraph.addEdge("D", "B", 1);

    result = Algorithms.topologicalSortWithCycles(cyclicGraph);
    assertTrue(result.hasCycles());
    assertTrue(result.getTopoOrder().isEmpty());
    assertTrue(result.getCycleStations().contains("B"));
    assertTrue(result.getCycleStations().contains("C"));
    assertTrue(result.getCycleStations().contains("D"));
}
```

---

## Javadocs

Todos os métodos estão documentados em **português**, incluindo:

* `@param` — descrição dos parâmetros
* `@return` — valores de retorno
* Exemplos de utilização
* Explicação clara da lógica interna
