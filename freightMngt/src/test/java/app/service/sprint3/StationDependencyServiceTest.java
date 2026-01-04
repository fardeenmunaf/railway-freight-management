package app.service.sprint3;

import app.model.Station;
import app.service.sprint3.StationDependencyService.Result;
import app.utils.graph.Edge;
import app.utils.graph.Graph;
import app.utils.graph.map.MapGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de testes para o serviço StationDependencyService.
 *
 * Verifica:
 * <ul>
 *   <li>Ordenação topológica sem ciclos</li>
 *   <li>Deteção de ciclos simples e complexos</li>
 *   <li>Estações e arestas envolvidas em ciclos</li>
 *   <li>Comportamento com grafos vazios</li>
 * </ul>
 *
 * @author Fardeen
 * @version 1.0
 */
public class StationDependencyServiceTest {

    private StationDependencyService service;

    @BeforeEach
    void setUp() {
        service = new StationDependencyService();
    }

    /**
     * Cria uma estação auxiliar com ID e nome.
     */
    private Station station(int id, String name) {
        Station s = new Station();
        s.setFacilityId(id);
        s.setName(name);
        return s;
    }

    /**
     * Testa ordenação topológica simples sem ciclos.
     *
     * A -> B -> C
     */
    @Test
    void testTopologicalSortWithoutCycles() {
        Graph<Station, Integer> graph = new MapGraph<>(true);

        Station a = station(1, "A");
        Station b = station(2, "B");
        Station c = station(3, "C");

        graph.addVertex(a);
        graph.addVertex(b);
        graph.addVertex(c);

        graph.addEdge(a, b, 1);
        graph.addEdge(b, c, 1);

        Result<Integer> result = service.planUpgrades(graph);

        assertFalse(result.hasCycles());
        assertEquals(3, result.getTopoOrder().size());
        assertEquals(List.of(a, b, c), result.getTopoOrder());
        assertTrue(result.getCycleStations().isEmpty());
        assertTrue(result.getCycleEdges().isEmpty());
        assertEquals("O(V+E)", result.getComplexity());
    }

    /**
     * Testa deteção de ciclo simples.
     *
     * A -> B -> A
     */
    @Test
    void testSimpleCycleDetection() {
        Graph<Station, Integer> graph = new MapGraph<>(true);

        Station a = station(1, "A");
        Station b = station(2, "B");

        graph.addVertex(a);
        graph.addVertex(b);

        graph.addEdge(a, b, 1);
        graph.addEdge(b, a, 1);

        Result<Integer> result = service.planUpgrades(graph);

        assertTrue(result.hasCycles());
        assertTrue(result.getTopoOrder().isEmpty());

        Set<Station> cycleStations = result.getCycleStations();
        assertEquals(2, cycleStations.size());
        assertTrue(cycleStations.contains(a));
        assertTrue(cycleStations.contains(b));

        Set<Edge<Station, Integer>> cycleEdges = result.getCycleEdges();
        assertEquals(2, cycleEdges.size());
    }

    /**
     * Testa deteção de ciclo complexo.
     *
     * A -> B -> C -> A
     */
    @Test
    void testComplexCycleDetection() {
        Graph<Station, Integer> graph = new MapGraph<>(true);

        Station a = station(1, "A");
        Station b = station(2, "B");
        Station c = station(3, "C");

        graph.addVertex(a);
        graph.addVertex(b);
        graph.addVertex(c);

        graph.addEdge(a, b, 1);
        graph.addEdge(b, c, 1);
        graph.addEdge(c, a, 1);

        Result<Integer> result = service.planUpgrades(graph);

        assertTrue(result.hasCycles());
        assertTrue(result.getTopoOrder().isEmpty());

        assertEquals(3, result.getCycleStations().size());
        assertTrue(result.getCycleStations().containsAll(List.of(a, b, c)));
        assertEquals(3, result.getCycleEdges().size());
    }

    /**
     * Testa grafo com um único vértice e sem dependências.
     */
    @Test
    void testSingleVertexGraph() {
        Graph<Station, Integer> graph = new MapGraph<>(true);

        Station a = station(1, "A");
        graph.addVertex(a);

        Result<Integer> result = service.planUpgrades(graph);

        assertFalse(result.hasCycles());
        assertEquals(1, result.getTopoOrder().size());
        assertEquals(a, result.getTopoOrder().get(0));
    }

    /**
     * Testa grafo vazio.
     */
    @Test
    void testEmptyGraph() {
        Graph<Station, Integer> graph = new MapGraph<>(true);

        Result<Integer> result = service.planUpgrades(graph);

        assertFalse(result.hasCycles());
        assertTrue(result.getTopoOrder().isEmpty());
        assertTrue(result.getCycleStations().isEmpty());
        assertTrue(result.getCycleEdges().isEmpty());
    }

