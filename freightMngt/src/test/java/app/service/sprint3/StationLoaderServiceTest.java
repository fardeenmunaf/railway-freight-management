package app.service.sprint3;

import app.model.Station;
import app.utils.graph.Edge;
import app.utils.graph.Graph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de testes para o serviço StationLoaderService.
 *
 * Verifica todas as funcionalidades do serviço incluindo:
 * <ul>
 *   <li>Carregamento de estações e linhas</li>
 *   <li>Validação de existência de estações</li>
 *   <li>Cálculo de caminhos mais curtos</li>
 *   <li>Detecção de ciclos negativos</li>
 *   <li>Seleção automática de algoritmos</li>
 * </ul>
 *
 * @author Fardeen
 * @version 1.0
 */
public class StationLoaderServiceTest {

    private StationLoaderService service;

    @TempDir
    Path tempDir;

    /**
     * Configuração executada antes de cada teste.
     * Inicializa nova instância do serviço.
     */
    @BeforeEach
    void setUp() {
        service = new StationLoaderService();
    }

    /**
     * Testa a inicialização básica do serviço.
     * Verifica se o grafo e mapa de estações são criados corretamente.
     */
    @Test
    void testServiceInitialization() {
        assertNotNull(service.getGraph());
        assertNotNull(service.getStationMap());
        assertEquals(0, service.getTotalStations());
        assertEquals(0, service.getTotalConnections());
    }

    /**
     * Testa o carregamento de estações a partir de ficheiro CSV válido.
     * Verifica se as estações são corretamente parseadas e adicionadas ao sistema.
     *
     * @throws IOException se houver erro na criação do ficheiro temporário
     */
    @Test
    void testLoadStations() throws IOException {
        Path stationsFile = tempDir.resolve("stations.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(stationsFile.toFile()))) {
            writer.write("idEstacao,nome,latitude,longitude\n");
            writer.write("1,Estação Central,41.1496,-8.6109\n");
            writer.write("2,Estação Norte,41.1500,-8.6200\n");
            writer.write("3,Estação Sul,41.1400,-8.6100\n");
        }

        TestableStationLoaderService testService = new TestableStationLoaderService();
        testService.loadStationsPublic(stationsFile.toString());

        assertEquals(3, testService.getTotalStations());
        assertTrue(testService.stationExists(1));
        assertTrue(testService.stationExists(2));
        assertTrue(testService.stationExists(3));
        assertFalse(testService.stationExists(4));
    }

    /**
     * Testa o carregamento de estações com linhas malformadas.
     * Verifica se linhas inválidas são ignoradas sem falhar o processo.
     *
     * @throws IOException se houver erro na criação do ficheiro temporário
     */
    @Test
    void testLoadStationsWithMalformedData() throws IOException {
        Path stationsFile = tempDir.resolve("stations.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(stationsFile.toFile()))) {
            writer.write("idEstacao,nome,latitude,longitude\n");
            writer.write("1,Estação Central,41.1496,-8.6109\n");
            writer.write("invalid,data\n");
            writer.write("2,Estação Norte,41.1500,-8.6200\n");
        }

        TestableStationLoaderService testService = new TestableStationLoaderService();
        testService.loadStationsPublic(stationsFile.toString());

        assertEquals(2, testService.getTotalStations());
        assertTrue(testService.stationExists(1));
        assertTrue(testService.stationExists(2));
    }

