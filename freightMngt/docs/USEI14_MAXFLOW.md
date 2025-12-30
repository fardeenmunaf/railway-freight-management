# USEI14 — Maximum throughput between two Hubs

Objetivo

- Calcular o fluxo máximo (max flow) entre duas estações (source, sink) usando capacidades por aresta (trens por dia) para estimar a vazão teórica de um corredor.
- Retorno esperado: source stid, target stid, maxFlowValue e análise da complexidade temporal.

Visão geral da implementação

- Algoritmo central: Edmonds–Karp (variante de Ford–Fulkerson que usa BFS para encontrar caminhos aumentantes).
- Código principal: `src/main/java/app/utils/graph/MaxFlow.java`.
  - A função pública de cálculo é `MaxFlow.edmondsKarp(...)` e o wrapper `MaxFlow.compute(...)` que devolve um `MaxFlow.Result` com metadados (source, sink, sourceKey, sinkKey, maxFlow, message, complexity).
  - A implementação usa uma matriz residual indexada por key do vértice: `E[][] residual = (E[][]) new Object[n][n];` onde `n = numVertices()`.
  - Regras/validações: as capacidades (pesos das arestas) não podem ser negativas; se houver arestas com capacidade negativa é lançada uma exceção.
  - Suporta vários tipos numéricos (Integer, Double, Long, Float, BigInteger, BigDecimal) — o wrapper `MaxFlowService` deduz o tipo a partir das arestas e fornece as operações de soma/subtração necessárias.

Como o grafo é obtido e onde as capacidades vêm

- Serviço que prepara e invoca o MaxFlow: `src/main/java/app/service/sprint3/MaxFlowService.java`.
  - Estratégia de obtenção do grafo (em ordem de tentativa):
    1. `tryInstantiateProjectGraph()` — tentativa reflexiva de obter uma instância do grafo do projecto (procura `MapGraph`/`MatrixGraph`, construtores, ou `getInstance()` estático). Se existir um grafo global do projecto, esse grafo será usado diretamente.
    2. `buildStationGraphFromRepositories()` — tenta construir um `MapGraph<Station,Integer>` usando as `Station` já carregadas em `Repositories` (carregadas por `AppInitializer`) e, se presente, ligações no CSV `sampleDataset/lines.csv` usando a coluna `capacity`.
    3. `buildStationCapacityGraphFromSample()` — fallback: lê `sampleDataset/stations.csv` e `sampleDataset/lines.csv` e constrói um `MapGraph<Station,Integer>` usando explicitamente a coluna `capacity` das linhas.
  - Portanto, as capacidades usadas pelo MaxFlow vêm da coluna `capacity` do ficheiro `sampleDataset/lines.csv` (quando o grafo é construído a partir dos CSVs) ou das arestas já presentes no grafo do projecto.

- Integração com `AppInitializer`:
  - `AppInitializer.loadStations(...)` carrega estações a partir de `train_stations_europe.csv` e adiciona-as ao `Repositories` (via `FacilityRepository`).
  - `MaxFlowService.buildStationGraphFromRepositories()` tenta reutilizar essas estações em `Repositories` (correspondência por `facilityId`) ao ligar as arestas com `departure_stid`/`arrival_stid` do `lines.csv`.
  - Observação prática: essa reutilização funciona apenas se os `facilityId` nas `Repositories` coincidirem com os ids em `lines.csv`. Caso contrário, o serviço recorre ao builder dos CSVs (fallback) para garantir um grafo com capacidades.

Como invocar (APIs disponíveis)

- Usar o grafo existente (quando já tens a referência ao grafo):
  - `MaxFlowService.computeUsingGraph(existingGraph, sourceKey, sinkKey)` — usa o grafo que passares diretamente.
- Invocação com tentativa automática de obter grafo do projecto / repos / CSVs:
  - `MaxFlowService.computeUsingProjectGraphIfAvailable(sourceKey, sinkKey)` — função de conveniência que segue a ordem de tentativa descrita acima e devolve `MaxFlow.Result<?,?>`.
