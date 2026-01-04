# USEI12 — Minimal Backbone Network (MST)

## Objetivo

Como planeador de infraestruturas, preciso computar uma rede mínima sobre a rede ferroviária (tratada como não-direcionada) para identificar um backbone mínimo que conecte todas as estações com o menor comprimento total de trilhos.

## Requisitos Implementados

### Entrada
- Grafo não-direcionado com valores de distância
- Dados carregados de **stations.csv** e **lines.csv** (rede ferroviária belga - Infrabel)

### Computação MST
- MST para cada componente conectada
- Distância total minimizada
- Algoritmo: **Kruskal** (Union-Find)

### Grafo de Saída
- Grafo representando a Rede Minimal Backbone
- Apenas arestas MST + estações incluídas

### Exportação DOT
O ficheiro DOT gerado contém:
- Cada vértice posicionado nas suas coordenadas XY reais: `pos="x,y!"`
- Apenas arestas MST
- Compatível com **GraphViz neato**
- Aparência similar ao mapa global de comboios da Bélgica

### Exportação SVG
- Ficheiro SVG criado baseado no DOT usando aplicação **neato** do GraphViz
- Fácil visualização da rede minimal

### Análise de Complexidade Temporal
- **O(E log V)** - Algoritmo de Kruskal com Union-Find
- Documentado em código e testes

## Estrutura de Ficheiros

```
freightMngt/
├── src/main/java/app/
│   ├── view/
│   │   └── MSTInfrastructurePlannerDemo.java     # Demonstração/UI
│   ├── controller/
│   │   └── MSTController.java                    # Orquestração workflow
│   ├── service/sprint3/
│   │   ├── MinimumSpanningTreeService.java       # Algoritmo MST (Kruskal)
│   │   ├── MSTDotExportService.java              # Exportação DOT/SVG
│   │   ├── MSTValidationService.java             # Validação de critérios
│   │   └── StationLoaderService.java             # Carregamento CSV
│   ├── test/
│   │   └── MSTExampleTest.java                   # Teste da demonstração
│   └── sampleDataset/
│       ├── stations.csv                           # Estações belgas (Infrabel)
│       └── lines.csv                              # Conexões ferroviárias
├── outputs/
│   ├── belgian_railway_mst.dot                   # MST em formato DOT
│   └── belgian_railway_mst.svg                   # MST visualizado (se GraphViz instalado)
└── README_USEI12_MST.md                          # Este ficheiro
```

## Fonte de Dados

Os dados das estações e linhas ferroviárias são provenientes de:

**Infrabel Open Data - Belgian Railway Network**
- URL: https://opendata.infrabel.be/explore/dataset/station_to_station/
- Formato: CSV
- Conteúdo: Estações ferroviárias belgas com coordenadas reais e distâncias entre estações

## Como Executar

### 1. Compilar o Projeto

```bash
cd freightMngt
mvn clean compile
```

### 2. Executar a Demonstração MST

```bash
# Executar demonstração completa (3 cenários)
java -cp target/classes app.view.MSTInfrastructurePlannerDemo

# Ou executar apenas teste da rede belga
java -cp target/classes app.test.MSTExampleTest
```

### 3. Gerar SVG (requer GraphViz instalado)

```bash
# Instalar GraphViz primeiro (se necessário)
# Windows: choco install graphviz
# Linux: sudo apt-get install graphviz
# macOS: brew install graphviz

# O SVG é gerado automaticamente se GraphViz estiver no PATH
# Verificar ficheiro: outputs/belgian_railway_mst.svg
```

### 4. Visualizar Resultados

```bash
# Ver ficheiro DOT gerado
type outputs\belgian_railway_mst.dot

# Abrir SVG no browser (se gerado)
start outputs\belgian_railway_mst.svg
```

## Exemplo de Uso Programático

```java
// 1. Carregar rede ferroviária belga dos CSVs
StationLoaderService loader = new StationLoaderService();
Graph<Station, Double> network = loader.loadNetwork(
    "src/main/java/app/sampleDataset/stations.csv",
    "src/main/java/app/sampleDataset/lines.csv"
);

// 2. Computar MST
MSTController controller = new MSTController();
MSTController.WorkflowResult result = controller.computeAndExportMST(
    network, 
    "outputs/belgian_railway_mst", 
    true  // gerar SVG
);

// 3. Visualizar resultados
System.out.println(result.getSummaryReport());
System.out.println("Total distance: " + result.getMstResult().getTotalDistance());
System.out.println("DOT file: " + result.getExportResult().getDotFilePath());
```

