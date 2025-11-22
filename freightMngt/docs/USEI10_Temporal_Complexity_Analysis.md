# USEI10 - Análise de Complexidade Temporal
## Radius Search e Density Summary

**Autor:** G113 2DK  
**Data:** 2025-01-20  
**Versão:** 1.0

---

## 1. Resumo da solução e complexidade principal

Nesta US analisamos o desempenho do algoritmo de pesquisa por raio (radius search) e do cálculo de densidade (density summary) implementados sobre:

- uma 2D-tree (variação de KD-tree) para pesquisa espacial; e
- uma AVL tree para manter os resultados já ordenados.

A tabela seguinte resume a complexidade temporal das operações mais relevantes.

| Operação          | Complexidade Temporal       | Observações principais                                      |
|-------------------|-----------------------------|-------------------------------------------------------------|
| **Radius Search** | **O(√N + k · log k)**       | √N ≈ nós visitados na 2D-tree com poda; k resultados     |
| **Density Summary** | **O(k)**                  | Um único percurso sobre os k resultados                     |
| **Total (USEI10)** | **O(√N + k · log k)**      | Dominado pela fase de radius search                        |

Onde:
- **N** é o número total de estações indexadas na 2D-tree;
- **k** é o número de estações que ficam dentro do raio R;
- em cenários típicos de uso, **k << N** (o raio é relativamente pequeno).

---

## 2. Radius Search na 2D-tree

### 2.1. Método principal `radiusSearchAsTree`

```java
public AVLTree<StationWithDistance, StationWithDistance> radiusSearchAsTree(
    double targetLat, double targetLon, double radiusKm) {

    AVLTree<...> results = new AVLTree<>();
    radiusSearchRecursive(tree.getRoot(), targetLat, targetLon, radiusKm, results);
    return results;
}
```

O método recebe um ponto alvo `(targetLat, targetLon)` e um raio em quilómetros. A partir daí:

1. Cria uma AVL tree vazia para guardar os resultados.
2. Invoca `radiusSearchRecursive` a partir da raiz da 2D-tree.
3. Devolve a AVL com todas as estações encontradas já ordenadas por:
   - distância crescente; e, em empate,
   - nome decrescente.

### 2.2. Complexidade do radius search

A complexidade total do radius search é:

> **O(√N + k · log k)**

Decomposta em duas partes:

| Sub-operação                    | Complexidade       | Explicação resumida                                         |
|---------------------------------|--------------------|--------------------------------------------------------------|
| Percurso da 2D-tree (com poda) | `O(√N)`          | Visita apenas uma fracção dos nós, em média                 |
| Inserção de k estações na AVL   | `O(k · log k)`     | Cada inserção na AVL custa O(log k)                         |

A seguir detalhamos estas duas componentes.

---

## 3. Percurso da 2D-tree com poda

### 3.1. Método `radiusSearchRecursive`

```java
private void radiusSearchRecursive(Node2D node, double targetLat,
                                   double targetLon, double radiusKm,
                                   AVLTree<...> results) {
    if (node == null) return;  // O(1)

    // Processar nó atual
    getNodeStations(node, targetLat, targetLon, radiusKm, results);

    // Determinar coordenada relevante (lat ou lon)
    double targetValue;
    if (node.isLatitude) {
        targetValue = targetLat;
    } else {
        targetValue = targetLon;
    }

    // Decidir se vale a pena visitar a subárvore esquerda
    if (node.left != null && (targetValue - node.key) * 111.0 <= radiusKm) {
        radiusSearchRecursive(node.left, targetLat, targetLon, radiusKm, results);
    }

    // Decidir se vale a pena visitar a subárvore direita
    if (node.right != null && (node.key - targetValue) * 111.0 <= radiusKm) {
        radiusSearchRecursive(node.right, targetLat, targetLon, radiusKm, results);
    }
}
```

