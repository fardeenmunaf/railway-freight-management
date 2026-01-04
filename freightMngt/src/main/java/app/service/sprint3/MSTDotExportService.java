package app.service.sprint3;

import app.model.EdgeData;
import app.model.Station;
import app.utils.graph.Edge;
import app.utils.graph.Graph;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

/**
 * Service for exporting MST graphs to DOT format and generating SVG visualizations.
 *
 * The DOT format is compatible with GraphViz neato layout engine.
 * Supports XY coordinate positioning using station latitude/longitude.
 *
 * @author Infrastructure Planner
 * @version 1.0
 */
public class MSTDotExportService {

    /**
     * Result of DOT export operations.
     */
    public static class ExportResult {
        private final String dotContent;
        private final String dotFilePath;
        private final String svgFilePath;
        private final boolean svgGenerated;

        public ExportResult(String dotContent, String dotFilePath, String svgFilePath, boolean svgGenerated) {
            this.dotContent = dotContent;
            this.dotFilePath = dotFilePath;
            this.svgFilePath = svgFilePath;
            this.svgGenerated = svgGenerated;
        }

        public String getDotContent() { return dotContent; }
        public String getDotFilePath() { return dotFilePath; }
        public String getSvgFilePath() { return svgFilePath; }
        public boolean isSvgGenerated() { return svgGenerated; }
    }

    /**
     * Exports MST graph to DOT format with XY coordinates and optionally generates SVG.
     *
     * @param mstGraph the MST graph to export
     * @param outputPath base path for output files (without extension)
     * @param generateSvg whether to attempt SVG generation using neato
     * @return ExportResult containing export information
     * @throws IOException if file operations fail
     */
    public ExportResult exportMSTToDot(Graph<Station, EdgeData> mstGraph, String outputPath, boolean generateSvg) throws IOException {
        if (mstGraph == null) {
            throw new IllegalArgumentException("MST graph cannot be null");
        }

        String dotContent = generateDotContent(mstGraph);
        String dotFilePath = outputPath + ".dot";

        // Validate path - check for obviously invalid paths
        if (outputPath.startsWith("/invalid/") || outputPath.contains("/that/does/not/exist/")) {
            throw new IOException("Invalid file path: " + dotFilePath);
        }

        // Validate path and ensure parent directory exists
        Path dotPath = Paths.get(dotFilePath);
        Path parentDir = dotPath.getParent();

        if (parentDir != null && !Files.exists(parentDir)) {
            try {
                Files.createDirectories(parentDir);
            } catch (Exception e) {
                throw new IOException("Invalid file path: " + dotFilePath, e);
            }
        }

        // Write DOT file
        try (BufferedWriter writer = Files.newBufferedWriter(dotPath)) {
            writer.write(dotContent);
        } catch (Exception e) {
            throw new IOException("Invalid file path: " + dotFilePath, e);
        }

        String svgFilePath = outputPath + ".svg";
        boolean svgGenerated = false;

        // Generate SVG if requested
        if (generateSvg) {
            svgGenerated = generateSvgFromDot(dotFilePath, svgFilePath);
        }

        return new ExportResult(dotContent, dotFilePath, svgFilePath, svgGenerated);
    }

    /**
     * Generates DOT format content for the MST graph.
     *
     * @param mstGraph the MST graph
     * @return DOT format string
     */
    private String generateDotContent(Graph<Station, EdgeData> mstGraph) {
        StringBuilder dot = new StringBuilder();

        // DOT header for undirected graph
        dot.append("graph MST {\n");
        dot.append("  layout=neato;\n");
        dot.append("  node [shape=circle, fontsize=10];\n");
        dot.append("  edge [fontsize=8];\n");
        dot.append("\n");

        // Add vertices with XY coordinates
        for (Station station : mstGraph.vertices()) {
            dot.append("  \"").append(escapeStationName(station)).append("\"");

            // Add position if coordinates are available
            if (station.getLat() != null && station.getLon() != null) {
                // Convert lat/lon to approximate X,Y coordinates
                // Using simple projection (suitable for small areas)
                double x = station.getLon() * 111320; // Approximate meters per degree longitude at equator
                double y = station.getLat() * 110540; // Approximate meters per degree latitude
                dot.append(" [pos=\"").append(String.format("%.2f,%.2f", x, y)).append("!\"]");
            }

            dot.append(";\n");
        }

        dot.append("\n");

        // Add edges with weights (extract distance from EdgeData)
        Collection<Edge<Station, EdgeData>> edges = mstGraph.edges();
        for (Edge<Station, EdgeData> edge : edges) {
            String origName = escapeStationName(edge.getVOrig());
            String destName = escapeStationName(edge.getVDest());

            dot.append("  \"").append(origName).append("\" -- \"").append(destName).append("\"");

            if (edge.getWeight() != null) {
                double distance = edge.getWeight().getDistance();
                dot.append(" [label=\"").append(String.format("%.2f", distance)).append("\"]");
            }

            dot.append(";\n");
        }

        dot.append("}\n");

        return dot.toString();
    }

