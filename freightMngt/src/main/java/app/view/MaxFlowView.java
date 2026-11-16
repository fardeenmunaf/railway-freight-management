package app.view;

import app.utils.graph.MaxFlow;
import app.model.Station;
import app.service.sprint3.MaxFlowService;
import app.repositories.Repositories;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * Console view for Maximum Throughput (MaxFlow).
 * Prints stations loaded in the project's graph (so IDs match the ones the user must input),
 * prompts for source and sink station IDs and displays the result.
 */
public class MaxFlowView {

    /**
     * Output modes for result formatting. DETAILED prints a multi-line verbose view; COMPACT prints a single-line summary.
     */
    public enum Mode { DETAILED, COMPACT }

    /**
     * Format a MaxFlow.Result into a human-readable string.
     * The method prefers Station.facilityId for displayed keys when available.
     *
     * @param res the result from MaxFlow computation (may be null)
     * @param mode formatting mode (DETAILED or COMPACT)
     * @param <V> vertex type
     * @param <E> capacity type
     * @return formatted string ready for printing
     */
    public static <V, E> String formatResult(MaxFlow.Result<V, E> res, Mode mode) {
        if (mode == null) mode = Mode.DETAILED;
        if (res == null) return mode == Mode.DETAILED ? "MaxFlow Result:\n  Result is null\n" : "MaxFlow: null";

        // Determine display keys preferring Station.facilityId when available
        String srcKeyStr = "null", sinkKeyStr = "null";
        try {
            if (res.source instanceof Station && ((Station) res.source).getFacilityId() != null) srcKeyStr = String.valueOf(((Station) res.source).getFacilityId());
            else if (res.sourceKey != null) srcKeyStr = res.sourceKey.toString();
        } catch (Exception ignored) {}
        try {
            if (res.sink instanceof Station && ((Station) res.sink).getFacilityId() != null) sinkKeyStr = String.valueOf(((Station) res.sink).getFacilityId());
            else if (res.sinkKey != null) sinkKeyStr = res.sinkKey.toString();
        } catch (Exception ignored) {}

        if (mode == Mode.COMPACT) {
            String maxFlow = res.maxFlow == null ? "null" : res.maxFlow.toString();
            String msg = (res.message == null || res.message.isEmpty()) ? "" : " (" + res.message + ")";
            return String.format("MaxFlow: %s -> %s | flow=%s%s", srcKeyStr, sinkKeyStr, maxFlow, msg);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("MaxFlow Result:\n");
        sb.append("  Source key: ").append(srcKeyStr).append("\n");
        sb.append("  Sink key:   ").append(sinkKeyStr).append("\n");
        sb.append("  Source:     ").append(describe(res.source)).append("\n");
        sb.append("  Sink:       ").append(describe(res.sink)).append("\n");
        sb.append("  Max flow:   ").append(res.maxFlow == null ? "null" : res.maxFlow.toString()).append("\n");
        sb.append("  Message:    ").append(res.message == null ? "" : res.message).append("\n");
        sb.append("  Complexity: ").append(res.complexity == null ? "" : res.complexity).append("\n");
        return sb.toString();
    }

    /**
     * Print a formatted MaxFlow.Result to standard output.
     *
     * @param res computation result
     * @param mode formatting mode
     * @param <V> vertex type
     * @param <E> capacity type
     */
    /** Short, readable description of a vertex: "NAME (id)" for stations, toString() otherwise. */
    private static String describe(Object vertex) {
        if (vertex == null) return "null";
        if (vertex instanceof Station s) {
            return s.getName() + " (" + s.getFacilityId() + ")";
        }
        return vertex.toString();
    }

    public static <V, E> void printResult(MaxFlow.Result<V, E> res, Mode mode) {
        System.out.println(formatResult(res, mode));
    }

    /**
     * Interactive console display that lists loaded stations, prompts the user for source and sink station IDs
     * and computes/prints the maximum flow using the project's graph.
     *
     * @param scanner Scanner instance to read user input
     * @throws IOException if an I/O error occurs while interacting with the console
     */
    public static void display(Scanner scanner) throws IOException {
        System.out.println("=== Maximum Throughput (MaxFlow) ===");

        List<Station> stations = MaxFlowService.getProjectGraphStations();
        if (stations == null) {
            System.err.println("Project graph not available. MaxFlow requires the project's graph (StationLoaderService.getGraph()). Ensure the application initialized the graph.");
            System.out.println("Press Enter to return to the menu...");
            scanner.nextLine();
            return;
        }

        if (stations.isEmpty()) {
            System.err.println("Project graph has no stations loaded. Ensure StationLoaderService populated the graph.");
            System.out.println("Press Enter to return to the menu...");
            scanner.nextLine();
            return;
        }

        // sort by facilityId and print (these are the IDs the user must input)
        stations.sort(Comparator.comparing(Station::getFacilityId));
        System.out.println("Loaded stations (ID : Name) - showing up to 50 entries:");
        stations.stream().limit(50).forEach(s -> System.out.printf("%d : %s%n", s.getFacilityId(), s.getName()));

        Integer sourceId = null, sinkId = null;
        try {
            System.out.print("Enter source station ID: ");
            sourceId = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Enter sink station ID: ");
            sinkId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.err.println("Invalid number. Returning to menu.");
            System.out.println("Press Enter to return to the menu...");
            scanner.nextLine();
            return;
        }

        System.out.println("Computing max flow between " + sourceId + " and " + sinkId + " using the project's graph...");
        MaxFlow.Result<?,?> res = MaxFlowService.computeUsingProjectGraphIfAvailable(sourceId, sinkId);

        if (res == null) {
            System.err.println("Computation returned null result.");
        } else {
            System.out.println(formatResult(res, Mode.DETAILED));
            // if no topology path message suggests reachable stations, MaxFlowService may provide helper methods
            if (res.message != null && res.message.toLowerCase().contains("no augmenting path")) {
            }
        }

        System.out.println("Press Enter to return to the menu...");
        scanner.nextLine();
    }
}
