## ✅ Plano de implementação:

### 1️⃣ **Implementar no `Algorithms.java` (métodos base que faltam):**

```java
// ✅ IMPLEMENTAR:
✓ BreadthFirstSearch()           // já tem assinatura
✓ DepthFirstSearch() (privado)   // já tem assinatura
✓ DepthFirstSearch() (público)   // já tem assinatura
✓ allPaths()                      // já tem assinatura
✓ shortestPathDijkstra()          // já tem assinatura - USEI15 (pesos ≥ 0)
✓ shortestPath()                  // já tem assinatura - USEI15
✓ shortestPaths()                 // já tem assinatura - USEI13 (base para centrality)
✓ getPath()                       // já tem assinatura (auxiliar)
✓ minDistGraph() (Floyd-Warshall) // já tem assinatura - USEI13/USEI15
```

---

### 2️⃣ **Criar package `algorithms/` para o resto:**

```
graph/
├── algorithms/
│   ├── TopologicalSort.java       # USEI11 (usa DFS de Algorithms.java)
│   ├── CycleDetector.java         # USEI11
│   ├── MinimumSpanningTree.java   # USEI12 (Kruskal ou Prim)
│   ├── DOTExporter.java           # USEI12 (export para Graphviz)
│   ├── CentralityMetrics.java     # USEI13 (usa shortestPaths de Algorithms.java)
│   ├── MaxFlowSolver.java         # USEI14 (Edmonds-Karp)
│   └── BellmanFordSolver.java     # USEI15 (suporta pesos negativos)
```

---

## 📋 Checklist por User Story:

| US | Algoritmo | Onde implementar |
|----|-----------|------------------|
| **USEI11** | Topological Sort | `algorithms/TopologicalSort.java` (usa DFS de `Algorithms.java`) |
| **USEI11** | Cycle Detection | `algorithms/CycleDetector.java` |
| **USEI12** | MST (Kruskal/Prim) | `algorithms/MinimumSpanningTree.java` |
| **USEI12** | DOT Export | `algorithms/DOTExporter.java` |
| **USEI13** | Betweenness Centrality | `algorithms/CentralityMetrics.java` (usa `shortestPaths()`) |
| **USEI13** | Harmonic Closeness | `algorithms/CentralityMetrics.java` (usa `shortestPaths()`) |
| **USEI13** | Degree/Strength | Já tens no `MatrixGraph` (`outDegree()`/`inDegree()`) |
| **USEI14** | Max-Flow | `algorithms/MaxFlowSolver.java` (Edmonds-Karp) |
| **USEI15** | Bellman-Ford | `algorithms/BellmanFordSolver.java` (novo - suporta pesos negativos) |
| **USEI15** | Dijkstra | Já implementas no `Algorithms.java` (mas só funciona com pesos ≥ 0) |

---

## 🔧 Estrutura do package `algorithms/`:

```java
// Exemplo: TopologicalSort.java
package app.utils.graph.algorithms;

import app.utils.graph.*;
import java.util.*;

public class TopologicalSort<V, E> {
    
    /**
     * USEI11: Computes topological order using DFS from Algorithms.java
     * Returns null if graph has cycles.
     */
    public LinkedList<V> compute(Graph<V, E> g) {
        if (!g.isDirected()) {
            throw new IllegalArgumentException("Topological sort requires directed graph");
        }
        
        LinkedList<V> result = new LinkedList<>();
        Set<V> visited = new HashSet<>();
        Set<V> recStack = new HashSet<>();
        
        for (V v : g.vertices()) {
            if (!visited.contains(v)) {
                if (hasCycleDFS(g, v, visited, recStack, result)) {
                    return null; // ciclo detetado
                }
            }
        }
        
        return result;
    }
    
    private boolean hasCycleDFS(Graph<V, E> g, V v, Set<V> visited, 
                                Set<V> recStack, LinkedList<V> result) {
        visited.add(v);
        recStack.add(v);
        
        for (V adj : g.adjVertices(v)) {
            if (recStack.contains(adj)) {
                return true; // ciclo!
            }
            if (!visited.contains(adj)) {
                if (hasCycleDFS(g, adj, visited, recStack, result)) {
                    return true;
                }
            }
        }
        
        recStack.remove(v);
        result.addFirst(v); // adiciona no início (ordem reversa)
        return false;
    }
}
```

---

## ⚙️ Exemplo de uso:

```java
// USEI11
MatrixGraph<Station, Double> g = builder.buildGraph(...);
TopologicalSort<Station, Double> topo = new TopologicalSort<>();
LinkedList<Station> order = topo.compute(g);

if (order == null) {
    System.out.println("Graph has cycles!");
} else {
    System.out.println("Topological order: " + order);
}

// USEI13
CentralityMetrics<Station, Double> centrality = new CentralityMetrics<>();
Map<Station, Double> betweenness = centrality.betweenness(g);
```

---

## ✅ Resumo:

1. **`Algorithms.java`:** Implementa os 9 métodos que já têm assinatura (DFS, BFS, Dijkstra, Floyd-Warshall, etc.)
2. **`graph/algorithms/`:** Cria classes especializadas para o que **não** está no `Algorithms.java` (MST, Max-Flow, Bellman-Ford, Centrality, TopologicalSort, DOTExporter)
3. **Reutilização:** As classes em `algorithms/` **usam** os métodos base do `Algorithms.java` (ex: TopologicalSort usa DFS, CentralityMetrics usa shortestPaths)