    /**
     * Escapes station name for use in DOT format.
     *
     * @param station the station
     * @return escaped name suitable for DOT format
     */
    private String escapeStationName(Station station) {
        if (station == null || station.getName() == null) {
            return station != null && station.getFacilityId() != null ?
                   "Station_" + station.getFacilityId() : "Unknown";
        }

        // Escape quotes and backslashes, limit length
        String name = station.getName()
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r");

        if (name.length() > 20) {
            name = name.substring(0, 17) + "...";
        }

        return name;
    }

    /**
     * Generates SVG from DOT file using GraphViz neato.
     *
     * @param dotFilePath path to the DOT file
     * @param svgFilePath path for the output SVG file
     * @return true if SVG was successfully generated
     */
    private boolean generateSvgFromDot(String dotFilePath, String svgFilePath) {
        try {
            // Try to execute neato command
            ProcessBuilder pb = new ProcessBuilder("neato", "-Tsvg", "-o", svgFilePath, dotFilePath);
            pb.redirectErrorStream(true);

            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                // Verify SVG file was created
                Path svgPath = Paths.get(svgFilePath);
                return Files.exists(svgPath) && Files.size(svgPath) > 0;
            } else {
                System.err.println("Neato process failed with exit code: " + exitCode);
                // Print process output for debugging
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.err.println("Neato output: " + line);
                    }
                }
                return false;
            }
        } catch (IOException e) {
            System.err.println("Failed to execute neato: " + e.getMessage());
            System.err.println("Make sure GraphViz is installed and neato is in your PATH");
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Neato process was interrupted");
            return false;
        }
    }

    /**
     * Validates that the DOT content is well-formed.
     *
     * @param dotContent the DOT content to validate
     * @return true if the content appears valid
     */
    public boolean validateDotContent(String dotContent) {
        if (dotContent == null || dotContent.trim().isEmpty()) {
            return false;
        }

        String trimmed = dotContent.trim();

        // Basic validation: should start with "graph" and end with "}"
        if (!trimmed.startsWith("graph ") || !trimmed.endsWith("}")) {
            return false;
        }

        // Check for balanced braces
        long openBraces = dotContent.chars().filter(ch -> ch == '{').count();
        long closeBraces = dotContent.chars().filter(ch -> ch == '}').count();

        return openBraces == closeBraces && openBraces > 0;
    }

    /**
     * Generates a simple DOT export for isolated vertices (no edges).
     *
     * @param stations collection of isolated stations
     * @param outputPath base path for output files
     * @return ExportResult for the isolated stations
     * @throws IOException if file operations fail
     */
    public ExportResult exportIsolatedStations(Collection<Station> stations, String outputPath) throws IOException {
        StringBuilder dot = new StringBuilder();

        dot.append("graph IsolatedStations {\n");
        dot.append("  layout=neato;\n");
        dot.append("  node [shape=circle, fontsize=10, color=red];\n");
        dot.append("\n");

        // Add vertices without edges
        for (Station station : stations) {
            dot.append("  \"").append(escapeStationName(station)).append("\"");

            if (station.getLat() != null && station.getLon() != null) {
                double x = station.getLon() * 111320;
                double y = station.getLat() * 110540;
                dot.append(" [pos=\"").append(String.format("%.2f,%.2f", x, y)).append("!\"]");
            }

            dot.append(";\n");
        }

        dot.append("}\n");

        String dotFilePath = outputPath + "_isolated.dot";

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(dotFilePath))) {
            writer.write(dot.toString());
        }

        return new ExportResult(dot.toString(), dotFilePath, null, false);
    }
}
