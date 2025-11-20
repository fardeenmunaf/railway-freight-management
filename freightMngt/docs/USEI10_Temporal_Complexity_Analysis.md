# USEI10 - Temporal Complexity Analysis
## Radius Search and Density Summary

**Autor:** G113 2DK  
**Data:** 2025-01-20  
**Versão:** 1.0

---

## 📊 Resumo Executivo

| Operação | Complexidade Temporal | Explicação |
|----------|----------------------|------------|
| **Radius Search** | **O(√N + k log k)** | √N = traversal da 2D-tree com pruning; k log k = inserções na AVL |
| **Density Summary** | **O(k)** | Percorre k resultados uma vez |
| **Total (USEI10)** | **O(√N + k log k)** | Dominado pelo radius search |

Onde:
- **N** = Total de estações na 2D-tree
- **k** = Número de estações dentro do raio R
- Tipicamente: **k << N** (k é muito menor que N)

---

## 🔍 Análise Detalhada por Componente

### **1. Radius Search na 2D-Tree**

#### **Método:** `radiusSearchAsTree()`

```java
public AVLTree<StationWithDistance, StationWithDistance> radiusSearchAsTree(
    double targetLat, double targetLon, double radiusKm) {
    
    AVLTree<...> results = new AVLTree<>();
    radiusSearchRecursive(tree.getRoot(), targetLat, targetLon, radiusKm, results);
    return results;
}
```

#### **Complexidade:** `O(√N + k log k)`

**Decomposição:**

| Sub-operação | Complexidade | Justificação |
|--------------|--------------|--------------|
| Traversal da 2D-tree | `O(√N)` | Com pruning espacial eficiente |
| Inserção de k estações na AVL | `O(k log k)` | Cada inserção é O(log k) |
| **Total** | **O(√N + k log k)** | Soma das operações |

---

### **1.1. Traversal da 2D-Tree: O(√N)**

#### **Método:** `radiusSearchRecursive()`

```java
private void radiusSearchRecursive(Node2D node, double targetLat, 
                                   double targetLon, double radiusKm, 
                                   AVLTree<...> results) {
    if (node == null) return;  // O(1)

    // Processar nó atual: O(b) onde b = tamanho do bucket
    getNodeStations(node, targetLat, targetLon, radiusKm, results);

    // Calcular distância ao plano divisor: O(1)
    double targetValue;
    if (node.isLatitude) {
        targetValue = targetLat;
    } else {
        targetValue = targetLon;
    }

    // PRUNING: Decidir se visita subárvores
    if (node.left != null && (targetValue - node.key) * 111.0 <= radiusKm) {
        radiusSearchRecursive(node.left, ...);  // Condicional
    }

    if (node.right != null && (node.key - targetValue) * 111.0 <= radiusKm) {
        radiusSearchRecursive(node.right, ...);  // Condicional
    }
}
```

#### **Análise do Pruning:**

**Sem pruning (pior caso):**
- Visita **todos os nós**: `O(N)`
- Acontece quando raio é muito grande ou árvore desbalanceada

**Com pruning (caso médio):**
- Visita apenas nós **próximos** do ponto alvo
- Para range queries em KD-trees balanceadas: **O(√N + k)**
- **√N** = número esperado de nós visitados com pruning espacial

**Intuição:**
- 2D-tree divide o espaço em regiões
- Pruning elimina ~70-90% dos ramos
- Em vez de visitar todos N nós, visita ~√N nós

---

### **1.2. Inserção na AVL Tree: O(k log k)**

#### **Método:** `getNodeStations()`

```java
private void getNodeStations(Node2D node, double targetLat, double targetLon, 
                             double radiusKm, AVLTree<...> results) {
    for (Station station : node.bucket.stations) {  // O(b)
        double distance = HaversineDistance.calculate(...);  // O(1)

        if (distance <= radiusKm) {  // O(1)
            StationWithDistance swd = new StationWithDistance(station, distance);
            results.insert(swd, swd);  // ← O(log k) inserção na AVL
        }
    }
}
```

#### **Análise:**

- Cada estação dentro do raio é inserida na AVL tree
- **k** estações dentro do raio → **k inserções**
- Cada inserção na AVL: **O(log k)** (altura da árvore)
- **Total:** `k × log k = O(k log k)`

**Exemplo:**
- k = 100 estações no raio
- Cada inserção: ~log₂(100) ≈ 7 operações
- Total: 100 × 7 = 700 operações

---

### **2. Density Summary Calculation**

#### **Método:** `calculateDensitySummary()`