Em cada nó:
- processamos o bucket de estações associado ao nó;
- calculamos a distância do ponto alvo ao plano de separação (latitude ou longitude);
- apenas descemos para a esquerda/direita se essa subárvore ainda puder conter pontos dentro do raio.

### 3.2. Por que aparece o termo √N?

**Pior caso (sem poda eficaz):**
- Se o raio for enorme (ou a árvore estiver bastante desbalanceada), podemos acabar por visitar praticamente todos os nós → **O(N)**.

**Caso médio em árvores balanceadas:**
- Em KD-trees/2D-trees equilibradas, a literatura aponta para **O(√N + k)** nós visitados em queries de range.
- Intuitivamente, cada nível divide o espaço; como fazemos poda com base na distância ao plano, a maior parte dos ramos fica logo descartada.
- Em vez de varrermos N nós, o número de nós relevantes cresce aproximadamente com √N.

Assim, no caso típico:

> **Traversal ≈ O(√N)**

---

## 4. Inserções na AVL com resultados do radius search

### 4.1. Método `getNodeStations`

```java
private void getNodeStations(Node2D node, double targetLat, double targetLon,
                             double radiusKm, AVLTree<...> results) {
    for (Station station : node.bucket.stations) {  // O(b)
        double distance = HaversineDistance.calculate(...);  // O(1)

        if (distance <= radiusKm) {  // O(1)
            StationWithDistance swd = new StationWithDistance(station, distance);
            results.insert(swd, swd);  // inserção na AVL: O(log k)
        }
    }
}
```

Cada estação candidata passa por três passos:

1. Cálculo da distância Haversine → custo constante **O(1)**.
2. Comparação `distance <= radiusKm` → **O(1)**.
3. Inserção na AVL tree → **O(log k)**, sendo k o número de elementos já inseridos.

No total, para k estações que acabam por estar dentro do raio:

> **Inserções na AVL:** `O(k · log k)`

---

## 5. Cálculo do Density Summary

### 5.1. Método `calculateDensitySummary`

```java
public DensitySummary calculateDensitySummary(AVLTree<...> resultsTree) {
    Map<String, Integer> byCountry = new HashMap<>();
    int cityCount = 0;
    int nonCityCount = 0;
    int totalCount = 0;

    for (StationWithDistance result : resultsTree.inOrderTraversal()) {  // O(k)
        Station station = result.station;
        totalCount++;  // O(1)

        // Contagem por país
        String country = station.getCountry();
        if (country != null) {
            byCountry.put(country, byCountry.getOrDefault(country, 0) + 1);  // O(1) amortizado
        }

        // Contagem por isCity
        Boolean isCity = station.getIsCity();
        if (isCity != null && isCity) {
            cityCount++;  // O(1)
        } else {
            nonCityCount++;  // O(1)
        }
    }

    return new DensitySummary(byCountry, cityCount, nonCityCount, totalCount);  // O(1)
}
```

A AVL `resultsTree` contém exatamente **k** entradas, uma por estação dentro do raio. O método percorre essa árvore em in-order traversal e, para cada estação, atualiza contadores simples.

- In-order traversal de k nós → **O(k)**;
- Operações em `HashMap` (`getOrDefault` e `put`) → **O(1)** amortizado por chamada;
- Atualização de contadores inteiros → **O(1)**.

Logo, a complexidade total desta fase é:

> **Density Summary:** `O(k)`

---

## 6. Cenários típicos de utilização

Para ter noção da ordem de grandeza, assumimos um dataset de referência com:

- **N = 10 000** estações na 2D-tree;
- Raio e distribuição espacial a variar consoante o cenário.

### 6.1. Raio pequeno (≈10 km)

Exemplo:

```text
N = 10 000
k = 15 estações dentro do raio
```

Estimativa de operações:

| Etapa               | Estimativa                   |
|---------------------|------------------------------|
| Traversal 2D-tree   | √10 000 ≈ 100 nós            |
| Inserções na AVL    | 15 · log₂(15) ≈ 60          |
| Density summary     | 15                           |
| **Total aproximado** | **~175 operações**          |

