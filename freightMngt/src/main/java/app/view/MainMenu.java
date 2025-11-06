package app.view;

import app.service.TravelTimeFeature;

import java.util.Scanner;

public class MainMenu {
    static Scanner scanner = new Scanner(System.in);

    public static void display() {
        Integer choice = null;

        System.out.println("Welcome to the Freight Management System Main Menu");


        //Some menu options to example
        while (true) {
            System.out.println("----------------------------------");
            System.out.println("|   Please select an option:     |");
            System.out.println("| 1. Calculate Travel Time       |");
            System.out.println("| 2. Bay Content Overview        |");
            System.out.println("| 3. Dispatch Stock              |");
            System.out.println("| 4. Relocate Stock              |");
            System.out.println("| 5. Inventory Orders Pick       |");
            System.out.println("| 6. Returned Orders Quarantine  |");
            System.out.println("| 7. Station                     |");
            System.out.println("| 8. Exit                        |");
            System.out.println("----------------------------------");
            System.out.print("Enter your choice (1-8): ");

            String input = scanner.nextLine();
            try {
                choice = Integer.parseInt(input);
                if (choice < 1 || choice > 8) {
                    System.out.println("Invalid choice. Please enter a number between 1 and 7.");
                } else if (choice == 8) {
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
                        default:
                            System.out.println("Option not implemented yet.");
                            break;
                    }
                    System.out.println(); // Add a blank line for better readability
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
}
