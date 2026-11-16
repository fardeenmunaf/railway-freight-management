package app.view;

import app.service.sprint3.RailHubCentralityAnalysis;
import app.service.sprint3.StationLoaderService;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * View for Rail Hub Centrality Analysis (USEI13).
 * Provides an interactive menu to display hub rankings and analysis results.
 */
public class RailHubCentralityView {

    private final RailHubCentralityAnalysis analysis;
    private final StationLoaderService dataService;
    private final Scanner scanner;

    private RailHubCentralityView(Scanner scanner) throws IOException {
        this.dataService = StationLoaderService.getInstance();
        this.analysis = new RailHubCentralityAnalysis();
        this.scanner = scanner;
    }

    public static void display(Scanner scanner) throws IOException {
        System.out.println("=== RAIL HUB CENTRALITY ANALYSIS ===");
        System.out.println("Computing betweenness, closeness and hub scores for the whole network...");
        System.out.println("This can take up to a minute, please wait.");
        RailHubCentralityView view = new RailHubCentralityView(scanner);
        System.out.println("Data loaded and metrics calculated successfully!");
        System.out.println();
        view.showMenu();
    }

    private void showMenu() {
        boolean exit = false;
        while (!exit) {
            System.out.println("=== HUB ANALYSIS MENU ===");
            System.out.println("1. View Top N Hubs");
            System.out.println("2. View Complete Ranking");
            System.out.println("3. View Specific Station Details");
            System.out.println("0. Return to main menu");
            System.out.print("Choose an option: ");

            try {
                int option = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (option) {
                    case 1:
                        showTopNHubs();
                        break;
                    case 2:
                        showFullRanking();
                        break;
                    case 3:
                        showStationDetails();
                        break;
                    case 0:
                        exit = true;
                        System.out.println("Returning to main menu...");
                        break;
                    default:
                        System.out.println("Invalid option!");
                }
            } catch (Exception e) {
                scanner.nextLine(); // Clear buffer
                System.out.println("Invalid input! Please enter a number.");
            }
            System.out.println();
        }
    }

    private void showTopNHubs() {
        System.out.print("\nHow many hubs do you want to view? (e.g., 10): ");
        try {
            int n = scanner.nextInt();
            scanner.nextLine();

            if (n <= 0) {
                System.out.println("Please enter a positive number.");
                return;
            }

            List<RailHubCentralityAnalysis.HubAnalysisResult> topHubs = analysis.getTopNHubs(n);

            if (topHubs.isEmpty()) {
                System.out.println("No results available.");
                return;
            }

            System.out.println("\n=== TOP " + Math.min(n, topHubs.size()) + " RAILWAY HUBS ===");
            System.out.println();
            printHubTable(topHubs);

        } catch (Exception e) {
            scanner.nextLine();
            System.out.println("Invalid input!");
        }
    }

    private void showFullRanking() {
        System.out.println("\n=== COMPLETE HUB RANKING ===");
        List<RailHubCentralityAnalysis.HubAnalysisResult> allResults = analysis.getRankedResults();

        if (allResults.isEmpty()) {
            System.out.println("No results available.");
            return;
        }

        System.out.println("Total stations analyzed: " + allResults.size());
        System.out.println();
        printHubTable(allResults);
    }

    private void printHubTable(List<RailHubCentralityAnalysis.HubAnalysisResult> results) {
        // Header
        System.out.println("┌──────┬─────────────────────────────┬──────────────┬──────────────┬──────────────┬──────────────┬──────────┐");
        System.out.println("│ Rank │ Station                     │  HubScore    │ Betweenness  │   Closeness  │   Strength   │  Degree  │");
        System.out.println("├──────┼─────────────────────────────┼──────────────┼──────────────┼──────────────┼──────────────┼──────────┤");

        // Data rows
        int rank = 1;
        for (RailHubCentralityAnalysis.HubAnalysisResult result : results) {
            System.out.printf("│ %-4d │ %-27s │ %12.4f │ %12.4f │ %12.4f │ %12.2f │ %8d │%n",
                rank++,
                truncateString(result.getStationName(), 27),
                result.getHubScore(),
                result.getBetweenness(),
                result.getHarmonicCloseness(),
                result.getStrength(),
                result.getDegree()
            );
        }

        // Footer
        System.out.println("└──────┴─────────────────────────────┴──────────────┴──────────────┴──────────────┴──────────────┴──────────┘");
        System.out.println();
        printLegend();
    }

