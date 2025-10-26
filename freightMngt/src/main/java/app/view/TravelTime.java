package app.view;

import app.model.Facility;
import app.model.Locomotive;
import app.repositories.FacilityRepository;
import app.repositories.LocomotiveRepository;
import app.repositories.Repositories;
import app.service.TravelTimeFeature;

import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * View class responsible for handling the travel time calculation user interface.
 * This class provides a console-based interface for users to calculate travel time
 * between two directly connected railway stations using a selected locomotive.
 * <p>
 * Features include:
 * - Display of available stations and locomotives
 * - Input validation for station and locomotive IDs
 * - Travel time calculation and results display
 * - Error handling with user-friendly messages
 * - Option to return to main menu at any time
 */
public class TravelTime {

    /**
     * Displays the travel time calculation interface and handles user interaction.
     * This method provides a complete workflow for calculating travel time between stations:
     * <p>
     * 1. Shows available stations (facility type 2 only)
     * 2. Shows available locomotives with their specifications
     * 3. Prompts user for origin station, destination station, and locomotive
     * 4. Validates inputs and calculates travel time
     * 5. Displays detailed results including distance, speed, and travel time
     * 6. Handles various error conditions with appropriate messages
     * <p>
     * The user can type 'b' at any input prompt to return to the main menu.
     *
     * @param scanner the Scanner object used for reading user input
     */
    public static void display(Scanner scanner) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           CALCULATE TRAVEL TIME");
        System.out.println("=".repeat(50));
        System.out.println("Type 'b' at any time to return to menu.\n");

        Repositories repositories = Repositories.getInstance();
        FacilityRepository facilityRepo = repositories.getFacilityRepository();
        LocomotiveRepository locomotiveRepo = repositories.getLocomotiveRepository();

        System.out.println("AVAILABLE STATIONS:");
        System.out.println("-".repeat(30));
        List<Facility> facilities = facilityRepo.getAllFacilities();
        if (facilities != null && !facilities.isEmpty()) {
            facilities.stream()
                    .filter(f -> f != null && f.getFacilityType() != null
                            && Integer.valueOf(2).equals(f.getFacilityType().getFacilityTypeId()))
                    .sorted(Comparator.comparing(f -> f.getFacilityId() == null ? Integer.MAX_VALUE : f.getFacilityId()))
                    .forEach(f -> System.out.printf("   %d - %s%n",
                            f.getFacilityId(),
                            f.getName() != null ? f.getName() : "(no name)"));
        } else {
            System.out.println("   (no stations found)");
        }

        System.out.println("\nAVAILABLE LOCOMOTIVES:");
        System.out.println("-".repeat(30));
        List<Locomotive> locos = locomotiveRepo.getAllLocomotives();
        if (locos != null && !locos.isEmpty()) {
            locos.stream()
                    .sorted(Comparator.comparing(l -> l.getLocomotiveId() == null ? Integer.MAX_VALUE : l.getLocomotiveId()))
                    .forEach(l -> System.out.printf("   %d - %s (Max Speed: %.0f km/h)%n",
                            l.getLocomotiveId(),
                            l.getName() != null ? l.getName() : "(no name)",
                            l.getMaxSpeed() != null ? l.getMaxSpeed() : 0.0));
        } else {
            System.out.println("   (no locomotives found)");
        }

        try {
            System.out.print("\nEnter origin station ID: ");
            String originInput = scanner.nextLine().trim();
            if ("b".equalsIgnoreCase(originInput)) return;
            int originId = Integer.parseInt(originInput);

            System.out.print("Enter destination station ID: ");
            String destInput = scanner.nextLine().trim();
            if ("b".equalsIgnoreCase(destInput)) return;
            int destinationId = Integer.parseInt(destInput);

            System.out.print("Enter locomotive ID: ");
            String locoInput = scanner.nextLine().trim();
            if ("b".equalsIgnoreCase(locoInput)) return;
            int locomotiveId = Integer.parseInt(locoInput);

            TravelTimeFeature.TravelTimeCalculator calculator = TravelTimeFeature.create(
                    repositories.getFacilityRepository(),
                    repositories.getLineSegmentRepository(),
                    repositories.getLineSegmentFacilityRepository(),
                    repositories.getLocomotiveRepository()
            );

            double travelTime = calculator.calculateTravelTimeMinutes(originId, destinationId, locomotiveId);

            Facility origin = facilityRepo.getFacilityById(originId);
            Facility destination = facilityRepo.getFacilityById(destinationId);
            Locomotive locomotive = locomotiveRepo.getLocomotiveById(locomotiveId);

            System.out.println("\n" + "=".repeat(50));
            System.out.println("              JOURNEY RESULTS");
            System.out.println("=".repeat(50));
            System.out.printf("Locomotive: %s (ID: %d)%n",
                    locomotive.getName() != null ? locomotive.getName() : "(no name)", locomotiveId);
            System.out.printf("Origin: %s (ID: %d)%n", origin.getName(), originId);
            System.out.printf("Destination: %s (ID: %d)%n", destination.getName(), destinationId);
            System.out.println("-".repeat(50));
            System.out.printf("Distance: %.2f km%n", calculator.getDistanceKm());
            System.out.printf("Speed: %.2f km/h%n", calculator.getEffectiveSpeedKmh());
            System.out.printf("Travel Time: %.2f minutes (%.0fh %.0fm)%n",
                    travelTime,
                    Math.floor(travelTime / 60.0),
                    travelTime % 60.0);
            System.out.println("=".repeat(50));

        } catch (NumberFormatException e) {
            System.out.println("Error: please enter valid numeric IDs.");
        } catch (TravelTimeFeature.NoDirectConnectionException e) {
            System.out.println("Connection Error: " + e.getMessage());
        } catch (TravelTimeFeature.InvalidSpeedException e) {
            System.out.println("Speed Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }

        System.out.println("\nPress Enter to return to main menu...");
        scanner.nextLine();
    }
}