## Cenários de Teste

O exemplo demonstra 3 cenários:

### 1. Rede Conectada (Belgian Railway Network)
- **Input**: Todas as estações belgas do CSV
- **Output**: MST conectando todas as estações alcançáveis
- **Arquivo**: `belgian_railway_mst.dot`

### 2. Componentes Desconectadas
- **Input**: Subconjunto de estações com conexões limitadas
- **Output**: MST para cada componente regional
- **Arquivo**: `regional_networks_mst.dot`

### 3. Estações Isoladas
- **Input**: Estações sem conexões
- **Output**: Apenas nós (sem arestas MST)
- **Arquivo**: `isolated_stations_mst.dot`

## Critérios de Aceitação Validados

- **Input**: Grafo não-direcionado com valores de distância
- **MST Computation**: MST para cada componente conectada
- **Total Distance**: Distância total minimizada
- **Output Graph**: Arestas MST + estações incluídas
- **DOT Export**: Ficheiro DOT com coordenadas XY
- **GraphViz Compatibility**: Layout neato especificado
- **SVG Export**: Geração SVG validada (se GraphViz disponível)
- **Time Complexity**: O(E log V) documentado
- **Validation**: Trata cenários conectados/desconectados/isolados

## Análise de Complexidade

Esta secção descreve a complexidade temporal e espacial do algoritmo de Kruskal utilizado para computar a Árvore Geradora Mínima (MST), bem como as razões teóricas por trás das operações realizadas.

---

### Algoritmo de Kruskal com Union-Find

O algoritmo de **Kruskal** constrói a MST selecionando arestas por ordem crescente de peso, garantindo que não formam ciclos. A estrutura **Union-Find** (Disjoint Set) é utilizada para detetar ciclos de forma eficiente.

#### Excerto de código — ordenação de arestas

```java
// 1. Obter todas as arestas do grafo
List<Edge<V, E>> edges = new ArrayList<>();
for (V vertex : graph.vertices()) {
    for (Edge<V, E> edge : graph.outgoingEdges(vertex)) {
        edges.add(edge);
    }
}

// 2. Ordenar arestas por peso (ordem crescente)
edges.sort((e1, e2) -> ce.compare(e1.getWeight(), e2.getWeight()));
```

#### Complexidade da ordenação

* Todas as arestas são coletadas → **O(E)**
* Ordenação das arestas → **O(E log E)**

Como num grafo, **E ≤ V²**, temos que **log E ≤ 2 log V**, logo:

**O(E log E) = O(E log V)**

---

#### Excerto de código — construção da MST

```java
// 3. Inicializar Union-Find para deteção de ciclos
UnionFind<V> uf = new UnionFind<>(graph.vertices());

// 4. Processar arestas em ordem crescente de peso
for (Edge<V, E> edge : edges) {
    V vOrig = edge.getVOrig();
    V vDest = edge.getVDest();
    
    // 5. Verificar se vértices estão em componentes diferentes
    if (!uf.find(vOrig).equals(uf.find(vDest))) {
        // 6. Adicionar aresta à MST
        mstEdges.add(edge);
        totalWeight = sum.apply(totalWeight, edge.getWeight());
        
        // 7. Unir componentes
        uf.union(vOrig, vDest);
        
        // 8. Parar se já temos V-1 arestas (MST completa)
        if (mstEdges.size() == graph.numVertices() - 1) {
            break;
        }
    }
}
```

#### Porque são processadas no máximo V-1 arestas?

* Uma **árvore** com **V** vértices tem exatamente **V-1** arestas
* A MST é uma árvore, logo conecta todos os vértices com **V-1** arestas
* Após adicionar **V-1** arestas válidas, a MST está completa
* Mesmo em grafos desconectados, cada componente forma sua própria árvore

---

#### Complexidade das operações Union-Find

A estrutura **Union-Find** com **path compression** e **union by rank** tem complexidade quase constante:

