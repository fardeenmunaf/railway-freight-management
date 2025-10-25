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
            System.out.println("| 2. Dispatch Stock              |");
            System.out.println("| 3. Relocate Stock              |");
            System.out.println("| 4. Inventory Orders Pick       |");
            System.out.println("| 5. Returned Orders Quarantine  |");
            System.out.println("| 6. Exit                        |");
            System.out.println("----------------------------------");
            System.out.print("Enter your choice (1-6): ");

            String input = scanner.nextLine();
            try {
                choice = Integer.parseInt(input);
                if (choice < 1 || choice > 6) {
                    System.out.println("Invalid choice. Please enter a number between 1 and 4.");
                } else if (choice == 6) {
                    System.out.println("Exiting the system. Goodbye!");
                    System.exit(0);
                } else {
                    switch (choice) {
                        case 1:
                            TravelTimeFeature.calculateTravelTimeFromUserInput(scanner);
                            break;
                        case 2:
                            break;
                        case 3:
                            break;
                        case 4:
                            break;
                        case 5:
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