    /**
     * Testa ordenação com múltiplas dependências convergentes.
     *
     * A -> C
     * B -> C
     */
    @Test
    void testMultipleDependenciesWithoutCycle() {
        Graph<Station, Integer> graph = new MapGraph<>(true);

        Station a = station(1, "A");
        Station b = station(2, "B");
        Station c = station(3, "C");

        graph.addVertex(a);
        graph.addVertex(b);
        graph.addVertex(c);

        graph.addEdge(a, c, 1);
        graph.addEdge(b, c, 1);

        Result<Integer> result = service.planUpgrades(graph);

        assertFalse(result.hasCycles());
        assertEquals(3, result.getTopoOrder().size());
        assertTrue(result.getTopoOrder().indexOf(c) > result.getTopoOrder().indexOf(a));
        assertTrue(result.getTopoOrder().indexOf(c) > result.getTopoOrder().indexOf(b));
    }

    /**
     * Teste de fluxo completo do planeamento de upgrades.
     */
    @Test
    void testCompleteUpgradePlanningFlow() {

        /* =========================================================
           1. Criar grafo de dependências (DAG)
           ========================================================= */
        Graph<Station, String> graph = new MapGraph<>(true);

        Station porto = station(1, "Porto");
        Station aveiro = station(2, "Aveiro");
        Station coimbra = station(3, "Coimbra");
        Station lisboa = station(4, "Lisboa");
        Station faro = station(5, "Faro");

        graph.addVertex(porto);
        graph.addVertex(aveiro);
        graph.addVertex(coimbra);
        graph.addVertex(lisboa);
        graph.addVertex(faro);

        /*
            Dependências de upgrade:
            Porto  -> Aveiro
            Aveiro -> Coimbra
            Coimbra -> Lisboa
            Lisboa -> Faro
         */
        graph.addEdge(porto, aveiro, "upgrade first");
        graph.addEdge(aveiro, coimbra, "upgrade first");
        graph.addEdge(coimbra, lisboa, "upgrade first");
        graph.addEdge(lisboa, faro, "upgrade first");

        /* =========================================================
           2. Planeamento SEM ciclos
           ========================================================= */
        Result<String> result = service.planUpgrades(graph);

        assertNotNull(result, "Resultado não deve ser null");
        assertFalse(result.hasCycles(), "Não deve haver ciclos");

        List<Station> order = result.getTopoOrder();
        assertEquals(5, order.size(), "Devem existir 5 estações na ordem");

        // Verifica ordem lógica
        assertTrue(order.indexOf(porto) < order.indexOf(aveiro));
        assertTrue(order.indexOf(aveiro) < order.indexOf(coimbra));
        assertTrue(order.indexOf(coimbra) < order.indexOf(lisboa));
        assertTrue(order.indexOf(lisboa) < order.indexOf(faro));

        assertEquals("O(V+E)", result.getComplexity());

        System.out.println("✓ Ordem de upgrades válida:");
        order.forEach(s -> System.out.println(" - " + s.getName()));

        /* =========================================================
           3. Introduzir CICLO no grafo
           Faro -> Porto
           ========================================================= */
        graph.addEdge(faro, porto, "cycle introduced");

        /* =========================================================
           4. Recalcular planeamento (COM ciclo)
           ========================================================= */
        Result<String> cycleResult = service.planUpgrades(graph);

        assertTrue(cycleResult.hasCycles(), "Deve detetar ciclo");
        assertTrue(cycleResult.getTopoOrder().isEmpty(),
                "Com ciclo, não deve existir ordenação topológica");

        Set<Station> cycleStations = cycleResult.getCycleStations();
        assertEquals(5, cycleStations.size(),
                "Todas as estações fazem parte do ciclo");

        assertTrue(cycleStations.containsAll(
                List.of(porto, aveiro, coimbra, lisboa, faro)));

        Set<Edge<Station, String>> cycleEdges = cycleResult.getCycleEdges();
        assertFalse(cycleEdges.isEmpty(), "Arestas do ciclo não devem estar vazias");

        System.out.println("\n✓ Ciclo detetado envolvendo estações:");
        cycleStations.forEach(s -> System.out.println(" - " + s.getName()));

        System.out.println("\n✓ Arestas do ciclo:");
        cycleEdges.forEach(e ->
                System.out.println(
                        e.getVOrig().getName() + " -> " +
                                e.getVDest().getName() + " [" + e.getWeight() + "]"
                )
        );

        System.out.println("\n✓ Teste de fluxo completo executado com sucesso!");
    }
}
