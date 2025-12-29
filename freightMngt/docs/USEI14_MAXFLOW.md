USEI14 — Documentação técnica: Cálculo de fluxo máximo (MaxFlow)

Visão geral
-----------
Este documento descreve a implementação atual de fluxo máximo presente em `app.utils.graph.MaxFlow` e como ela satisfaz a User Story USEI14: "Maximum throughput between two Hubs". Contém: propósito, contrato de entrada/saída, exemplos de uso, tratamento de erros, complexidade temporal, casos de teste sugeridos e melhorias recomendadas.

Mapeamento direto para a User Story (USEI14)
-------------------------------------------
- Objetivo da US: calcular o fluxo máximo entre duas estações (source e sink) usando capacidades por aresta, para estimar a vazão teórica do corredor.
- Implementação: `MaxFlow.compute(...)` provê exatamente isso e retorna um `Result` com campos que permitem identificar source/sink e o valor do fluxo máximo (`maxFlow`). A complexidade documentada também é fornecida.

Onde procurar a implementação
-----------------------------
- Classe: `app.utils.graph.MaxFlow`
- Método principal (resumo):
  - `public static <V,E> Result<V,E> compute(Graph<V,E> g, V source, V sink, Comparator<E> cmp, BinaryOperator<E> add, BinaryOperator<E> sub, E zero)`
  - Sobreloads com `zero`/sem `sub` e com `int sourceKey, int sinkKey`.
- Resultado: `MaxFlow.Result<V,E>` com campos: `source`, `sink`, `sourceKey`, `sinkKey`, `maxFlow`, `message`, `complexity`.

Contrato (inputs/outputs) — resumo prático
------------------------------------------
Inputs obrigatórios:
- `g`: objeto `Graph<V,E>` onde cada `Edge<V,E>` contém um peso/capacidade do tipo `E`.
- `source` / `sink`: vértices (ou chaves inteiras com overloads) existentes em `g`.
- `cmp`: `Comparator<E>` para comparar capacidades.
- `add`: `BinaryOperator<E>` para somar capacidades.
- `sub`: `BinaryOperator<E>` para subtrair capacidades (há overload que deriva `sub` para tipos numéricos comuns).
- `zero`: elemento zero do tipo `E` (ex.: `Integer.valueOf(0)`).

Output (`MaxFlow.Result<V,E>`):
- `source`: objeto de vértice original (quando disponível)
- `sink`: objeto de vértice de destino
- `sourceKey` / `sinkKey`: chaves inteiras (se chamadas por chave)
- `maxFlow`: valor do fluxo máximo (tipo `E`) — pode ser `null` em caso de entrada inválida
- `message`: texto descrevendo o resultado (p.ex.: "Max flow computed successfully.")
- `complexity`: string com complexidade (ex.: "O(V·E^2)")

Erros e casos especiais (comportamento atual)
---------------------------------------------
- Capacidades negativas: provê validação e lança `IllegalArgumentException` (conforme US — capacidades devem ser não-negativas).
- Vértices inválidos (source ou sink não pertencem ao grafo): `compute` retorna um `Result` com `maxFlow = null` e `message = "Invalid source or sink vertex."` (ou similar para chaves).
- `source.equals(sink)`: retorna `maxFlow = zero` e `message = "Source equals sink - max flow is 0."`.
- Se não existe caminho aumentante entre source e sink: retorna `maxFlow = zero` e `message = "No augmenting path found - max flow is 0."`.
- Tipos numéricos suportados pelo helper `defaultSubtract`: `Integer`, `Double`, `Long`, `Float`, `BigInteger`, `BigDecimal`. Para outros tipos, `UnsupportedOperationException` será lançada se se usar o overload que deriva `sub`.

Complexidade e performance
--------------------------
- Algoritmo: Edmonds–Karp (variante de Ford–Fulkerson com BFS para encontrar caminhos aumentantes).
- Complexidade documental: O(V·E^2) — reproduzida no campo `complexity` do `Result`.
- Observação prática: para grafos muito grandes, Edmonds–Karp pode ser lento; considere implementar Dinic ou heurísticas/limites de tempo se for necessário em produção.