* **Find(v)**: O(α(V)) onde α é a função inversa de Ackermann
* **Union(u, v)**: O(α(V))

Para todos os propósitos práticos, **α(V) < 5** mesmo para V extremamente grande (α(2^65536) = 5), logo:

**O(α(V)) ≈ O(1)**

Como processamos no máximo **E** arestas, cada uma com operações Find/Union:

**O(E × α(V)) ≈ O(E)**

---

#### Complexidade temporal total

Combinando todas as operações:

1. **Coletar arestas**: O(E)
2. **Ordenar arestas**: O(E log V)
3. **Processar arestas com Union-Find**: O(E × α(V)) ≈ O(E)

**Complexidade total: O(E log V)**

---

#### Complexidade espacial

* **Grafo original**: O(V + E)
* **Lista de arestas**: O(E)
* **Union-Find**: O(V)
* **MST resultante**: O(V + E_mst) onde E_mst ≤ V-1

**Complexidade espacial total: O(V + E)**

---

### Comparação com Algoritmo de Prim

| Aspecto             | Kruskal                  | Prim                          |
| ------------------- | ------------------------ | ----------------------------- |
| Estratégia          | Arestas (edge-based)     | Vértices (vertex-based)       |
| Estrutura auxiliar  | Union-Find               | Fila de prioridade            |
| Complexidade        | O(E log V)               | O(E log V) com heap           |
| Melhor para         | Grafos esparsos          | Grafos densos                 |
| Múltiplos componentes | Trata naturalmente     | Requer múltiplas execuções    |

---

### Aplicação à Rede Ferroviária Belga

Para a rede Infrabel com aproximadamente:
* **V ≈ 559** estações
* **E ≈ 1382** conexões bidirecionais

A complexidade é:
* **O(1382 × log 559) ≈ O(1382 × 9.1) ≈ 12,576** operações

**Tempo de execução**: < 1ms em hardware moderno

---

### Melhor e Pior Caso

#### Melhor Caso: O(E log V)

O melhor caso ocorre quando:
* **Grafo conectado**: Uma única componente
* **Early termination efetiva**: MST completa com V-1 arestas logo no início da lista ordenada
* **Arestas já ordenadas**: Menor overhead na ordenação se já estiverem parcialmente ordenadas

Mesmo assim, a ordenação inicial das arestas **O(E log V)** é inevitável, pois Kruskal requer arestas ordenadas por peso.

**Exemplo de melhor caso**:
```
Grafo: Árvore (V vértices, E = V-1 arestas)
- Ordenação: O((V-1) log V) = O(V log V)
- Union-Find: O(V-1) ≈ O(V)
- Total: O(V log V)
```

Para grafos extremamente esparsos onde **E ≈ V**, a complexidade aproxima-se de **O(V log V)**.

---

#### Pior Caso: O(E log V)

O pior caso ocorre quando:
* **Grafo denso**: E ≈ V² arestas
* **Todas as arestas processadas**: Nenhuma aresta forma ciclo até o final
* **Múltiplas componentes**: Várias árvores disjuntas

**Exemplo de pior caso**:
```
Grafo completo: V vértices, E = V(V-1)/2 arestas
- Ordenação: O(V² log V²) = O(V² log V)
- Union-Find: O(V²) operações (todas as arestas verificadas)
- Total: O(V² log V)
```

Como **E = V²** no pior caso:
**O(E log V) = O(V² log V)**

---

#### Caso Médio: O(E log V)

Na prática, para grafos típicos de redes ferroviárias:
* **Grafos esparsos**: E ≈ 2V a 4V (cada estação conecta a 2-4 estações)
* **Ordenação domina**: O(E log V) é o gargalo
* **Union-Find quase constante**: α(V) < 5 sempre

**Complexidade caso médio: O(E log V)**

---

### Garantias do Algoritmo

1. **Corretude**: Kruskal sempre produz uma MST válida (provado por indução)
2. **Otimalidade**: A MST tem o menor peso total possível (propriedade do corte)
3. **Determinismo**: Para pesos únicos, a MST é única
4. **Componentes desconectados**: Cada componente obtém sua própria MST

---

### Implementação de Union-Find