Neste cenário, só uma pequena fracção das estações é de facto processada.

### 6.2. Raio médio (≈50 km)

```text
N = 10 000
k = 150 estações dentro do raio
```

| Etapa               | Estimativa                        |
|---------------------|-----------------------------------|
| Traversal 2D-tree   | √10 000 ≈ 100 nós                 |
| Inserções na AVL    | 150 · log₂(150) ≈ 1 125          |
| Density summary     | 150                               |
| **Total aproximado** | **~1 375 operações**             |

Mesmo assim, continuamos bastante abaixo de um percurso completo de N elementos.

### 6.3. Raio grande (≈500 km) – caso desfavorável

```text
N = 10 000
k = 5 000 estações dentro do raio (50% do dataset)
```

| Etapa               | Estimativa                         |
|---------------------|------------------------------------|
| Traversal 2D-tree   | ~N/2 ≈ 5 000 nós                   |
| Inserções na AVL    | 5 000 · log₂(5 000) ≈ 61 400      |
| Density summary     | 5 000                              |
| **Total aproximado** | **~71 400 operações**             |

Aqui já nos aproximamos mais do custo de uma abordagem “brute force”, mas ainda com estruturação e ordenação automáticas.

---

## 7. Comparação com alternativas simples

### 7.1. Abordagem 1 – Brute Force sem 2D-tree

```java
for (Station s : allStations) {        // O(N)
    double distance = haversine(...);  // O(1)
    if (distance <= radius) {
        results.add(s);                // O(1)
    }
}
Collections.sort(results);             // O(k · log k)
```

Complexidade:

> **Brute force:** `O(N + k · log k)`

Comparação:

- **Implementação USEI10:** `O(√N + k · log k)`
- **Brute force:** `O(N + k · log k)`

O ganho está essencialmente em substituir o termo **N** por **√N** na parte de pesquisa espacial.

### 7.2. Abordagem 2 – Usar apenas lista e sort final

```java
List<StationWithDistance> results = new ArrayList<>();
// radiusSearchRecursive preenche a lista
Collections.sort(results);  // O(k · log k)
```

Nesta abordagem também se obtém `O(√N + k · log k)`, mas:

- é necessário manter uma lista adicional;
- a ordenação só acontece no fim;
- na nossa solução, a AVL tree garante ordenação incremental e uma API homogénea com o resto do projeto.

---

## 8. Complexidade espacial

| Estrutura                      | Espaço       | Comentário                           |
|--------------------------------|--------------|--------------------------------------|
| 2D-tree (todas as estações)   | O(N)         | Estrutura principal de indexação     |
| AVL com resultados             | O(k)         | Apenas estações dentro do raio       |
| HashMap `byCountry`           | O(c)         | c = nº de países distintos (c << k)  |
| Contadores simples             | O(1)         | cityCount, nonCityCount, totalCount |
| **Total**                      | **O(N + k)** | Dominado pela 2D-tree                |

Importa notar que a 2D-tree é construída uma vez (no carregamento/índice inicial) e reutilizada em múltiplas queries, amortizando o custo de construção.

---

## 9. Conclusão

A combinação 2D-tree + AVL tree para o USEI10 conduz à seguinte complexidade global:

> **Complexidade final:** `O(√N + k · log k)`

Em resumo:

- Para raios pequenos e médios (caso mais comum), **k é muito menor que N**, pelo que a componente `√N` é bastante favorável face a uma pesquisa linear.
- A AVL garante que os resultados ficam sempre ordenados sem passos adicionais de sort.
- O cálculo do density summary é linear no número de resultados, `O(k)`, e portanto mais eficiente comparado com o radius search.

Face a uma solução inadequada (brute force), esperamos beneficios claros em datasets com milhares de estações e com consultas frequentes.

---

**Documento elaborado para USEI10 – Grupo G113 2DK**  
**ISEP – LAPR3 – 2024/2025**
