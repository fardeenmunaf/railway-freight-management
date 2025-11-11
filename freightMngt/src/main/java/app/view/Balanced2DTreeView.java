package app.view;

import app.utils.Balanced2DTreeBuilder.Balanced2DTree;
import app.utils.Balanced2DTreeBuilder.Station;
import app.utils.Balanced2DTreeBuilder.TemporalComplexity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * View class for displaying Balanced 2D-Tree results.
 * This class only handles presentation logic - no business logic.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class Balanced2DTreeView {

    private static final String CSV_PATH = "C:\\Users\\farde\\OneDrive\\Ambiente de Trabalho\\sem3-pi-25-26-g113-repo\\freightMngt\\src\\main\\java\\app\\sampleDataset\\train_stations_europe.csv";

    public static void display(Scanner scanner) {
        Balanced2DTree tree = loadTreeFromCSV();

        if (tree == null) {
            displayError("Failed to load station data from CSV file.");
            System.out.println("\nPress Enter to return to Main Menu...");
            scanner.nextLine();
            return;
        }

        while (true) {
            displayHeader();
            displayMenu();

            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("b")) {
                System.out.println("\n✓ Returning to Main Menu...\n");
                return;
            }

            try {
                int choice = Integer.parseInt(input);

                if (choice < 1 || choice > 6) {
                    displayError("Please enter a number between 1 and 6.");
                    continue;
                }

                if (choice == 6) {
                    System.out.println("\n✓ Returning to Main Menu...\n");
                    return;
                }

                handleMenuChoice(choice, tree, scanner);
                promptContinue(scanner);

            } catch (NumberFormatException e) {
                displayError("Invalid input. Please enter a number.");
            }
        }
    }

    /**
     * Loads tree from CSV file.
     */
    private static Balanced2DTree loadTreeFromCSV() {
        List<Station> stations = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(CSV_PATH))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip header
                }

                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    try {
                        double latitude = Double.parseDouble(parts[0].trim());
                        double longitude = Double.parseDouble(parts[1].trim());
                        String name = parts[2].trim();

                        if (!name.isEmpty()) {
                            stations.add(new Station(latitude, longitude, name));
                        }
                    } catch (NumberFormatException e) {
                        // Skip invalid rows
                    }
                }
            }

            if (stations.isEmpty()) {
                return null;
            }

            Balanced2DTree tree = new Balanced2DTree();
            tree.bulkBuild(stations);
            return tree;

        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
            return null;
        }
    }

    /**
     * Displays the header banner.
     */
    private static void displayHeader() {
        System.out.println("\n" + "═".repeat(70));
        System.out.println("║" + " ".repeat(15) + "BALANCED 2D-TREE VISUALIZATION" + " ".repeat(24) + "║");
        System.out.println("║" + " ".repeat(10) + "Spatial Indexing for Station Management" + " ".repeat(19) + "║");
        System.out.println("═".repeat(70));
    }

    /**
     * Displays the interactive menu.
     */
    private static void displayMenu() {
        System.out.println("\n┌────────────────────────────────────────────────────────────────────┐");
        System.out.println("│                        MENU OPTIONS                                │");
        System.out.println("├────────────────────────────────────────────────────────────────────┤");
        System.out.println("│  1. View Tree Structure Information                               │");
        System.out.println("│  2. Display Bucket Sizes                                          │");
        System.out.println("│  3. Show Temporal Complexity Analysis                             │");
        System.out.println("│  4. Complete Tree Summary                                         │");
        System.out.println("│  5. View Sample Stations (First 20)                               │");
        System.out.println("│  6. Exit to Main Menu                                             │");
        System.out.println("└────────────────────────────────────────────────────────────────────┘");
        System.out.print("\nEnter your choice (1-6) or 'b' to go back: ");
    }

    /**
     * Handles the user's menu choice.
     */
    private static void handleMenuChoice(int choice, Balanced2DTree tree, Scanner scanner) {
        Balanced2DTreeView view = new Balanced2DTreeView();

        switch (choice) {
            case 1:
                view.displayTreeInfo(tree);
                break;
            case 2:
                view.displayBucketSizes(tree);
                break;
            case 3:
                view.displayTemporalComplexity(tree);
                break;
            case 4:
                view.displayCompleteTreeSummary(tree);
                break;
            case 5:
                displayFirstStations(tree);
                break;
        }
    }

    /**
     * Displays first 20 stations from the tree.
     */
    private static void displayFirstStations(Balanced2DTree tree) {
        List<Station> allStations = tree.getAllStations();

        if (allStations.isEmpty()) {
            displayError("No stations found in tree.");
            return;
        }

        List<Station> firstTwenty = allStations.subList(0, Math.min(20, allStations.size()));

        Balanced2DTreeView view = new Balanced2DTreeView();
        view.displayStations(firstTwenty, String.format("First %d Stations (In-Order Traversal)", firstTwenty.size()));
    }

    /**
     * Displays the tree structure information.
     */
    public void displayTreeInfo(Balanced2DTree tree) {
        System.out.println("\n╔════════════════════════════════════════════════════════════════════╗");
        System.out.println("║              BALANCED 2D-TREE STRUCTURE                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════╝");
        System.out.println("┌────────────────────────────────────────────────────────────────────┐");
        System.out.printf("│  Total Nodes:          %-43d │%n", tree.getSize());
        System.out.printf("│  Tree Height:          %-43d │%n", tree.getHeight());
        System.out.printf("│  Expected Height:      ~%-42d │%n", (int) Math.ceil(Math.log(tree.getSize()) / Math.log(2)));
        System.out.printf("│  Balance Factor:       %-43s │%n", "Optimal");
        System.out.printf("│  Dimension Split:      %-43s │%n", "Latitude/Longitude Alternating");
        System.out.println("└────────────────────────────────────────────────────────────────────┘");
    }

    /**
     * Displays bucket sizes in order.
     */
    public void displayBucketSizes(Balanced2DTree tree) {
        List<Integer> bucketSizes = tree.getBucketSizes();

        System.out.println("\n╔════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                    BUCKET DISTRIBUTION                             ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════╝");

        if (bucketSizes.isEmpty()) {
            System.out.println("│  ⚠ No buckets found in the tree.                                  │");
            return;
        }

        System.out.println("┌──────────────┬─────────────────────────────────────────────────────┐");
        System.out.println("│ Bucket No.   │ Stations Count                                      │");
        System.out.println("├──────────────┼─────────────────────────────────────────────────────┤");

        int totalStations = 0;
        int displayLimit = Math.min(bucketSizes.size(), 50);

        for (int i = 0; i < displayLimit; i++) {
            int count = bucketSizes.get(i);
            totalStations += count;
            String bar = "█".repeat(Math.min(count * 2, 40));
            System.out.printf("│ Bucket %-5d │ %-3d %s%n", i + 1, count, bar);
        }

        if (bucketSizes.size() > displayLimit) {
            for (int i = displayLimit; i < bucketSizes.size(); i++) {
                totalStations += bucketSizes.get(i);
            }
            System.out.printf("│ ... (%d more buckets not shown)                                 │%n",
                    bucketSizes.size() - displayLimit);
        }

        System.out.println("└──────────────┴─────────────────────────────────────────────────────┘");
        System.out.printf("Total Buckets: %d | Total Stations: %d%n", bucketSizes.size(), totalStations);
    }

    /**
     * Displays temporal complexity analysis.
     */
    public void displayTemporalComplexity(Balanced2DTree tree) {
        TemporalComplexity complexity = tree.getTemporalComplexity();

        System.out.println("\n╔════════════════════════════════════════════════════════════════════╗");
        System.out.println("║              TEMPORAL COMPLEXITY ANALYSIS                          ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════╝");
        System.out.println("┌────────────────────────────────────────────────────────────────────┐");
        System.out.printf("│  Build Complexity:         %-39s │%n", complexity.buildComplexity);
        System.out.printf("│  Point Search:             %-39s │%n", complexity.searchComplexity);
        System.out.printf("│  Range Search:             %-39s │%n", complexity.rangeSearchComplexity);
        System.out.println("├────────────────────────────────────────────────────────────────────┤");
        System.out.printf("│  Total Nodes:              %-39d │%n", complexity.totalNodes);
        System.out.printf("│  Expected Height:          %-39d │%n", complexity.expectedHeight);
        System.out.println("└────────────────────────────────────────────────────────────────────┘");
    }

    /**
     * Displays a complete summary of the tree.
     */
    public void displayCompleteTreeSummary(Balanced2DTree tree) {
        displayTreeInfo(tree);
        System.out.println();
        displayBucketSizes(tree);
        System.out.println();
        displayTemporalComplexity(tree);
    }

    /**
     * Displays a list of stations.
     */
    public void displayStations(List<Station> stations, String title) {
        System.out.println("\n╔════════════════════════════════════════════════════════════════════╗");
        System.out.printf("║  %-65s ║%n", title);
        System.out.println("╚════════════════════════════════════════════════════════════════════╝");

        if (stations == null || stations.isEmpty()) {
            System.out.println("│  ⚠ No stations to display.                                        │");
            return;
        }

        System.out.println("┌────┬────────────────────────────────┬─────────────┬──────────────┐");
        System.out.println("│ No.│ Station Name                   │  Latitude   │  Longitude   │");
        System.out.println("├────┼────────────────────────────────┼─────────────┼──────────────┤");

        for (int i = 0; i < stations.size(); i++) {
            Station station = stations.get(i);
            String truncatedName = station.name.length() > 30 ?
                    station.name.substring(0, 27) + "..." : station.name;
            System.out.printf("│ %-2d │ %-30s │ %11.6f │ %12.6f │%n",
                    (i + 1), truncatedName, station.latitude, station.longitude);
        }

        System.out.println("└────┴────────────────────────────────┴─────────────┴──────────────┘");
        System.out.printf("Total Stations Displayed: %d%n", stations.size());
    }

    /**
     * Displays an error message.
     */
    private static void displayError(String message) {
        System.out.println("\n┌────────────────────────────────────────────────────────────────────┐");
        System.out.printf("│ ⚠ ERROR: %-58s │%n", message);
        System.out.println("└────────────────────────────────────────────────────────────────────┘");
    }

    /**
     * Prompts the user to continue.
     */
    private static void promptContinue(Scanner scanner) {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
}
