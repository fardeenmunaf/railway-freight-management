# Caminhos Ferroviários Mais Curtos

## 📌 Descrição

Sistema que calcula o **caminho mais curto entre estações ferroviárias belgas**, utilizando:

* **Dijkstra** (pesos positivos)
* **Bellman-Ford** (pesos negativos)

Os dados são carregados a partir dos ficheiros `stations.csv` e `lines.csv`.

---

## ⚙️ Como Funciona

### 1️⃣ Grafo Não Direcionado

* Cada linha do `lines.csv` gera **duas arestas** (A → B e B → A)
* Total de linhas no grafo = `2 × número real de ligações`
* Exemplo:

  ```text
  1266,621,dist,cap,-3.42
  ```

  Gera duas arestas bidirecionais

---

### 2️⃣ Lógica de Seleção de Algoritmo

1. Tenta **Dijkstra** primeiro (mais rápido)
2. Se encontrar caminho:

    * Contém arestas negativas? → **Bellman-Ford**
    * Apenas arestas positivas? → **Dijkstra**
3. Se Dijkstra falhar:

    * Tenta **Bellman-Ford**
4. Se Bellman-Ford falhar:

    * Existe **ciclo negativo** (reporta estações e arestas)

---

### 3️⃣ Critérios para Bellman-Ford

* Custo total **positivo**, mas caminho contém arestas negativas
* Custo total **negativo**
* Existência de qualquer aresta negativa

➡️ Nestes casos, Bellman-Ford é obrigatório

---

## 🧪 Exemplos Práticos

### ✅ Dijkstra (caminho totalmente positivo)

**Input:** `906 → 151`

**Output:**

```text
NIEUWKERKEN(ID:906) → BEVEREN(W)(ID:151) [4.81]
Custo total: 4.81
Algoritmo: Dijkstra
```

---

### ⚠️ Bellman-Ford (caminho com custo negativo intermédio)

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

## 📁 Estrutura de Ficheiros

```text
src/main/java/app/sampleDataset/
├── stations.csv   # ID, Nome, Latitude, Longitude (559 estações)
└── lines.csv      # ID1, ID2, Distância, Capacidade, Custo
                   # 1382 ligações × 2 (grafo bidirecionado)
```

---

## ▶️ Como Usar

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

## 🔧 Detalhes Técnicos

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

## ✅ Casos de Teste Validados

| Partida | Chegada | Algoritmo    | Custo Total    | Observações                 |
| ------: | ------: | ------------ | -------------- | --------------------------- |
|     906 |     151 | Dijkstra     | 4.81           | Apenas custos positivos     |
|     351 |     434 | Bellman-Ford | -4.27          | Custo total negativo        |
|    1266 |    1270 | Bellman-Ford | 88.53          | Negativo intermédio         |
|     259 |     258 | Bellman-Ford | Ciclo Negativo | Ciclo detetado corretamente |

---

## 📚 Javadocs

Todos os métodos estão documentados em **português**, incluindo:

* `@param` — descrição dos parâmetros
* `@return` — valores de retorno
* Exemplos de utilização
* Explicação clara da lógica interna