```java
public DensitySummary calculateDensitySummary(AVLTree<...> resultsTree) {
    Map<String, Integer> byCountry = new HashMap<>();
    int cityCount = 0;
    int nonCityCount = 0;
    int totalCount = 0;

    // Percorre TODAS as k estações UMA VEZ
    for (StationWithDistance result : resultsTree.inOrderTraversal()) {  // O(k)
        Station station = result.station;
        totalCount++;  // O(1)

        // Count by country
        String country = station.getCountry();
        if (country != null) {
            byCountry.put(country, byCountry.getOrDefault(country, 0) + 1);  // O(1)
        }

        // Count by isCity
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

#### **Complexidade:** `O(k)`

**Justificação:**
- **In-order traversal** da AVL tree: `O(k)` - visita cada nó uma vez
- **HashMap operations** (put, getOrDefault): `O(1)` amortizado
- **Contadores** (cityCount, totalCount): `O(1)`
- **Total:** Percorre k estações → **O(k)**

---

## 📈 Análise de Cenários

### **Cenário 1: Raio pequeno (10 km)**

```
N = 10,000 estações
k = 15 estações dentro do raio
```

| Operação | Cálculo | Resultado |
|----------|---------|-----------|
| Traversal 2D-tree | √10,000 | ~100 nós visitados |
| Inserções AVL | 15 × log₂(15) | ~60 operações |
| Density summary | 15 | 15 operações |
| **Total** | 100 + 60 + 15 | **~175 operações** |

**Eficiência:** Excelente! Apenas 1.75% dos nós visitados.

---

### **Cenário 2: Raio médio (50 km)**

```
N = 10,000 estações
k = 150 estações dentro do raio
```

| Operação | Cálculo | Resultado |
|----------|---------|-----------|
| Traversal 2D-tree | √10,000 | ~100 nós visitados |
| Inserções AVL | 150 × log₂(150) | ~1,125 operações |
| Density summary | 150 | 150 operações |
| **Total** | 100 + 1,125 + 150 | **~1,375 operações** |

**Eficiência:** Muito boa! Apenas 13.75% dos nós processados.

---

### **Cenário 3: Raio grande (500 km) - Pior Caso**

```
N = 10,000 estações
k = 5,000 estações dentro do raio (50% do dataset)
```

| Operação | Cálculo | Resultado |
|----------|---------|-----------|
| Traversal 2D-tree | ~N/2 | ~5,000 nós visitados |
| Inserções AVL | 5,000 × log₂(5,000) | ~61,400 operações |
| Density summary | 5,000 | 5,000 operações |
| **Total** | 5,000 + 61,400 + 5,000 | **~71,400 operações** |

**Eficiência:** Aceitável. Dominado por k log k (inserções AVL).

---

## 🔬 Comparação com Alternativas

### **Abordagem 1: Brute Force (sem 2D-tree)**

```java
// Percorre TODAS as estações
for (Station s : allStations) {  // O(N)
    double distance = haversine(...);  // O(1)
    if (distance <= radius) {
        results.add(s);
    }
}
Collections.sort(results);  // O(k log k)
```

**Complexidade:** `O(N + k log k)`

**Comparação:**
- **USEI10:** `O(√N + k log k)` ← Melhor!
- **Brute Force:** `O(N + k log k)`
- **Ganho:** √N vs N (ex: 100 vs 10,000 operações)

---

### **Abordagem 2: Lista ordenada (sem AVL tree)**

```java
// Usa 2D-tree mas guarda em List
List<StationWithDistance> results = new ArrayList<>();
radiusSearchRecursive(..., results);  // O(√N + k)
Collections.sort(results);  // O(k log k)
```

**Complexidade:** `O(√N + k log k)`

**Comparação:**
- **USEI10:** `O(√N + k log k)` ← Igual!
- **Lista ordenada:** `O(√N + k log k)`
- **Vantagem USEI10:** AVL mantém invariante de ordenação durante inserção

---

## 🎯 Otimizações Implementadas

### **1. Pruning Espacial**

```java
// Calcula distância perpendicular ao plano divisor
if ((targetValue - node.key) * 111.0 <= radiusKm) {
    radiusSearchRecursive(node.left, ...);  // ← Só visita se necessário
}
```

**Impacto:** Reduz O(N) → O(√N)

---

### **2. Inserção Direta na AVL**

```java
// Insere diretamente durante a busca (não cria lista intermediária)
results.insert(swd, swd);
```

**Impacto:** 
- Ordenação automática durante inserção
- Sem overhead de ordenação posterior
- Memória constante (não duplica dados)

---

### **3. HashMap para Contagem de Países**

```java
byCountry.put(country, byCountry.getOrDefault(country, 0) + 1);
```

**Impacto:** 
- Contagem em O(1) por país (amortizado)
- Em vez de O(k²) com busca linear

---

## 📊 Complexidade Espacial

| Estrutura | Espaço | Justificação |
|-----------|--------|--------------|
| **2D-tree** | O(N) | Armazena todas as N estações |
| **AVL results** | O(k) | Apenas estações no raio |
| **HashMap byCountry** | O(c) | c = número de países únicos (c << k) |
| **Contadores** | O(1) | cityCount, totalCount, etc. |
| **Total** | **O(N + k)** | Dominado pela 2D-tree |

**Nota:** A 2D-tree é construída **uma vez** e reutilizada para múltiplas queries.

---

## 🏆 Conclusão

### **Complexidade Final: O(√N + k log k)**

**Vantagens:**
1. ✅ **Sub-linear** em N (graças ao pruning: √N << N)
2. ✅ **Eficiente** para raios pequenos/médios (k << N)
3. ✅ **Escalável** para datasets grandes (10,000+ estações)
4. ✅ **Ordenação automática** via AVL tree
5. ✅ **Density summary em tempo linear** O(k)

**Casos de uso ideais:**
- Raios pequenos a médios (1-100 km)
- Queries frequentes (2D-tree amortiza custo de construção)
- Datasets com distribuição espacial não uniforme

**Performance esperada:**
- Dataset de 10,000 estações
- Raio típico de 50 km → ~150 resultados
- **~1,500 operações** (vs 10,000 em brute force)
- **Speedup de ~7x** 🚀

---

## 📚 Referências

1. **Bentley, J. L.** (1975). "Multidimensional binary search trees used for associative searching." *Communications of the ACM*, 18(9), 509-517.

2. **Cormen, T. H., et al.** (2009). *Introduction to Algorithms* (3rd ed.). MIT Press. Chapter 12 (BST) and Chapter 13 (AVL Trees).

3. **de Berg, M., et al.** (2008). *Computational Geometry: Algorithms and Applications* (3rd ed.). Springer. Chapter 5 (Range Searching).

4. **Haversine Formula**: Great-circle distance calculation for spherical coordinates.

---

**Documento gerado para USEI10 - G113 2DK**  
**ISEP - LAPR3 - 2024/2025**

