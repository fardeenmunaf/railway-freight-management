package app.service.sprint2;

import app.model.*;
import app.repositories.Repositories;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Demo class to test the Train Dispatch Scheduler functionality.
 * Creates sample data and demonstrates dispatch creation.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class TrainDispatchDemo {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║            TRAIN DISPATCH SCHEDULER - DEMONSTRATION            ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");

        Repositories repos = Repositories.getInstance();

        // Create sample data
        setupSampleData(repos);

        // Create a train dispatch service
        TrainDispatchService dispatchService = new TrainDispatchService(
            repos.getTrainDispatchRepository(),
            repos.getRoutePointRepository(),
            repos.getTrainServiceCompositionRepository(),
            repos.getLineSegmentRepository(),
            repos.getLineSegmentFacilityRepository(),
            repos.getFacilityRepository()
        );

        // Create a sample dispatch
        System.out.println("Creating a sample train dispatch...\n");

        TrainService trainService = repos.getTrainServiceRepository().getTrainServiceById(1);
        if (trainService == null) {
            System.out.println("✗ No train service found. Please initialize data first.");
            return;
        }

        LocalDateTime departureTime = LocalDateTime.now().plusHours(2);
        List<Integer> route = Arrays.asList(1, 2, 3); // Lisboa -> Porto -> Coimbra

        try {
            TrainDispatch dispatch = dispatchService.createManualDispatch(
                trainService,
                departureTime,
                route
            );

            System.out.println("✓ Dispatch created successfully!\n");
            printDispatchDetails(dispatch);

        } catch (Exception e) {
            System.out.println("✗ Error creating dispatch: " + e.getMessage());
            System.out.println("\nNote: Make sure to run AppInitializer first to load sample data.");
        }
    }

    private static void setupSampleData(Repositories repos) {
        // Check if data already exists
        if (repos.getTrainServiceRepository().getAllTrainServices().isEmpty()) {
            System.out.println("Setting up sample train service...\n");

            // Create a sample train
            Company operator = new Company(1, "Portuguese Railways", "CP", "PT123456789");
            repos.getCompanyRepository().addCompany(operator);

            Train train = new Train(1, 101, "Lisboa Santa Apolónia", "Porto Campanhã", operator);
            repos.getTrainRepository().addTrain(train);

            // Create a train service
            TrainService service = new TrainService(1, train,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(3),
                null, "scheduled");
            repos.getTrainServiceRepository().addTrainService(service);

            // Add some composition (locomotive and wagons)
            Locomotive loco = repos.getLocomotiveRepository().getLocomotiveById(1);
            if (loco != null) {
                ServiceLocomotive serviceLoco = new ServiceLocomotive(1, service, loco, 1);
                repos.getServiceLocomotiveRepository().addServiceLocomotive(serviceLoco);

                TrainServiceComposition composition = new TrainServiceComposition(service, serviceLoco, null);
                repos.getTrainServiceCompositionRepository().addTrainServiceComposition(composition);
            }

            System.out.println("✓ Sample train service created.\n");
        }
    }

    private static void printDispatchDetails(TrainDispatch dispatch) {
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("                    DISPATCH DETAILS");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.printf("Dispatch ID:         %d%n", dispatch.getDispatchId());
        System.out.printf("Train Service ID:    %d%n", dispatch.getTrainService().getServiceId());
        System.out.printf("Route Type:          %s%n", dispatch.getRouteType());
        System.out.printf("Status:              %s%n", dispatch.getStatus());
        System.out.printf("Departure:           %s%n", dispatch.getPlannedDepartureTime());
        System.out.printf("Est. Arrival:        %s%n", dispatch.getEstimatedArrivalTime());
        System.out.printf("Total Distance:      %.2f km%n", dispatch.getTotalDistanceKm());
        System.out.printf("Est. Travel Time:    %.0f minutes%n", dispatch.getEstimatedTravelTimeMinutes());
        System.out.printf("Route Points:        %d%n", dispatch.getRoute().size());
        System.out.println("═══════════════════════════════════════════════════════════════\n");

        if (!dispatch.getRoute().isEmpty()) {
            System.out.println("ROUTE DETAILS:");
            System.out.println("───────────────────────────────────────────────────────────────");
            for (RoutePoint rp : dispatch.getRoute()) {
                System.out.printf("%d. %s%n",
                    rp.getSequenceOrder(),
                    rp.getFacility() != null ? rp.getFacility().getName() : "Unknown");
                System.out.printf("   Arrival:   %s%n", rp.getEstimatedArrivalTime());
                System.out.printf("   Departure: %s%n", rp.getEstimatedDepartureTime());
                System.out.printf("   Distance:  %.2f km from start%n", rp.getDistanceFromStartKm());
                if (!"NONE".equals(rp.getCrossingOperation())) {
                    System.out.printf("   ⚠ Crossing: %s (Wait: %d min)%n",
                        rp.getCrossingOperation(),
                        rp.getExpectedWaitTimeMinutes());
                }
                System.out.println();
            }
        }
    }
}

