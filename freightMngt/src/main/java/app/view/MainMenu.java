package app.view;

import java.io.IOException;
import java.util.Scanner;

public class MainMenu {
    static Scanner scanner = new Scanner(System.in);

    public static void display() {
        Integer choice = null;

        System.out.println("Welcome to the Freight Management System Main Menu");

        while (true) {
            System.out.println("-----------------------------------");
            System.out.println("|   Please select an option:      |");
            System.out.println("| 1. Calculate Travel Time        |");
            System.out.println("| 2. Bay Content Overview         |");
            System.out.println("| 3. Dispatch Stock               |");
            System.out.println("| 4. Relocate Stock               |");
            System.out.println("| 5. Inventory Orders Pick        |");
            System.out.println("| 6. Returned Orders Quarantine   |");
            System.out.println("| 7. Station Search               |");
            System.out.println("| 8. 2DTree                       |");
            System.out.println("| 9. Nearest Station Search       |");
            System.out.println("| 10. Radius Search               |");
            System.out.println("| 11. Range Query                 |");
            System.out.println("| 12. Train Dispatch Scheduler    |");
            System.out.println("| 13. Station Upgrade             |");
            System.out.println("| 14. Freights Route View         |");
            System.out.println("| 15. Aware Shortest Paths        |");
            System.out.println("| 0. Exit                         |");
            System.out.println("-----------------------------------");
            System.out.print("Enter your choice (0-15): ");

            String input = scanner.nextLine();
            try {
                choice = Integer.parseInt(input);
                if (choice < 0 || choice > 15) {
                    System.out.println("Invalid choice. Please enter a number between 0 and 15.");
                } else if (choice == 0) {
                    System.out.println("Exiting the system. Goodbye!");
                    System.exit(0);
                } else {
                    switch (choice) {
                        case 1:
                            TravelTime.display(scanner);
                            break;
                        case 2:
                            BayContentOverview.display(scanner);
                            break;
                        case 3:
                            InventoryDispatch.display(scanner);
                            break;
                        case 4:
                            RelocateStock.display(scanner);
                            break;
                        case 5:
                            PickingPlanView.display(scanner);
                            break;
                        case 6:
                            ReturnProcessing.display(scanner);
                            break;
                        case 7:
                            StationSearchView.display(scanner);
                            break;
                        case 8:
                            Balanced2DTreeView.display(scanner);
                            break;
                        case 9:
                            NearestStationView.display(scanner);
                            break;
                        case 10:
                            RadiusSearchView.display(scanner);
                            break;
                        case 11:
                            RangeQueryView.display(scanner);
                            break;
                        case 12:
                            TrainDispatchScheduler.display(scanner);
                            break;
                        case 13:
                            StationUpgradeView.display(scanner);
                            break;
                        case 14:
                            FreightsRouteView.display(scanner);
                            break;
                        case 15:
                            ShortestPathUI.display(scanner);
                            break;
                        default:
                            System.out.println("Option not implemented yet.");
                            break;
                    }
                    System.out.println(); // Add a blank line for better readability
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