    private void printLegend() {
        System.out.println("LEGEND:");
        System.out.println("  • HubScore:     Composite score (0-1) combining all metrics");
        System.out.println("  • Betweenness:  Frequency with which station appears in shortest paths");
        System.out.println("  • Closeness:    How easily the station reaches all others");
        System.out.println("  • Strength:     Sum of distances/capacities of connections");
        System.out.println("  • Degree:       Number of direct connections from the station");
        System.out.println();
        System.out.println("HUBSCORE INTERPRETATION:");
        System.out.println("  0.8 - 1.0: Major Hub (Critical)");
        System.out.println("  0.6 - 0.8: Important Hub (High priority)");
        System.out.println("  0.4 - 0.6: Regional Hub (Medium priority)");
        System.out.println("  0.2 - 0.4: Minor Hub (Low priority)");
        System.out.println("  0.0 - 0.2: Peripheral (Minimal priority)");
    }



    private void showStationDetails() {
        System.out.print("\nEnter station ID: ");
        try {
            int stationId = scanner.nextInt();
            scanner.nextLine();

            List<RailHubCentralityAnalysis.HubAnalysisResult> allResults = analysis.getRankedResults();

            RailHubCentralityAnalysis.HubAnalysisResult found = null;
            int rank = 1;
            int foundRank = -1;

            for (RailHubCentralityAnalysis.HubAnalysisResult result : allResults) {
                if (result.getStationId() == stationId) {
                    found = result;
                    foundRank = rank;
                    break;
                }
                rank++;
            }

            if (found == null) {
                System.out.println("Station with ID " + stationId + " not found.");
                return;
            }

            printStationDetails(found, foundRank, allResults.size());

        } catch (Exception e) {
            scanner.nextLine();
            System.out.println("Invalid input!");
        }
    }

    private void printStationDetails(RailHubCentralityAnalysis.HubAnalysisResult result, int rank, int total) {
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║          STATION DETAILS                                       ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.printf("║  ID:             %-45d ║%n", result.getStationId());
        System.out.printf("║  Name:           %-45s ║%n", truncateString(result.getStationName(), 45));
        System.out.printf("║  Ranking:        %-3d of %-39d ║%n", rank, total);
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.printf("║  HubScore:       %-10.4f %-34s ║%n", result.getHubScore(), getHubScoreLabel(result.getHubScore()));
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.println("║  DETAILED METRICS:                                             ║");
        System.out.printf("║    • Betweenness:        %10.4f %-27s ║%n", result.getBetweenness(),
            "(Bridge importance)");
        System.out.printf("║    • Harmonic Closeness: %10.4f %-27s ║%n", result.getHarmonicCloseness(),
            "(Global accessibility)");
        System.out.printf("║    • Strength:           %10.2f %-27s ║%n", result.getStrength(),
            "(Total capacity)");
        System.out.printf("║    • Degree:             %10d %-27s ║%n", result.getDegree(),
            "(Direct connections)");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.println("║  ANALYSIS:                                                     ║");
        System.out.println("║  " + getAnalysisText(result) + " ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
    }

    private String getHubScoreLabel(double hubScore) {
        if (hubScore >= 0.8) return "Major Hub";
        if (hubScore >= 0.6) return "Important Hub";
        if (hubScore >= 0.4) return "Regional Hub";
        if (hubScore >= 0.2) return "Minor Hub";
        return "Peripheral";
    }

    private String getAnalysisText(RailHubCentralityAnalysis.HubAnalysisResult result) {
        StringBuilder analysis = new StringBuilder();

        if (result.getHubScore() >= 0.8) {
            analysis.append("CRITICAL station! Maximum priority for investment.  ");
        } else if (result.getHubScore() >= 0.6) {
            analysis.append("Important station with high operational priority.   ");
        } else if (result.getHubScore() >= 0.4) {
            analysis.append("Regional hub with moderate importance.              ");
        } else {
            analysis.append("Station with limited importance in the network.     ");
        }

        return analysis.toString();
    }

    private String truncateString(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }
}
