# USEI12 — Minimal Backbone Network (MST)

## Objetivo

Como planeador de infraestruturas, preciso computar uma rede mínima sobre a rede ferroviária (tratada como não-direcionada) para identificar um backbone mínimo que conecte todas as estações com o menor comprimento total de trilhos.

## Requisitos Implementados

### ✅ Entrada
- Grafo não-direcionado com valores de distância
- Dados carregados de **stations.csv** e **lines.csv** (rede ferroviária belga - Infrabel)

### ✅ Computação MST
- MST para cada componente conectada
- Distância total minimizada
- Algoritmo: **Kruskal** (Union-Find)

### ✅ Grafo de Saída
- Grafo representando a Rede Minimal Backbone
- Apenas arestas MST + estações incluídas

### ✅ Exportação DOT
O ficheiro DOT gerado contém:
- Cada vértice posicionado nas suas coordenadas XY reais: `pos="x,y!"`
- Apenas arestas MST
- Compatível com **GraphViz neato**
- Aparência similar ao mapa global de comboios da Bélgica

### ✅ Exportação SVG
- Ficheiro SVG criado baseado no DOT usando aplicação **neato** do GraphViz
- Fácil visualização da rede minimal

### ✅ Análise de Complexidade Temporal
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

- ✅ **Input**: Grafo não-direcionado com valores de distância
- ✅ **MST Computation**: MST para cada componente conectada
- ✅ **Total Distance**: Distância total minimizada
- ✅ **Output Graph**: Arestas MST + estações incluídas
- ✅ **DOT Export**: Ficheiro DOT com coordenadas XY
- ✅ **GraphViz Compatibility**: Layout neato especificado
- ✅ **SVG Export**: Geração SVG validada (se GraphViz disponível)
- ✅ **Time Complexity**: O(E log V) documentado
- ✅ **Validation**: Trata cenários conectados/desconectados/isolados

## Complexidade Temporal

### Algoritmo de Kruskal (Implementado)

**O(E log V)** onde:
- **E** = número de arestas (conexões ferroviárias)
- **V** = número de vértices (estações)

#### Detalhamento:
1. **Ordenação de arestas**: O(E log E) = O(E log V) [pois E ≤ V²]
2. **Union-Find operations**: O(E × α(V)) ≈ O(E) [α é inverso de Ackermann, praticamente constante]
3. **Total**: O(E log V)

### Espaço
- **O(V + E)** para armazenar o grafo

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

## Próximos Passos

1. ✅ Implementação MST básica
2. ✅ Integração com dados reais (Infrabel CSV)
3. ✅ Exportação DOT com coordenadas
4. ✅ Geração SVG com GraphViz
5. 🔲 Otimização para redes muito grandes (>10,000 estações)
6. 🔲 Interface gráfica para visualização interativa
7. 🔲 Análise de custo-benefício para expansão de rede