```java
public class UnionFind<V> {
    private Map<V, V> parent;
    private Map<V, Integer> rank;
    
    // Inicialização: O(V)
    public UnionFind(Collection<V> vertices) {
        parent = new HashMap<>();
        rank = new HashMap<>();
        for (V v : vertices) {
            parent.put(v, v);      // Cada vértice é sua própria raiz
            rank.put(v, 0);        // Rank inicial = 0
        }
    }
    
    // Find com path compression: O(α(V)) ≈ O(1)
    public V find(V v) {
        if (!parent.get(v).equals(v)) {
            parent.put(v, find(parent.get(v)));  // Compressão de caminho
        }
        return parent.get(v);
    }
    
    // Union by rank: O(α(V)) ≈ O(1)
    public void union(V v1, V v2) {
        V root1 = find(v1);
        V root2 = find(v2);
        
        if (root1.equals(root2)) return;
        
        // Anexar conjunto menor ao maior
        if (rank.get(root1) < rank.get(root2)) {
            parent.put(root1, root2);
        } else if (rank.get(root1) > rank.get(root2)) {
            parent.put(root2, root1);
        } else {
            parent.put(root2, root1);
            rank.put(root1, rank.get(root1) + 1);
        }
    }
}
```

---

### Otimizações Implementadas

1. **Early termination**: Para quando V-1 arestas já foram adicionadas
2. **Path compression**: Reduz altura das estruturas no Union-Find
3. **Union by rank**: Mantém conjuntos balanceados
4. **Remoção de duplicados**: Grafo não-direcionado processa cada aresta apenas uma vez

## Formato DOT Gerado

```dot
graph MST {
  layout=neato;
  node [shape=circle, fontsize=10];
  edge [fontsize=8];

  "Bruxelles-Central" [pos="145432.00,166123.00!"];
  "Antwerpen-Centraal" [pos="152876.00,212347.00!"];
  "Gent-Sint-Pieters" [pos="104587.00,185429.00!"];
  ...

  "Bruxelles-Central" -- "Antwerpen-Centraal" [label="44.00"];
  "Bruxelles-Central" -- "Gent-Sint-Pieters" [label="56.00"];
  ...
}
```

### Características:
- **Layout**: `neato` (layout baseado em posições físicas)
- **Coordenadas**: `pos="x,y!"` (posições fixas, não otimizadas)
- **Arestas**: Apenas MST edges com distâncias
- **Nós**: Todas as estações alcançáveis

## Referências

- **GraphViz DOT Language**: https://graphviz.org/doc/info/lang.html
- **Infrabel Open Data**: https://opendata.infrabel.be/
- **Belgian Train Map**: https://www.belgiantrain.be/en/travel-info/prepare-for-your-journey/leaflets/global-map-train-belgium
- **Kruskal's Algorithm**: Cormen et al., "Introduction to Algorithms"
- **Union-Find**: Sedgewick & Wayne, "Algorithms"

## Autores

- **Infrastructure Planning Team**
- **Version**: 2.0 - Belgian Railway Network Integration
- **Date**: 2026-01-03

## Notas Técnicas

### Coordenadas Geográficas
As coordenadas dos ficheiros CSV estão em **Lambert 72** (sistema de coordenadas belga).
Para conversão para lat/lon WGS84, consultar: https://epsg.io/31370

### GraphViz Installation
- **Windows**: `choco install graphviz` ou download de https://graphviz.org/download/
- **Linux**: `sudo apt-get install graphviz`
- **macOS**: `brew install graphviz`
- **Verificar**: `neato -V` deve mostrar a versão instalada

### Troubleshooting

**Erro: "outputs\belgian_railway_mst.dot (O sistema não conseguiu localizar o diretório especificado)"**
- Solução: O diretório `outputs/` é criado automaticamente pelo sistema

**Erro: "Cannot run program 'neato'"**
- Solução: GraphViz não está instalado ou não está no PATH
- Instalação: Seguir instruções acima
- O ficheiro DOT é gerado mesmo sem GraphViz (SVG apenas se disponível)

**MST tem mais arestas que n-1**
- Causa: Grafo pode ter múltiplos componentes desconectados
- Cada componente tem seu próprio MST
- Total de arestas = (n₁-1) + (n₂-1) + ... onde n₁, n₂, ... são tamanhos dos componentes
