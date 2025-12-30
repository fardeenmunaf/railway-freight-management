# USEI13 - Rail Hub Centrality Analysis - Temporal Complexity Analysis

**Author:** Diogo Remelgado  
**Date:** 2025-12-30  

---

## Overview

The Rail Hub Centrality Analysis (USEI13) computes multiple centrality metrics to identify critical railway stations in a transportation network. The system calculates:

1. **Degree** - Number of direct connections
2. **Strength** - Sum of edge weights (cost)
3. **Betweenness Centrality** - Intermediation in shortest paths
4. **Harmonic Closeness** - Average accessibility
5. **HubScore** - Normalized composite metric

---

## Graph Characteristics

Let:
- **V** = Number of vertices (stations) in the graph
- **E** = Number of edges (railway connections) in the graph

### Graph Density:
- **Sparse graph**: E ≈ V (minimum connectivity)
- **Dense graph**: E ≈ V² (high connectivity)

---

## Detailed Complexity Analysis by Operation

### 1. Degree and Strength Calculation

**Method:** `computeDegreeAndStrength()`

**Algorithm:**
```
For each station v in V:
    For each edge e connected to v:
        degree++
        strength += edge.getCost()
```

**Complexity:**
- **Best Case:** O(V + E)
  - Occurs when: Each station is visited once (V iterations), and each edge is examined once (E iterations)
  - Scenario: Always occurs regardless of graph structure
  
- **Worst Case:** O(V + E)
  - Occurs when: Same as best case
  - Scenario: Linear complexity is optimal for this operation

**Explanation:** This is a single pass through all vertices and edges. The complexity is always O(V + E) regardless of graph density.

---

### 2. Betweenness Centrality (Brandes Algorithm)

**Method:** `computeBetweenness()`

**Algorithm:** Brandes' algorithm for weighted graphs using Dijkstra's shortest paths

**Complexity:**
- **Best Case:** O(V·E + V²·log V)
  - Occurs when: Sparse graph (E ≈ V)
  - Calculation: O(V·V + V²·log V) = O(V² + V²·log V) = O(V²·log V)
  - Scenario: Railway network with minimal connections (linear or tree structure)
  
- **Worst Case:** O(V·E + V²·log V)
  - Occurs when: Dense graph (E ≈ V²)
  - Calculation: O(V·V² + V²·log V) = O(V³ + V²·log V) = O(V³)
  - Scenario: Fully connected railway network with many redundant paths

**Explanation:** Brandes' algorithm executes Dijkstra from each vertex:
- **V iterations** (one per source vertex)
- Each Dijkstra execution: **O(E + V·log V)** using priority queue
- Total: **O(V·(E + V·log V))** = **O(V·E + V²·log V)**

---

### 3. Harmonic Closeness Centrality

**Method:** `computeHarmonicCloseness()`

**Algorithm:** All-Pairs Shortest Paths (APSP) using Dijkstra from each vertex

**Complexity:**
- **Best Case:** O(V²·log V + V·E)
  - Occurs when: Sparse graph (E ≈ V)
  - Calculation: O(V·(V·log V + E)) = O(V²·log V + V²) = O(V²·log V)
  - Scenario: Tree-like railway network with minimal branching
  
- **Worst Case:** O(V³)
  - Occurs when: Dense graph (E ≈ V²)
  - Calculation: O(V·(V·log V + V²)) = O(V²·log V + V³) = O(V³)
  - Scenario: Highly interconnected railway hub system

**Explanation:** For each station as source:
- Run Dijkstra to find shortest paths to all other vertices: **O(E + V·log V)**
- Compute harmonic sum: **O(V)**
- Total for all sources: **O(V·(E + V·log V + V))** = **O(V·E + V²·log V)**

---

### 4. Metric Normalization

**Method:** `normalizeMetrics()`

**Algorithm:** Min-max normalization for betweenness, harmonic closeness, and strength

**Complexity:**
- **Best Case:** O(V)
  - Occurs when: Always (single pass through vertices)
  - Scenario: Any graph size
  
- **Worst Case:** O(V)
  - Occurs when: Always (single pass through vertices)
  - Scenario: Any graph size

**Explanation:** For each metric:
- Find min and max: O(V)
- Normalize each value: O(V)
- Total for 3 metrics: **O(3·V)** = **O(V)**

---

### 5. HubScore Computation

**Method:** `computeHubScore()`

**Algorithm:** Weighted sum of normalized metrics

**Complexity:**
- **Best Case:** O(V)
  - Occurs when: Always (single pass through vertices)
  - Scenario: Any graph size
  
- **Worst Case:** O(V)
  - Occurs when: Always (single pass through vertices)
  - Scenario: Any graph size

**Explanation:** For each station:
- Retrieve 3 normalized values: O(1)
- Calculate weighted sum: O(1)
- Total: **O(V)**

---

## Overall System Complexity

### Total Complexity: `calculateAllMetrics()`

**Operations performed:**
1. Degree & Strength: O(V + E)
2. Betweenness: O(V·E + V²·log V)
3. Harmonic Closeness: O(V·E + V²·log V)
4. Normalization: O(V)
5. HubScore: O(V)

**Total:** O(V + E + V·E + V²·log V + V·E + V²·log V + V + V)

**Simplified:** O(V·E + V²·log V)

---

### Best Case Scenario

**Graph Type:** Sparse graph (E ≈ V)

**Complexity:** O(V² + V²·log V) = **O(V²·log V)**

**Real-world Example:**
- Linear railway line: 1000 stations, 999 connections
- Calculation: O(1000² · log 1000) ≈ O(10,000,000 operations)

**When it occurs:**
- Railway networks with minimal branching
- Linear or tree-structured routes
- Rural areas with single main lines

---

### Worst Case Scenario

**Graph Type:** Dense graph (E ≈ V²)

**Complexity:** O(V³ + V²·log V) = **O(V³)**

**Real-world Example:**
- Fully connected hub system: 1000 stations, ~500,000 connections
- Calculation: O(1000³) ≈ O(1,000,000,000 operations)

**When it occurs:**
- Metropolitan railway networks with extensive interconnections
- Hub-and-spoke systems with many cross-connections
- Networks with high redundancy for reliability

---