- Visualização/Output: `app/view/MaxFlowView.java` formata e imprime `MaxFlow.Result` em modo DETAILED ou COMPACT.

Formato do resultado (`MaxFlow.Result`)

- Campos principais:
  - `source` (V) — objecto vertex (quando disponível)
  - `sink` (V) — objecto vertex
  - `sourceKey` (Integer) — chave numérica (stid) do source quando resolvida
  - `sinkKey` (Integer) — chave numérica do sink quando resolvida
  - `maxFlow` (E) — valor do fluxo máximo (tipo numérico)
  - `message` (String) — texto com info/erros
  - `complexity` (String) — análise temporal (por exemplo: "O(V·E^2)")

Análise completa de complexidade (tempo e memória) — explicação e razões

1) Edmonds–Karp (implementação usada)
- Complexidade temporal: O(V · E^2)
  - Explicação: cada iteração encontra um caminho aumentante usando BFS (custo O(E) por BFS em grafos representados por adjacências). O número total de iterações (aumentos) é O(V·E) no pior caso para Edmonds–Karp. Multiplicando dá O(V·E·E) = O(V·E^2).
  - Intuição: cada aumento reduz pelo menos uma aresta saturada; o número de aumentos pode ser tão alto quanto O(V·E) em grafos construídos adversarialmente. Como cada BFS custa O(E), chegamos ao bound O(V·E^2).
- Complexidade de memória: O(V^2) adicional (matriz residual)
  - Explicação: a implementação actual aloca uma matriz residual `E[][] residual = new Object[n][n]` (onde n = numVertices). Isto usa O(n^2) memória para representar capacidades residuais entre quaisquer pares de vértices. Em grafos esparsos este custo pode ser caro; uma alternativa eficiente em memória seria usar um grafo residual representado por listas de adjacência (O(V + E)).
- Operações internas custosas:
  - BFS por iteração: O(E)
  - Atualização de residual ao longo do caminho: O(length do caminho) ≤ O(V)
  - Escolha do bottleneck (min) ao longo do caminho: O(V) (percurso do caminho por pares u→v)

2) Overhead de preparação e detecção de tipo no wrapper (`MaxFlowService.compute`)
- Detecção de tipo de peso: O(E) — o serviço varre as arestas até encontrar um peso não-nulo para deduzir o tipo numérico (Integer/Double/...). Custo linear no número de arestas até encontrar um peso.
- Preparação das funções add/sub: O(1) após detecção de tipo.

3) Construção do grafo a partir de CSV (`buildStationCapacityGraphFromSample` / `buildStationGraphFromRepositories`)
- Leitura de CSVs: linear no número de linhas dos ficheiros.
  - `buildStationCapacityGraphFromSample()` faz:
    - leitura de todas as estações do CSV: O(S) onde S = número de estações
    - leitura de todas as linhas/arestas do CSV e adição (quando capacity existe): O(L) onde L = número de linhas
    - custo total: O(S + L)
  - `buildStationGraphFromRepositories()` varre todas as facilities em `Repositories` e constrói vértices: O(F) onde F = número de facilities; em seguida varre `lines.csv` para ligar arestas (O(L)). Total: O(F + L).
- Memória da estrutura final: depende da implementação do grafo (MapGraph usa estruturas por vértice; memória ≈ O(V + E) para representação por listas/estruturas). Nota: embora `MapGraph` mantenha objetos Edge, `MaxFlow` cria ainda uma matriz residual O(V^2).

4) Complexidade combinada (fluxo completo incluindo preparação)
- Caso típico quando o serviço constrói o grafo a partir dos CSVs: custo de preparação O(S + L) + custo do algoritmo O(V·E^2) (sendo V ≈ número de estações usadas e E ≈ número de arestas). A etapa dominante geralmente é o passo do algoritmo de max-flow (O(V·E^2)), especialmente em grafos médios a grandes.

