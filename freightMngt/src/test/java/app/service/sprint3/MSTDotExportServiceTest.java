package app.service.sprint3;

import app.model.Station;
import app.utils.graph.Graph;
import app.utils.graph.map.MapGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for MSTDotExportService.
 *
 * Tests cover:
 * - DOT format generation with XY coordinates
 * - GraphViz compatibility
 * - SVG generation
 * - Edge cases and error handling
 *
 * @author Test Infrastructure
 */
class MSTDotExportServiceTest {

    private MSTDotExportService exportService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        exportService = new MSTDotExportService();
    }

    @Nested
    @DisplayName("DOT Format Generation Tests")
    class DotFormatTests {

        @Test
        @DisplayName("Should generate valid DOT format for MST with XY coordinates")
        void testDotGenerationWithCoordinates() throws IOException {
            // Arrange
            Graph<Station, Double> mst = createTestMSTWithCoordinates();
            String outputPath = tempDir.resolve("test_mst").toString();

            // Act
            MSTDotExportService.ExportResult result = exportService.exportMSTToDot(mst, outputPath, false);

            // Assert
            assertNotNull(result);
            assertNotNull(result.getDotContent());
            assertTrue(result.getDotContent().contains("graph MST"));
            assertTrue(result.getDotContent().contains("layout=neato"));

            // Verify XY coordinates are included
            assertTrue(result.getDotContent().contains("pos="));

            // Verify file was created
            Path dotFile = Path.of(result.getDotFilePath());
            assertTrue(Files.exists(dotFile));

            String fileContent = Files.readString(dotFile);
            assertEquals(result.getDotContent(), fileContent);

            // Validate DOT structure
            assertTrue(exportService.validateDotContent(result.getDotContent()));
        }

        @Test
        @DisplayName("Should handle stations without coordinates")
        void testDotGenerationWithoutCoordinates() throws IOException {
            // Arrange
            Graph<Station, Double> mst = createTestMSTWithoutCoordinates();
            String outputPath = tempDir.resolve("test_mst_no_coords").toString();

            // Act
            MSTDotExportService.ExportResult result = exportService.exportMSTToDot(mst, outputPath, false);

            // Assert
            assertNotNull(result);
            assertNotNull(result.getDotContent());
            assertTrue(result.getDotContent().contains("graph MST"));

            // Should not contain pos attributes
            assertFalse(result.getDotContent().contains("pos="));

            assertTrue(exportService.validateDotContent(result.getDotContent()));
        }

        @Test
        @DisplayName("Should generate DOT for empty MST")
        void testEmptyMSTDot() throws IOException {
            // Arrange
            Graph<Station, Double> mst = new MapGraph<>(false);
            String outputPath = tempDir.resolve("empty_mst").toString();

            // Act
            MSTDotExportService.ExportResult result = exportService.exportMSTToDot(mst, outputPath, false);

            // Assert
            assertNotNull(result);
            assertTrue(result.getDotContent().contains("graph MST"));
            assertFalse(result.getDotContent().contains("--")); // No edges
            assertTrue(exportService.validateDotContent(result.getDotContent()));
        }
    }

    @Nested
    @DisplayName("DOT Validation Tests")
    class DotValidationTests {

        @Test
        @DisplayName("Should validate correct DOT content")
        void testValidDotContent() {
            String validDot = "graph MST {\n  layout=neato;\n  \"A\" -- \"B\";\n}";
            assertTrue(exportService.validateDotContent(validDot));
        }

        @Test
        @DisplayName("Should reject invalid DOT content")
        void testInvalidDotContent() {
            // Test various invalid formats
            assertFalse(exportService.validateDotContent(null));
            assertFalse(exportService.validateDotContent(""));
            assertFalse(exportService.validateDotContent("   "));
            assertFalse(exportService.validateDotContent("not a graph"));
            assertFalse(exportService.validateDotContent("graph MST { missing close brace"));
            assertFalse(exportService.validateDotContent("digraph MST { }")); // Wrong keyword
        }

        @Test
        @DisplayName("Should handle unbalanced braces")
        void testUnbalancedBraces() {
            String unbalancedDot1 = "graph MST { { nested but not closed";
            String unbalancedDot2 = "graph MST { } extra }";

            assertFalse(exportService.validateDotContent(unbalancedDot1));
            assertFalse(exportService.validateDotContent(unbalancedDot2));
        }
    }

    @Nested
    @DisplayName("Isolated Stations Export Tests")
    class IsolatedStationsTests {

        @Test
        @DisplayName("Should export isolated stations with special formatting")
        void testIsolatedStationsExport() throws IOException {
            // Arrange
            List<Station> isolatedStations = Arrays.asList(
                createStation(1, "Isolated1", 10.0, 20.0),
                createStation(2, "Isolated2", 30.0, 40.0),
                createStation(3, "Isolated3", null, null) // No coordinates
            );

            String outputPath = tempDir.resolve("isolated").toString();

            // Act
            MSTDotExportService.ExportResult result = exportService.exportIsolatedStations(isolatedStations, outputPath);

            // Assert
            assertNotNull(result);
            assertTrue(result.getDotFilePath().endsWith("_isolated.dot"));
            assertNull(result.getSvgFilePath()); // SVG not generated for isolated stations
            assertFalse(result.isSvgGenerated());

            String dotContent = result.getDotContent();
            assertTrue(dotContent.contains("graph IsolatedStations"));
            assertTrue(dotContent.contains("color=red")); // Special formatting for isolated stations
            assertTrue(dotContent.contains("\"Isolated1\""));
            assertTrue(dotContent.contains("\"Isolated2\""));
            assertTrue(dotContent.contains("\"Isolated3\""));

            // Should have coordinates for stations that have them
            assertTrue(dotContent.contains("pos=")); // At least some positions

            // File should be created
            Path isolatedFile = Path.of(result.getDotFilePath());
            assertTrue(Files.exists(isolatedFile));
        }

        @Test
        @DisplayName("Should handle empty isolated stations list")
        void testEmptyIsolatedStations() throws IOException {
            // Arrange
            List<Station> emptyList = Arrays.asList();
            String outputPath = tempDir.resolve("empty_isolated").toString();

            // Act
            MSTDotExportService.ExportResult result = exportService.exportIsolatedStations(emptyList, outputPath);

            // Assert
            assertNotNull(result);
            String dotContent = result.getDotContent();
            assertTrue(dotContent.contains("graph IsolatedStations"));
            assertFalse(dotContent.contains("--")); // No edges
            // Should only contain the header and closing brace
            long nodeCount = dotContent.lines().filter(line -> line.trim().contains("\"")).count();
            assertEquals(0, nodeCount);
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should reject null MST graph")
        void testNullMSTGraph() {
            String outputPath = tempDir.resolve("null_test").toString();

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> exportService.exportMSTToDot(null, outputPath, false)
            );

            assertEquals("MST graph cannot be null", exception.getMessage());
        }

        @Test
        @DisplayName("Should handle invalid file paths gracefully")
        void testInvalidFilePath() {
            // Arrange
            Graph<Station, Double> mst = new MapGraph<>(false);
            String invalidPath = "/invalid/path/that/does/not/exist/test";

            // Act & Assert - should throw IOException
            assertThrows(IOException.class, () -> {
                exportService.exportMSTToDot(mst, invalidPath, false);
            });
        }

        @Test
        @DisplayName("Should handle stations with null names")
        void testStationsWithNullNames() throws IOException {
            // Arrange
            Graph<Station, Double> mst = new MapGraph<>(false);
            Station s1 = createStation(1, null, 1.0, 1.0); // null name
            Station s2 = new Station(); // all null
            s2.setFacilityId(2);

            mst.addVertex(s1);
            mst.addVertex(s2);
            mst.addEdge(s1, s2, 5.0);

            String outputPath = tempDir.resolve("null_names").toString();

            // Act
            MSTDotExportService.ExportResult result = exportService.exportMSTToDot(mst, outputPath, false);

            // Assert
            assertNotNull(result);
            String dotContent = result.getDotContent();

            // Should use fallback names
            assertTrue(dotContent.contains("Station_1"));
            assertTrue(dotContent.contains("Station_2"));
            assertTrue(exportService.validateDotContent(dotContent));
        }
    }

    @Nested
    @DisplayName("SVG Generation Tests")
    class SvgGenerationTests {

        @Test
        @DisplayName("Should attempt SVG generation when requested")
        void testSvgGenerationAttempt() throws IOException {
            // Arrange
            Graph<Station, Double> mst = createTestMSTWithCoordinates();
            String outputPath = tempDir.resolve("svg_test").toString();

            // Act - request SVG generation (may fail if neato not installed)
            MSTDotExportService.ExportResult result = exportService.exportMSTToDot(mst, outputPath, true);

            // Assert
            assertNotNull(result);
            assertNotNull(result.getSvgFilePath());
            assertTrue(result.getSvgFilePath().endsWith(".svg"));

            // SVG generation result depends on whether GraphViz/neato is installed
            // We just verify that the attempt was made and recorded properly
        }

        @Test
        @DisplayName("Should not generate SVG when not requested")
        void testNoSvgGeneration() throws IOException {
            // Arrange
            Graph<Station, Double> mst = createTestMSTWithCoordinates();
            String outputPath = tempDir.resolve("no_svg_test").toString();

            // Act
            MSTDotExportService.ExportResult result = exportService.exportMSTToDot(mst, outputPath, false);

            // Assert
            assertNotNull(result);
            assertFalse(result.isSvgGenerated());
        }
    }

    /**
     * Helper method to create test MST with coordinates.
     */
    private Graph<Station, Double> createTestMSTWithCoordinates() {
        Graph<Station, Double> mst = new MapGraph<>(false);

        Station s1 = createStation(1, "StationA", 38.7169, -9.1395); // Lisbon coordinates
        Station s2 = createStation(2, "StationB", 41.1579, -8.6291); // Porto coordinates
        Station s3 = createStation(3, "StationC", 40.2033, -8.4103); // Coimbra coordinates

        mst.addVertex(s1);
        mst.addVertex(s2);
        mst.addVertex(s3);

        mst.addEdge(s1, s2, 275.0); // Approximate distance Lisbon-Porto
        mst.addEdge(s2, s3, 120.0); // Approximate distance Porto-Coimbra

        return mst;
    }

    /**
     * Helper method to create test MST without coordinates.
     */
    private Graph<Station, Double> createTestMSTWithoutCoordinates() {
        Graph<Station, Double> mst = new MapGraph<>(false);

        Station s1 = createStation(1, "StationX", null, null);
        Station s2 = createStation(2, "StationY", null, null);

        mst.addVertex(s1);
        mst.addVertex(s2);
        mst.addEdge(s1, s2, 100.0);

        return mst;
    }

    /**
     * Helper method to create test stations.
     */
    private Station createStation(int id, String name, Double lat, Double lon) {
        Station station = new Station();
        station.setFacilityId(id);
        station.setName(name);
        station.setLat(lat);
        station.setLon(lon);
        return station;
    }
}
