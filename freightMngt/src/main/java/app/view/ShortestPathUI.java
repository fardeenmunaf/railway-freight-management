package app.view;

import app.service.sprint3.ShortestPathService;
import app.service.sprint3.StationLoaderService;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class ShortestPathUI {

    private final ShortestPathService pathService;
    private final StationLoaderService dataService;
    private final Scanner scanner;

    private ShortestPathUI(Scanner scanner) throws IOException {
        this.dataService = StationLoaderService.getInstance();
        this.pathService = new ShortestPathService();
        this.scanner = scanner;
    }

    public static void display(Scanner scanner) throws IOException {
        ShortestPathUI ui = new ShortestPathUI(scanner);
        System.out.println("Dados carregados com sucesso!");
        System.out.println("Total de estações: " + ui.dataService.getTotalStations());
        System.out.println("Total de linhas: " + ui.dataService.getTotalConnections());
        System.out.println();
        ui.showMenu();
    }

    private void showMenu() {
        boolean exit = false;
        while (!exit) {
            System.out.println("=== CÁLCULO DE CAMINHOS ===");
            System.out.println("1. Calcular caminho mais curto");
            System.out.println("2. Listar estações disponíveis");
            System.out.println("0. Voltar ao menu principal");
            System.out.print("Escolha uma opção: ");
            try {
                int option = scanner.nextInt();
                scanner.nextLine();
                switch (option) {
                    case 1:
                        calculatePath();
                        break;
                    case 2:
                        listStations();
                        break;
                    case 0:
                        exit = true;
                        System.out.println("A voltar ao menu principal...");
                        break;
                    default:
                        System.out.println("Opção inválida!");
                }
            } catch (Exception e) {
                scanner.nextLine();
                System.out.println("Entrada inválida! Por favor, insira um número.");
            }
            System.out.println();
        }
    }

    private void calculatePath() {
        System.out.println("\n=== CÁLCULO DO CAMINHO MAIS CURTO ===");
        Integer departureId = getStationId("partida");
        Integer arrivalId = getStationId("chegada");

        if (departureId == null || arrivalId == null) {
            return;
        }

        System.out.println("\nA calcular o melhor caminho...");
        ShortestPathService.PathResultWithAlgorithm result = pathService.calculateBestPath(departureId, arrivalId);

        if (result == null) {
            System.out.println("Não foi possível calcular o caminho.");
            System.out.println("   (Estações não existem ou sem caminho conectado)");
            return;
        }

        System.out.println("\n--- RESULTADO ---");
        System.out.println("Algoritmo usado: " + result.getAlgorithmUsed());
        System.out.println();

        ShortestPathService.PathResult pathResult = result.getPathResult();

        if (result.hasNegativeCycle()) {
            System.out.println("AVISO: Ciclo negativo detectado!");
            System.out.println("O grafo contém inconsistências de configuração.");
            System.out.println();
            System.out.println(pathResult.formatPath());
        } else {
            System.out.println(pathResult.formatPath());
        }
    }

    private Integer getStationId(String tipo) {
        System.out.print("ID da estação de " + tipo + ": ");
        try {
            Integer id = scanner.nextInt();
            scanner.nextLine();
            if (!dataService.stationExists(id)) {
                System.out.println("Estação com ID " + id + " não existe!");
                return null;
            }
            System.out.println("Estação " + id + " encontrada!");
            return id;
        } catch (Exception e) {
            scanner.nextLine();
            System.out.println("ID inválido!");
            return null;
        }
    }

    private void listStations() {
        System.out.println("\n=== ESTAÇÕES DISPONÍVEIS ===");
        if (dataService.getTotalStations() == 0) {
            System.out.println("Nenhuma estação carregada.");
            return;
        }

        List<String> formattedStations = dataService.getFormattedStationList();
        formattedStations.forEach(System.out::println);
        System.out.println("\nTotal: " + dataService.getTotalStations() + " estações");
    }
}
