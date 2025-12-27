package app.controller;

import app.model.Station;
import app.service.sprint3.MinimumSpanningTreeService;
import app.service.sprint3.MSTDotExportService;
import app.utils.graph.Graph;

import java.io.IOException;
import java.util.List;

/**
 * Controller for MST (Minimum Spanning Tree) operations on station networks.
 *
 * Provides endpoints for computing MST, exporting to DOT format, and generating SVG visualizations.
 * Handles infrastructure planning scenarios where minimal track backbone is needed.
 *
 * @author Infrastructure Planner
 * @version 1.0
 */
public class MSTController {

    private final MinimumSpanningTreeService mstService;
    private final MSTDotExportService dotExportService;

    /**
     * Constructor with dependency injection.
     */
    public MSTController() {
        this.mstService = new MinimumSpanningTreeService();
        this.dotExportService = new MSTDotExportService();
    }

    /**
     * Constructor for testing with custom services.
     */
    public MSTController(MinimumSpanningTreeService mstService, MSTDotExportService dotExportService) {
        this.mstService = mstService;
        this.dotExportService = dotExportService;
    }

    /**
     * Computes MST for the given station network.
     *
     * @param stationGraph the input undirected graph
     * @return MST computation result
     */
    public MinimumSpanningTreeService.MSTResult computeMinimumSpanningTree(Graph<Station, Double> stationGraph) {
        if (stationGraph == null) {
            throw new IllegalArgumentException("Station graph cannot be null");
        }

        return mstService.computeMST(stationGraph);
    }

    /**
     * Exports MST to DOT format and optionally generates SVG.
     *
     * @param mstResult the MST computation result
     * @param outputPath base path for output files
     * @param generateSvg whether to generate SVG visualization
     * @return export result with file paths and success status
     * @throws IOException if file operations fail
     */
    public MSTDotExportService.ExportResult exportMSTVisualization(
            MinimumSpanningTreeService.MSTResult mstResult,
            String outputPath,
            boolean generateSvg) throws IOException {

        if (mstResult == null) {
            throw new IllegalArgumentException("MST result cannot be null");
        }

        return dotExportService.exportMSTToDot(mstResult.getMstGraph(), outputPath, generateSvg);
    }

    /**
     * Complete workflow: compute MST and export visualization.
     *
     * @param stationGraph input station network
     * @param outputPath base path for output files
     * @param generateSvg whether to generate SVG
     * @return combined result with MST data and export information
     * @throws IOException if export operations fail
     */
    public WorkflowResult computeAndExportMST(Graph<Station, Double> stationGraph,
                                            String outputPath,
                                            boolean generateSvg) throws IOException {

        // Compute MST
        MinimumSpanningTreeService.MSTResult mstResult = computeMinimumSpanningTree(stationGraph);

        // Export visualization
        MSTDotExportService.ExportResult exportResult = exportMSTVisualization(mstResult, outputPath, generateSvg);

        return new WorkflowResult(mstResult, exportResult);
    }

    /**
     * Combined result of MST computation and export operations.
     */
    public static class WorkflowResult {
        private final MinimumSpanningTreeService.MSTResult mstResult;
        private final MSTDotExportService.ExportResult exportResult;

        public WorkflowResult(MinimumSpanningTreeService.MSTResult mstResult,
                            MSTDotExportService.ExportResult exportResult) {
            this.mstResult = mstResult;
            this.exportResult = exportResult;
        }

        public MinimumSpanningTreeService.MSTResult getMstResult() { return mstResult; }
        public MSTDotExportService.ExportResult getExportResult() { return exportResult; }

        /**
         * Gets a summary report of the MST computation and export.
         */
        public String getSummaryReport() {
            StringBuilder report = new StringBuilder();
            report.append("=== MST Computation Summary ===\n");
            report.append("Total Distance: ").append(String.format("%.2f", mstResult.getTotalDistance())).append("\n");
            report.append("Connected Components: ").append(mstResult.getConnectedComponents().size()).append("\n");
            report.append("Time Complexity: ").append(mstResult.getTimeComplexity()).append("\n");
            report.append("Vertices in MST: ").append(mstResult.getMstGraph().numVertices()).append("\n");
            report.append("Edges in MST: ").append(mstResult.getMstGraph().numEdges()).append("\n");

            report.append("\n=== Export Summary ===\n");
            report.append("DOT file: ").append(exportResult.getDotFilePath()).append("\n");

            if (exportResult.isSvgGenerated()) {
                report.append("SVG file: ").append(exportResult.getSvgFilePath()).append("\n");
            } else {
                report.append("SVG generation: Failed or not requested\n");
            }

            // Component details
            if (mstResult.getConnectedComponents().size() > 1) {
                report.append("\n=== Connected Components ===\n");
                for (int i = 0; i < mstResult.getConnectedComponents().size(); i++) {
                    List<Station> component = mstResult.getConnectedComponents().get(i);
                    report.append("Component ").append(i + 1).append(" (").append(component.size()).append(" stations): ");

                    if (component.size() <= 5) {
                        for (int j = 0; j < component.size(); j++) {
                            if (j > 0) report.append(", ");
                            Station station = component.get(j);
                            report.append(station.getName() != null ? station.getName() : "Station_" + station.getFacilityId());
                        }
                    } else {
                        // Show first 3 and last 1 for large components
                        for (int j = 0; j < 3; j++) {
                            if (j > 0) report.append(", ");
                            Station station = component.get(j);
                            report.append(station.getName() != null ? station.getName() : "Station_" + station.getFacilityId());
                        }
                        report.append(", ... (").append(component.size() - 4).append(" more), ");
                        Station lastStation = component.get(component.size() - 1);
                        report.append(lastStation.getName() != null ? lastStation.getName() : "Station_" + lastStation.getFacilityId());
                    }
                    report.append("\n");
                }
            }

            return report.toString();
        }
    }
}
