# GUIA: Como Explicar o Código de Pesquisa de Estações

## 1. HaversineDistance — Calcular distância entre coordenadas

**O que é:**
- Classe utilitária que calcula a distância real (em km) entre dois pontos GPS (latitude, longitude).
- Usa a **fórmula de Haversine** que considera a curvatura da Terra.

**Como explicar:**
```
"A classe HaversineDistance calcula a distância geodésica entre dois pontos.
A fórmula de Haversine é:
  a = sin²(Δlat/2) + cos(lat1) × cos(lat2) × sin²(Δlon/2)
  c = 2 × atan2(√a, √(1-a))
  distância = Raio_Terra × c

Raio da Terra = 6371 km
O método calculate() recebe (lat1, lon1, lat2, lon2) em graus e devolve km."
```

**Exemplo prático:**
- Lisboa (38.72, -9.14) → Porto (41.16, -8.63) ≈ 274 km

---

## 2. StationWithDistance — Par (estação, distância)

**O que é:**
- Classe simples que guarda uma estação e a sua distância ao ponto de consulta.
- Tem `Comparable` para ordenar por distância (menor primeiro).

**Como explicar:**
```
"É um wrapper que associa uma Station com a distância calculada até o ponto de consulta.
Implementa Comparable para podermos ordenar os resultados por distância crescente.
Exemplo: StationWithDistance(Lisboa, 10.5 km)"
```

---

## 3. findNearestStations — Encontrar N estações mais próximas

**O que faz:**
- Método público que encontra as N estações mais próximas de um ponto (lat, lon).
- Devolve lista ordenada por distância (mais próxima primeiro).

**Como explicar:**
```
"Este método:
1. Cria uma lista bestCandidates vazia (vai guardar os N melhores)
2. Chama findNearestRecursive() para percorrer a árvore
3. No fim, ordena bestCandidates por distância
4. Devolve a lista

Entrada: (latitude, longitude, n)
Saída: Lista de N estações mais próximas com as suas distâncias"
```

**Exemplo:**
```java
tree.findNearestStations(40.0, -8.0, 3)
→ [Estação A (5 km), Estação B (12 km), Estação C (18 km)]
```

---

## 4. findNearestRecursive — O NÚCLEO (mais importante!)

**O que faz:**
- Percorre a árvore 2D de forma **inteligente** para encontrar os vizinhos mais próximos.
- Usa **poda (pruning)** para evitar explorar zonas que não podem ter estações melhores.

**Como explicar PASSO-A-PASSO:**

### Passo 1: Processar o nó atual
```
"Para cada estação no bucket do nó:
  - Calcula distância com Haversine
  - Se ainda não tenho N candidatos → adiciono
  - Se já tenho N, comparo com o pior candidato:
      Se esta for melhor → substituo o pior"
```

### Passo 2: Escolher qual filho visitar primeiro
```
"Comparo o ponto de consulta com a chave do nó:
  - Se targetLat < node.key → visito LEFT primeiro
  - Senão → visito RIGHT primeiro
  
(Visito o lado mais provável de ter estações próximas)"
```

### Passo 3: Visitar o primeiro filho (recursão)
```
"Chamo findNearestRecursive() no filho mais promissor"
```

### Passo 4: DECIDIR se visito o outro lado (PODA!)
```
"Calculo a distância ao plano de corte:
  planeDistance = |targetValue - node.key| (em graus)
  planeDistanceKm ≈ planeDistance × 111 km

Só visito o outro lado SE:
  - Ainda não tenho N candidatos OU
  - planeDistanceKm < distância do pior candidato atual

Se NÃO: PODO! (não visito, poupo tempo)"
```

### Passo 5: Terminar
```
"Volto ao nível anterior da recursão"
```

**IMPORTANTE — O truque da poda:**
```
"Imagina que já encontrei 3 estações e a pior está a 20 km.
Se o plano de corte está a 50 km do ponto de consulta,
então TUDO do outro lado está pelo menos a 50 km.
Logo, não vale a pena ir lá! → PODO essa sub-árvore"
```

---

## 5. Exemplo Numérico Completo

**Setup:**
```
Estações:
  A = (40.0, -8.0)
  B = (40.1, -8.0)  
  C = (41.0, -8.0)

Árvore:
    [B: lat=40.1]
    /            \
[A: 40.0]     [C: 41.0]

Consulta: target=(40.05, -8.0), n=2
```

**Execução:**

**1. Visito root (B):**
- Dist(B, target) = Haversine(40.05, 40.1) ≈ **5.5 km**
- bestCandidates = `[B: 5.5 km]`

**2. Escolho filho:**
- targetLat (40.05) < node.key (40.1) → vou para **LEFT** (A)

**3. Visito A:**
- Dist(A, target) = Haversine(40.05, 40.0) ≈ **5.5 km**
- Ainda tenho espaço (size < n=2) → adiciono
- bestCandidates = `[A: 5.5 km, B: 5.5 km]`

**4. Volto ao root — DECISÃO DE PODA:**
- planeDistance = |40.05 - 40.1| = **0.05 graus**
- planeDistanceKm = 0.05 × 111 = **5.55 km**
- Pior candidato = **5.5 km**
- 5.55 < 5.5? **NÃO!**
- **NÃO visito RIGHT (C)** → PODA!

