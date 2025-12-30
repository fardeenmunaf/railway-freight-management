package app.view;

import app.utils.graph.MaxFlow;
import java.io.IOException;
import java.util.Scanner;

import app.repositories.Repositories;
import app.model.Station;
import app.service.sprint3.MaxFlowService;
import java.util.List;
import java.util.stream.Collectors;

/**
 * View única para formatar e imprimir resultados de MaxFlow.
 * Suporta dois modos: DETAILED (vários campos, legível) e COMPACT (uma linha resumo).
 * Inclui um método de consola mínimo `display(Scanner)` para substituir a vista removida.
 */
public class MaxFlowView {

    public enum Mode {
        DETAILED,
        COMPACT
    }

    /**
     * Formata o resultado no modo especificado.
     */
    public static <V, E> String formatResult(MaxFlow.Result<V, E> res, Mode mode) {
        if (mode == null) mode = Mode.DETAILED;

        if (res == null) {
            return mode == Mode.DETAILED
                    ? "MaxFlow Result:\n  Result is null\n"
                    : "MaxFlow: null";
        }

        if (mode == Mode.COMPACT) {
            String sourceKey = res.sourceKey == null ? "null" : res.sourceKey.toString();
            String sinkKey = res.sinkKey == null ? "null" : res.sinkKey.toString();
            String maxFlow = res.maxFlow == null ? "null" : res.maxFlow.toString();
            String msg = (res.message == null || res.message.isEmpty()) ? "" : " (" + res.message + ")";
            return String.format("MaxFlow: %s -> %s | flow=%s%s", sourceKey, sinkKey, maxFlow, msg);
        }

        // DETAILED
        StringBuilder sb = new StringBuilder();
        sb.append("MaxFlow Result:\n");
        sb.append("  Source key: ").append(res.sourceKey).append("\n");
        sb.append("  Sink key:   ").append(res.sinkKey).append("\n");
        sb.append("  Source:     ").append(res.source == null ? "null" : res.source.toString()).append("\n");
        sb.append("  Sink:       ").append(res.sink == null ? "null" : res.sink.toString()).append("\n");
        sb.append("  Max flow:   ").append(res.maxFlow == null ? "null" : res.maxFlow.toString()).append("\n");
        sb.append("  Message:    ").append(res.message == null ? "" : res.message).append("\n");
        sb.append("  Complexity: ").append(res.complexity == null ? "" : res.complexity).append("\n");
        return sb.toString();
    }

    /**
     * Imprime o resultado no modo especificado.
     */
    public static <V, E> void printResult(MaxFlow.Result<V, E> res, Mode mode) {
        System.out.println(formatResult(res, mode));
    }

    /**
     * Conveniência: imprime em modo detalhado.
     */
    public static <V, E> void printDetailed(MaxFlow.Result<V, E> res) {
        printResult(res, Mode.DETAILED);
    }

    /**
     * Conveniência: imprime em modo compacto.
     */
    public static <V, E> void printCompact(MaxFlow.Result<V, E> res) {
        printResult(res, Mode.COMPACT);
    }

    /**
     * Método mínimo de console para substituir a vista removida.
     * Agora: lista estações carregadas no Repositories, pede source/sink por ID e calcula max flow.
     */
    public static void display(Scanner scanner) throws IOException {
        Repositories repos = Repositories.getInstance();
        List<Station> stations = repos.getFacilityRepository().getAllFacilities().stream()
                .filter(f -> f instanceof Station)
                .map(f -> (Station) f)
                .collect(Collectors.toList());

        if (stations.isEmpty()) {
            System.err.println("⚠ No stations loaded in Repositories. Make sure AppInitializer.run() executed.");
            System.out.println("Pressione Enter para voltar ao menu...");
            scanner.nextLine();
            return;
        }

        System.out.println("=== Maximum Throughput (MaxFlow) ===");
        System.out.println("Loaded stations (ID : Name) - showing up to 50 entries:");
        stations.stream().limit(50).forEach(s -> System.out.printf("%d : %s%n", s.getFacilityId(), s.getName()));

        Integer sourceId = null;
        Integer sinkId = null;

        try {
            System.out.print("Enter source station ID: ");
            String sIn = scanner.nextLine().trim();
            sourceId = Integer.parseInt(sIn);

            System.out.print("Enter sink station ID: ");
            String tIn = scanner.nextLine().trim();
            sinkId = Integer.parseInt(tIn);
        } catch (NumberFormatException e) {
            System.err.println("Invalid number. Returning to menu.");
            System.out.println("Pressione Enter para voltar ao menu...");
            scanner.nextLine();
            return;
        }

        System.out.println("Computing max flow between " + sourceId + " and " + sinkId + " (may use sample CSV if repositories have no capacity graph)...");
        MaxFlow.Result<?,?> res = MaxFlowService.computeUsingProjectGraphIfAvailable(sourceId, sinkId);

        if (res == null) {
            System.err.println("Computation returned null result.");
        } else {
            System.out.println(formatResult(res, Mode.DETAILED));
        }

        System.out.println("Pressione Enter para voltar ao menu...");
        scanner.nextLine();
    }
}
