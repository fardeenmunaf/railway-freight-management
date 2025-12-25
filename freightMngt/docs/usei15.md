# Caminhos Ferroviários Mais Curtos

##  Descrição

Sistema que calcula o **caminho mais curto entre estações ferroviárias belgas**, utilizando:

* **Dijkstra** (pesos positivos)
* **Bellman-Ford** (pesos negativos)

Os dados são carregados a partir dos ficheiros `stations.csv` e `lines.csv`.

---

## ️ Como Funciona

###  Grafo Não Direcionado

* Cada linha do `lines.csv` gera **duas arestas** (A → B e B → A)
* Total de linhas no grafo = `2 × número real de ligações`
* Exemplo:

  ```text
  1266,621,dist,cap,-3.42
  ```

  Gera duas arestas bidirecionais

---

###  Lógica de Seleção de Algoritmo

1. Tenta **Dijkstra** primeiro (mais rápido)
2. Se encontrar caminho:

    * Contém arestas negativas? → **Bellman-Ford**
    * Apenas arestas positivas? → **Dijkstra**
3. Se Dijkstra falhar:

    * Tenta **Bellman-Ford**
4. Se Bellman-Ford falhar:

    * Existe **ciclo negativo** (reporta estações e arestas)

---

###  Critérios para Bellman-Ford

* Custo total **positivo**, mas caminho contém arestas negativas
* Custo total **negativo**
* Existência de qualquer aresta negativa

 Nestes casos, Bellman-Ford é obrigatório

---

## Exemplos Práticos

###  Dijkstra (caminho totalmente positivo)

**Input:** `906 → 151`

**Output:**

```text
NIEUWKERKEN(ID:906) → BEVEREN(W)(ID:151) [4.81]
Custo total: 4.81
Algoritmo: Dijkstra
```

---

### Bellman-Ford (caminho com custo negativo intermédio)

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

##  Estrutura de Ficheiros

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

###  Dijkstra

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

[oxed{O(|V|^2 + |E|)}]

Em grafos densos, o termo dominante é **O(|V|²)**. Uma versão com fila de prioridade reduziria a complexidade para **O((|V| + |E|) log |V|)**.

#### Complexidade espacial

* Arrays `visited`, `dist`, `pathKeys` → **O(|V|)**

---

###  Bellman-Ford

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

### ⚖️ Comparação Resumida

| Algoritmo    | Pesos Negativos | Ciclos Negativos | Complexidade Temporal |   |   |   |   |
| ------------ | ------------- |---------------| ---- | - |---| - | - |
| Dijkstra     |  Não          |  Não          | O( V²| + | E | ) |
| Bellman-Ford |  Sim          |  Sim          | O( V | x | E | ) |

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

## Javadocs

Todos os métodos estão documentados em **português**, incluindo:

* `@param` — descrição dos parâmetros
* `@return` — valores de retorno
* Exemplos de utilização
* Explicação clara da lógica interna