**5. Resultado final:**
```
[A: 5.5 km, B: 5.5 km]
(C nem foi avaliada — poupámos tempo!)
```

---

## 6. Complexidade Temporal — Análise Detalhada

### 6.1. Construção da Árvore: **O(N log N)**

**Onde está:**
- **Método:** `bulkBuild(List<Station> stations)`
- **Linha do código:** Método `bulkBuildRecursive()`

**Porquê O(N log N):**
```
1. Para cada nível da recursão:
   - Usa AVLTree para agrupar e ordenar estações → O(N log N) total
   - Encontra mediana das chaves únicas → O(N)
   - Particiona estações em left/equal/right → O(N)

2. Altura da árvore: log N níveis (árvore balanceada)

3. Total: O(N log N) × log N níveis, mas amortizado = O(N log N)
```

**No código:**
```java
// bulkBuildRecursive() — este loop roda para TODAS as estações
for (Station s : stations) {
    Double key = useLatitude ? s.getLat() : s.getLon();
    avl.insert(key, s);  // ← AVL insert: O(log N)
}
// Este loop roda N vezes → N × log N = O(N log N)
```

---

### 6.2. Procura N Vizinhos Mais Próximos

**Onde está:**
- **Método:** `findNearestStations()` chama `findNearestRecursive()`

#### **Melhor/Médio Caso: O(log N)**

**Porquê:**
```
A poda funciona eficientemente:
- Visitamos apenas O(log N) nós (descemos na árvore como numa binary search)
- Em cada nó: calculamos distância para 1 estação (ou poucas no bucket) → O(1)
- Total: O(log N) nós × O(1) = O(log N)
```

**No código (a poda aqui):**
```java
// findNearestRecursive() — DECISÃO DE PODA
if (bestCandidates.size() < n ||
    planeDistanceKm < bestCandidates.get(bestCandidates.size() - 1).distance) {
    findNearestRecursive(secondChild, ...);  // ← SÓ visita se necessário!
}
// Se a condição for FALSE → NÃO visita a sub-árvore! (poupamos tempo)
```

**Quando acontece:**
- Estações bem distribuídas geograficamente
- A poda elimina ~metade das sub-árvores em cada nível

---

#### **Pior Caso: O(N)**

**Porquê:**
```
A poda NÃO funciona:
- Visitamos TODOS os nós da árvore
- Em cada nó: calculamos distância → O(1)
- Total: N nós × O(1) = O(N)
```

**Quando acontece:**
1. **Estações em linha reta:** todas a distâncias semelhantes
2. **Ponto de consulta no centro de todas as estações**
3. **Todas as estações à mesma distância do ponto**

**Exemplo pior caso:**
```
Estações dispostas em círculo, consulta no centro:
    A
  B   C
D   X   E  ← X = ponto de consulta
  F   G
    H

Todas as estações estão aproximadamente à mesma distância de X!
→ A poda nunca elimina nenhuma sub-árvore
→ Temos de visitar TODAS (A, B, C, D, E, F, G, H) = O(N)
```

---

### 6.3. Operações Auxiliares

**Collections.sort() na lista bestCandidates:**
```java
Collections.sort(bestCandidates);  // ← O(n log n) onde n = número de vizinhos
```
- Isto roda a cada inserção → O(n log n) por inserção
- Se procuramos n vizinhos pequeno (ex: n=3) → impacto negligenciável
- Se n for grande (ex: n=1000) → pode ser gargalo!

**Solução melhor (sugestão):**
- Usar `PriorityQueue` (max-heap) → inserção O(log n)

---

### 6.4. Resumo Visual

```
┌─────────────────────────┬──────────────┬─────────────┐
│ Operação                │ Complexidade │ Onde está   │
├─────────────────────────┼──────────────┼─────────────┤
│ bulkBuild()             │ O(N log N)   │ Construção  │
│ findNearestStations()   │              │             │
│   - Melhor/Médio caso   │ O(log N)     │ Com poda    │
│   - Pior caso           │ O(N)         │ Sem poda    │
│ Collections.sort()      │ O(n log n)   │ A cada add  │
│   (n = num vizinhos)    │              │             │
└─────────────────────────┴──────────────┴─────────────┘

N = número total de estações
n = número de vizinhos procurados (normalmente n << N)
```

**Como explicar:**
```
"A construção é O(N log N) porque usamos AVL para ordenar em bulkBuildRecursive.

A procura no melhor caso é O(log N) graças à PODA na linha:
  'if (planeDistanceKm < worstCandidateDistance)'
Isto elimina sub-árvores que não podem ter estações melhores.

No pior caso é O(N) quando a poda falha e visitamos tudo.

Na prática, com estações bem distribuídas, é quase sempre O(log N)!"
```

---

## 7. Resumo Final (para explicar tudo em 1 minuto)

```
"Temos uma árvore 2D que organiza estações por lat/lon alternadamente.

Para encontrar as N mais próximas:
1. Percorremos a árvore recursivamente
2. Calculamos distâncias com Haversine
3. Mantemos os N melhores candidatos
4. PODAMOS sub-árvores que não podem ter nada melhor
   (comparando distância ao plano de corte vs pior candidato)

Isto é muito mais rápido que calcular distância a TODAS as estações!"
```