Exemplos de uso (Java)
----------------------
- Exemplo usando capacidades `Integer` e chamadas por chaves (assume `g` implementa os métodos descritos):

```java
Comparator<Integer> cmp = Integer::compare;
BinaryOperator<Integer> add = Integer::sum;
Integer zero = Integer.valueOf(0);

MaxFlow.Result<MyVertex, Integer> res = MaxFlow.compute(g, sourceKey, sinkKey, cmp, add, zero);
if (res.maxFlow != null) {
    System.out.println("Source key: " + res.sourceKey);
    System.out.println("Sink key: " + res.sinkKey);
    System.out.println("Max flow: " + res.maxFlow);
    System.out.println("Message: " + res.message);
}
```

- Exemplo com objetos `V` diretamente:

```java
MaxFlow.Result<Vertex, Integer> r = MaxFlow.compute(g, sourceVertex, sinkVertex, Integer::compare, Integer::sum, Integer.valueOf(0));
```

Casos de teste sugeridos (para aceitar USEI14)
----------------------------------------------
Implemente pelo menos os três testes abaixo em JUnit (ou usar a suite de testes do projeto):
1) Source == Sink
   - Setup: grafo qualquer; chamar `compute` com `source == sink`.
   - Esperado: `maxFlow == zero`, `message` indicando igualdade, `complexity == "O(1)"`.
2) Sem caminho possível
   - Ex.: vértices A,B,C; aresta A->B cap=5; sem caminho A->C.
   - Chamar `compute(g, A, C, ...)`.
   - Esperado: `maxFlow == zero`, mensagem "No augmenting path found - max flow is 0.".
3) Grafo de exemplo clássico
   - Grafo:
     - s->a cap 3
     - s->b cap 2
     - a->b cap 1
     - a->t cap 2
     - b->t cap 3
   - Esperado: `maxFlow` de `s` para `t` == 4.

Testes adicionais: validação de capacidades negativas (deve lançar `IllegalArgumentException`), tipos suportados por `defaultSubtract`, comportamento para chaves inválidas.

Observações operacionais / recomendações
--------------------------------------
- Unidade: a US menciona "trens/dia" — a implementação não anexa unidade às capacidades. Documente no contrato (ex.: as capacidades representarem trens/dia) ou padronize a unidade antes de invocar `MaxFlow`.
- Para ajudar priorização de upgrades (requisito operacional mencionado na US), pode ser útil estender `Result` para também listar arestas saturadas (ou o corte mínimo) e fluxos por aresta residual. Isso não é obrigatório para cumprir a US mínima, mas agrega valor.
- Considere adicionar um overload/adapter que normalize capacidades quando a entrada usar diferentes unidades (p.ex., trens/hora vs trens/dia).
- Se a funcionalidade deverá ser consumida por um frontend/serviço, criar uma história separada para expor um endpoint REST é recomendado (mantém escopo da US original focado no algoritmo).

Critérios de Aceitação (sugestão curta para a US)
------------------------------------------------
- Implementação retorna `Result` com `sourceKey`, `sinkKey`, `maxFlow` (não-negativo), `message` e `complexity`.
- Lançamento de `IllegalArgumentException` para capacidades negativas.
- Testes automatizados: os 3 casos básicos funcionando com valores esperados.
- Documentação de unidade e tipos numéricos suportados.

Próximos passos que posso fazer por si
-------------------------------------
- Gerar os 3 testes JUnit no diretório `src/test/java` (posso criar os arquivos de teste com grafos mínimos).
- Criar um pequeno exemplo executável (runner) que constrói um grafo e imprime o `Result`.
- Adicionar uma melhoria que lista arestas saturadas no `Result`.

---
Arquivo gerado: `docs/USEI14_MAXFLOW.md` (local no repositório). Se quiser, agora crio os testes JUnit sugeridos ou um runner de exemplo — diga qual prefere que eu faça primeiro.