Considerações práticas e motivação das decisões

- Por que Edmonds–Karp?
  - Simplicidade e robustez: Edmonds–Karp (BFS) é fácil de implementar e garantir comportamento determinístico. Para grafos de dimensão moderada (como um subconjunto do grafo belga de teste), é suficientemente eficiente e fácil de compreender/depurar.
  - Compatibilidade de tipos: a implementação suporta vários tipos numéricos e valida as capacidades (não-negatividade).
- Por que matriz residual (memória O(V^2))?
  - Simplicidade de indexação: a matriz residual permite indexar diretamente por chave de vértice (g.key(v)). Facilita operações em código genérico sem manipular nós/arestas residuais explícitos.
  - Trade-off: maior consumo de memória em grafos com muitos vértices. Para escalabilidade, uma versão alternativa e recomendada é usar uma representação residual por listas (adjacência), que reduz memória a O(V + E) e mantém cada operação de atualização eficiente.
- Porque a preparação do grafo tenta usar a instância do projecto por reflexão?
  - Deseja-se preferir um grafo que já existe na aplicação (por exemplo montado por outros módulos) em vez de sempre reconstruir a partir dos CSVs. O método `tryInstantiateProjectGraph()` tenta instanciar/refletir `MapGraph`/`MatrixGraph` do projecto e, se houver um singleton `getInstance()`, obtém o grafo global.

Casos de borda e validação

- Capacidades nulas: se o grafo não tiver arestas com capacidade (pesos nulos), `MaxFlowService` devolve um `Result` com mensagem a indicar "Graph edges do not have capacities (weights are null). Provide numeric capacities.".
- Capacidades negativas: `MaxFlow` valida e rejeita (lança IllegalArgumentException) — capacidades devem ser não-negativas.
- Source == sink: `MaxFlow.compute(...)` trata esse caso devolvendo fluxo 0 e mensagem apropriada.
- Vértices inválidos: se source/sink não existirem no grafo devolve um `Result` indicando "Invalid source or sink vertex.".

Recomendações para produção / melhorias futuras

- Representação residual por adjacência: reimplementar a residual graph como listas adjacentes (Edge residual objects) para reduzir memória de O(V^2) para O(V + E) — isto também permite aplicar algoritmos mais eficientes (Dinic/O(E·sqrt(V)), Push–relabel, etc.).
- Implementar Dinic ou Push–relabel para melhores garantias de desempenho em grafos maiores:
  - Dinic: O(E·V^1/2) em muitos casos práticos, e geralmente muito mais rápido que Edmonds–Karp para grafos grandes.
  - Push–relabel (Goldberg–Tarjan): O(V^2·sqrt(E)) ou variantes com bom desempenho prático.
- Testes automatizados: adicionar testes JUnit com grafos de referência pequenos (ex.: grafo 4 nós com fluxos conhecidos) e com subgrafo extraído de `sampleDataset/lines.csv` para validar resultados e regressões.

Resumo final / como usar hoje

- Fluxo máximo (procedimento):
  1. Obter um grafo com capacidades (preferir grafo do projecto ou usar `MaxFlowService.computeUsingProjectGraphIfAvailable(sourceId, sinkId)`).
  2. Invocar `MaxFlowService.compute((Graph)g, sourceKey, sinkKey)` ou `computeUsingProjectGraphIfAvailable(sourceKey, sinkKey)`.
  3. Ler `MaxFlow.Result`: `sourceKey`, `sinkKey`, `maxFlow` e `complexity`.

- Local de código relevante:
  - Algoritmo: `src/main/java/app/utils/graph/MaxFlow.java`
  - Serviço: `src/main/java/app/service/sprint3/MaxFlowService.java`
  - View: `src/main/java/app/view/MaxFlowView.java`
  - Datasets: `src/main/java/app/sampleDataset/stations.csv`, `src/main/java/app/sampleDataset/lines.csv`

---
