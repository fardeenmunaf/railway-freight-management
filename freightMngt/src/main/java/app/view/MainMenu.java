package app.view;

import java.util.Scanner;

public class MainMenu {
    static Scanner scanner = new Scanner(System.in);

    public static void display() {
        Integer choice = null;

        System.out.println("Welcome to the Freight Management System Main Menu");


        //Some menu options to example
        while (true) {
            System.out.println("--------------------------------");
            System.out.println("|   Please select an option:   |");
            System.out.println("| 1. Manage Trains             |");
            System.out.println("| 2. Manage Wagons             |");
            System.out.println("| 3. Manage Train Services     |");
            System.out.println("| 4. Exit                      |");
            System.out.println("--------------------------------");
            System.out.print("Enter your choice (1-4): ");

            String input = scanner.nextLine();
            try {
                choice = Integer.parseInt(input);
                if (choice < 1 || choice > 4) {
                    System.out.println("Invalid choice. Please enter a number between 1 and 4.");
                } else if (choice == 4) {
                    System.out.println("Exiting the system. Goodbye!");
                    System.exit(0);
                } else {
                    // Handle choices 1-3
                    switch (choice) {
                        case 1:
                            System.out.println("Manage Trains selected.");
                            // TODO: Call appropriate train management method
                            break;
                        case 2:
                            System.out.println("Manage wagons selected.");
                            // TODO: Call appropriate wagon management method
                            break;
                        case 3:
                            System.out.println("Manage Train Services selected.");
                            // TODO: Call appropriate train services management method
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