    /**
     * Testa o carregamento de linhas (conexões) entre estações.
     * Verifica se as arestas são criadas corretamente com os custos apropriados.
     * <p>
     * Nota: Grafo não-direcionado duplica cada aresta (A-B e B-A).
     *
     * @throws IOException se houver erro na criação dos ficheiros temporários
     */
    @Test
    void testLoadLines() throws IOException {
        Path stationsFile = tempDir.resolve("stations.csv");
        Path linesFile = tempDir.resolve("lines.csv");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(stationsFile.toFile()))) {
            writer.write("idEstacao,nome,latitude,longitude\n");
            writer.write("1,Estação A,0,0\n");
            writer.write("2,Estação B,0,0\n");
            writer.write("3,Estação C,0,0\n");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(linesFile.toFile()))) {
            writer.write("id1,id2,col3,col4,custo\n");
            writer.write("1,2,x,y,5.5\n");
            writer.write("2,3,x,y,3.2\n");
            writer.write("1,3,x,y,10.0\n");
        }

        TestableStationLoaderService testService = new TestableStationLoaderService();
        testService.loadStationsPublic(stationsFile.toString());
        testService.loadLinesPublic(linesFile.toString());

        assertEquals(3, testService.getTotalStations());
        assertEquals(6, testService.getTotalConnections()); // 3 linhas × 2 direções
    }

    /**
     * Testa o carregamento de linhas com dados inválidos.
     * Verifica se linhas malformadas são ignoradas corretamente.
     *
     * @throws IOException se houver erro na criação dos ficheiros temporários
     */
    @Test
    void testLoadLinesWithInvalidData() throws IOException {
        Path stationsFile = tempDir.resolve("stations.csv");
        Path linesFile = tempDir.resolve("lines.csv");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(stationsFile.toFile()))) {
            writer.write("idEstacao,nome,latitude,longitude\n");
            writer.write("1,Estação A,0,0\n");
            writer.write("2,Estação B,0,0\n");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(linesFile.toFile()))) {
            writer.write("id1,id2,col3,col4,custo\n");
            writer.write("1,2,x,y,5.5\n");
            writer.write("invalid,line\n");
            writer.write("1,999,x,y,3.0\n");
        }

        TestableStationLoaderService testService = new TestableStationLoaderService();
        testService.loadStationsPublic(stationsFile.toString());
        testService.loadLinesPublic(linesFile.toString());

        assertEquals(2, testService.getTotalConnections()); // 1 linha válida × 2 direções
    }

    /**
     * Testa a verificação de existência de estações.
     * Valida tanto IDs existentes quanto inexistentes.
     *
     * @throws IOException se houver erro na criação do ficheiro temporário
     */
    @Test
    void testStationExists() throws IOException {
        Path stationsFile = tempDir.resolve("stations.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(stationsFile.toFile()))) {
            writer.write("idEstacao,nome,latitude,longitude\n");
            writer.write("100,Estação Teste,0,0\n");
        }

        TestableStationLoaderService testService = new TestableStationLoaderService();
        testService.loadStationsPublic(stationsFile.toString());

        assertTrue(testService.stationExists(100));
        assertFalse(testService.stationExists(200));
        assertFalse(testService.stationExists(null));
    }

    /**
     * Testa a obtenção da lista formatada de estações.
     * Verifica se as estações são ordenadas por ID e formatadas corretamente.
     *
     * @throws IOException se houver erro na criação do ficheiro temporário
     */
    @Test
    void testGetFormattedStationList() throws IOException {
        Path stationsFile = tempDir.resolve("stations.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(stationsFile.toFile()))) {
            writer.write("idEstacao,nome,latitude,longitude\n");
            writer.write("3,Estação C,0,0\n");
            writer.write("1,Estação A,0,0\n");
            writer.write("2,Estação B,0,0\n");
        }

        TestableStationLoaderService testService = new TestableStationLoaderService();
        testService.loadStationsPublic(stationsFile.toString());

        List<String> formatted = testService.getFormattedStationList();
        assertEquals(3, formatted.size());
        assertTrue(formatted.get(0).contains("ID: 1"));
        assertTrue(formatted.get(0).contains("Estação A"));
        assertTrue(formatted.get(1).contains("ID: 2"));
        assertTrue(formatted.get(2).contains("ID: 3"));
    }

    /**
     * Testa o cálculo de caminho mais curto usando algoritmo de Dijkstra.
     * Cenário com apenas custos positivos onde Dijkstra é o algoritmo ideal.
     *
     * @throws IOException se houver erro na criação dos ficheiros temporários
     */
    @Test
    void testCalculateBestPathWithDijkstra() throws IOException {
        Path stationsFile = tempDir.resolve("stations.csv");
        Path linesFile = tempDir.resolve("lines.csv");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(stationsFile.toFile()))) {
            writer.write("idEstacao,nome,latitude,longitude\n");
            writer.write("1,Estação A,0,0\n");
            writer.write("2,Estação B,0,0\n");
            writer.write("3,Estação C,0,0\n");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(linesFile.toFile()))) {
            writer.write("id1,id2,col3,col4,custo\n");
            writer.write("1,2,x,y,5.0\n");
            writer.write("2,3,x,y,3.0\n");
        }

        TestableStationLoaderService testService = new TestableStationLoaderService();
        testService.loadStationsPublic(stationsFile.toString());
        testService.loadLinesPublic(linesFile.toString());

        StationLoaderService.PathResultWithAlgorithm result = testService.calculateBestPath(1, 3);

        assertNotNull(result);
        assertEquals("Dijkstra", result.getAlgorithmUsed());
        assertFalse(result.hasNegativeCycle());
        assertEquals(8.0, result.getPathResult().totalCost, 0.01);
        assertEquals(3, result.getPathResult().path.size());
    }

    /**
     * Testa o cálculo de caminho com custos negativos usando Bellman-Ford.
     * Verifica se o algoritmo é automaticamente trocado quando há pesos negativos.
     *
     * @throws IOException se houver erro na criação dos ficheiros temporários
     */
    @Test
    void testCalculateBestPathWithNegativeWeights() throws IOException {
        Path stationsFile = tempDir.resolve("stations.csv");
        Path linesFile = tempDir.resolve("lines.csv");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(stationsFile.toFile()))) {
            writer.write("idEstacao,nome,latitude,longitude\n");
            writer.write("1,Estação A,0,0\n");
            writer.write("2,Estação B,0,0\n");
            writer.write("3,Estação C,0,0\n");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(linesFile.toFile()))) {
            writer.write("id1,id2,col3,col4,custo\n");
            writer.write("1,2,x,y,-2.0\n");
            writer.write("2,3,x,y,5.0\n");
        }

        TestableStationLoaderService testService = new TestableStationLoaderService();
        testService.loadStationsPublic(stationsFile.toString());
        testService.loadLinesPublic(linesFile.toString());

        StationLoaderService.PathResultWithAlgorithm result = testService.calculateBestPath(1, 3);

        assertNotNull(result);
        assertEquals("Bellman-Ford", result.getAlgorithmUsed());
        assertFalse(result.hasNegativeCycle());
    }

    /**
     * Testa o cálculo de caminho quando não existe conexão entre estações.
     * Verifica se retorna resultado indicando ausência de caminho.
     *
     * @throws IOException se houver erro na criação dos ficheiros temporários
     */
    @Test
    void testCalculateBestPathNoConnection() throws IOException {
        Path stationsFile = tempDir.resolve("stations.csv");
        Path linesFile = tempDir.resolve("lines.csv");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(stationsFile.toFile()))) {
            writer.write("idEstacao,nome,latitude,longitude\n");
            writer.write("1,Estação A,0,0\n");
            writer.write("2,Estação B,0,0\n");
            writer.write("3,Estação C,0,0\n");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(linesFile.toFile()))) {
            writer.write("id1,id2,col3,col4,custo\n");
            writer.write("1,2,x,y,5.0\n");
        }

        TestableStationLoaderService testService = new TestableStationLoaderService();
        testService.loadStationsPublic(stationsFile.toString());
        testService.loadLinesPublic(linesFile.toString());

        StationLoaderService.PathResultWithAlgorithm result = testService.calculateBestPath(1, 3);

        assertNotNull(result);
        assertTrue(result.hasNegativeCycle() || result.getPathResult().path.isEmpty());
    }

    /**
     * Testa o cálculo de caminho com estações inexistentes.
     * Verifica se retorna null quando IDs de estações são inválidos.
     *
     * @throws IOException se houver erro na criação do ficheiro temporário
     */
    @Test
    void testCalculateBestPathInvalidStations() throws IOException {
        Path stationsFile = tempDir.resolve("stations.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(stationsFile.toFile()))) {
            writer.write("idEstacao,nome,latitude,longitude\n");
            writer.write("1,Estação A,0,0\n");
        }

        TestableStationLoaderService testService = new TestableStationLoaderService();
        testService.loadStationsPublic(stationsFile.toString());

        assertNull(testService.calculateBestPath(1, 999));
        assertNull(testService.calculateBestPath(999, 1));
        assertNull(testService.calculateBestPath(null, 1));
    }

    /**
     * Testa a formatação do resultado de caminho.
     * Verifica se a string gerada contém nomes de estações e custos.
     *
     * @throws IOException se houver erro na criação dos ficheiros temporários
     */
    @Test
    void testPathResultFormatting() throws IOException {
        Path stationsFile = tempDir.resolve("stations.csv");
        Path linesFile = tempDir.resolve("lines.csv");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(stationsFile.toFile()))) {
            writer.write("idEstacao,nome,latitude,longitude\n");
            writer.write("1,Porto,0,0\n");
            writer.write("2,Lisboa,0,0\n");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(linesFile.toFile()))) {
            writer.write("id1,id2,col3,col4,custo\n");
            writer.write("1,2,x,y,15.5\n");
        }

        TestableStationLoaderService testService = new TestableStationLoaderService();
        testService.loadStationsPublic(stationsFile.toString());
        testService.loadLinesPublic(linesFile.toString());

        StationLoaderService.PathResultWithAlgorithm result = testService.calculateBestPath(1, 2);
        String formatted = result.getPathResult().formatPath();

        assertTrue(formatted.contains("Porto"));
        assertTrue(formatted.contains("Lisboa"));
        assertTrue(formatted.contains("15.5") || formatted.contains("15,5"));
        assertTrue(formatted.contains("Custo total"));
    }

    /**
     * Testa o acesso ao grafo do serviço.
     * Verifica se o grafo retornado é válido e contém dados corretos.
     *
     * @throws IOException se houver erro na criação do ficheiro temporário
     */
    @Test
    void testGetGraph() throws IOException {
        Path stationsFile = tempDir.resolve("stations.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(stationsFile.toFile()))) {
            writer.write("idEstacao,nome,latitude,longitude\n");
            writer.write("1,Estação A,0,0\n");
            writer.write("2,Estação B,0,0\n");
        }

        TestableStationLoaderService testService = new TestableStationLoaderService();
        testService.loadStationsPublic(stationsFile.toString());

        Graph<Station, Double> graph = testService.getGraph();
        assertNotNull(graph);
        assertEquals(2, graph.numVertices());
    }

    /**
     * Testa o acesso ao mapa de estações.
     * Verifica se o mapa retornado contém as estações corretas indexadas por ID.
     *
     * @throws IOException se houver erro na criação do ficheiro temporário
     */
    @Test
    void testGetStationMap() throws IOException {
        Path stationsFile = tempDir.resolve("stations.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(stationsFile.toFile()))) {
            writer.write("idEstacao,nome,latitude,longitude\n");
            writer.write("10,Estação X,0,0\n");
            writer.write("20,Estação Y,0,0\n");
        }

        TestableStationLoaderService testService = new TestableStationLoaderService();
        testService.loadStationsPublic(stationsFile.toString());

        Map<Integer, Station> map = testService.getStationMap();
        assertNotNull(map);
        assertEquals(2, map.size());
        assertTrue(map.containsKey(10));
        assertTrue(map.containsKey(20));
        assertEquals("Estação X", map.get(10).getName());
    }

    /**
     * Teste de fluxo completo integrando todas as funcionalidades.
     *
     * Simula cenário real:
     * 1. Cria rede ferroviária com múltiplas estações
     * 2. Define conexões com diferentes custos
     * 3. Calcula múltiplos caminhos
     * 4. Valida algoritmos utilizados
     * 5. Verifica formatação de resultados
     *
     * @throws IOException se houver erro na criação dos ficheiros temporários
     */
    @Test
    void testCompleteFlowIntegration() throws IOException {
        Path stationsFile = tempDir.resolve("stations.csv");
        Path linesFile = tempDir.resolve("lines.csv");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(stationsFile.toFile()))) {
            writer.write("idEstacao,nome,latitude,longitude\n");
            writer.write("1,Porto Campanhã,41.1496,-8.6109\n");
            writer.write("2,Aveiro,40.6443,-8.6455\n");
            writer.write("3,Coimbra-B,40.2111,-8.4292\n");
            writer.write("4,Lisboa Santa Apolónia,38.7169,-9.1252\n");
            writer.write("5,Faro,37.0194,-7.9304\n");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(linesFile.toFile()))) {
            writer.write("id1,id2,col3,col4,custo\n");
            writer.write("1,2,x,y,8.5\n");
            writer.write("2,3,x,y,6.2\n");
            writer.write("3,4,x,y,12.3\n");
            writer.write("4,5,x,y,18.7\n");
            writer.write("1,3,x,y,20.0\n");
            writer.write("2,4,x,y,25.0\n");
        }

        TestableStationLoaderService testService = new TestableStationLoaderService();
        testService.loadStationsPublic(stationsFile.toString());
        testService.loadLinesPublic(linesFile.toString());

        assertEquals(5, testService.getTotalStations(), "Deve ter 5 estações");
        assertEquals(12, testService.getTotalConnections(), "Deve ter 12 conexões (6 linhas × 2)");

        assertTrue(testService.stationExists(1), "Porto deve existir");
        assertTrue(testService.stationExists(5), "Faro deve existir");
        assertFalse(testService.stationExists(99), "Estação 99 não deve existir");

        List<String> stationList = testService.getFormattedStationList();
        assertEquals(5, stationList.size(), "Lista deve ter 5 estações");
        assertTrue(stationList.get(0).contains("Porto Campanhã"));

        StationLoaderService.PathResultWithAlgorithm shortPath = testService.calculateBestPath(1, 3);
        assertNotNull(shortPath, "Deve encontrar caminho Porto-Coimbra");
        assertEquals("Dijkstra", shortPath.getAlgorithmUsed());
        assertFalse(shortPath.hasNegativeCycle());
        assertEquals(14.7, shortPath.getPathResult().totalCost, 0.01);

        StationLoaderService.PathResultWithAlgorithm longPath = testService.calculateBestPath(1, 5);
        assertNotNull(longPath, "Deve encontrar caminho Porto-Faro");
        assertEquals("Dijkstra", longPath.getAlgorithmUsed());
        assertEquals(45.7, longPath.getPathResult().totalCost, 0.01);
        assertEquals(5, longPath.getPathResult().path.size());

        String formatted = longPath.getPathResult().formatPath();
        assertTrue(formatted.contains("Porto Campanhã"));
        assertTrue(formatted.contains("Faro"));
        assertTrue(formatted.contains("Custo total"));
        assertTrue(formatted.contains("45.7") || formatted.contains("45,7"));

        assertNull(testService.calculateBestPath(1, 999));

        Graph<Station, Double> graph = testService.getGraph();
        assertNotNull(graph);
        assertEquals(5, graph.numVertices());
        assertEquals(12, graph.numEdges()); // 6 linhas × 2 direções

        Map<Integer, Station> stationMap = testService.getStationMap();
        assertEquals("Lisboa Santa Apolónia", stationMap.get(4).getName());

        StationLoaderService.PathResultWithAlgorithm midPath = testService.calculateBestPath(2, 4);
        assertNotNull(midPath);
        assertTrue(midPath.getPathResult().totalCost < 25.0);

        System.out.println("✓ Teste de fluxo completo executado com sucesso!");
        System.out.println("  - Estações: " + testService.getTotalStations());
        System.out.println("  - Conexões: " + testService.getTotalConnections());
        System.out.println("  - Caminhos testados: 4");
        System.out.println("  - Algoritmo: Dijkstra");
    }

    /**
     * Classe auxiliar que expõe métodos privados para testes.
     * Permite testar métodos de carregamento sem depender de ficheiros fixos.
     */
    private static class TestableStationLoaderService extends StationLoaderService {
        public void loadStationsPublic(String filePath) throws IOException {
            super.loadStations(filePath);
        }

        public void loadLinesPublic(String filePath) throws IOException {
            super.loadLines(filePath);
        }
    }
}
