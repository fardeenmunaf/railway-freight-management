package app.controller;

import app.model.Allocation;
import app.model.WarehouseBay;
import app.service.InventoryDispatch;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller responsible for managing the execution
 * of the USEI04 - Pick Path Sequencing logic.
 * <p>
 * This class acts as a bridge between the system’s data layer
 * (which provides the list of WarehouseBay objects)
 * and the PickPathSequencer use case, which generates
 * and displays the picking routes based on the current warehouse state.
 *
 * @author G113
 * @version 1.1
 */
public class PickPathController {

    /**
     * Executes the Pick Path Sequencing (USEI04) logic using real bay data.
     * <p>
     * The method transforms the received list of WarehouseBay objects
     * into Allocation objects, assuming that each bay has a quantity
     * of boxes to pick. It then invokes the PickPathSequencer use case
     * to generate and display both Strategy A and Strategy B routes.
     *
     * @param warehouseBays list of WarehouseBay objects representing bays in the warehouse
     */
    public void executePickPathSequencing(List<WarehouseBay> warehouseBays) {

        if (warehouseBays == null || warehouseBays.isEmpty()) {
            System.out.println("No bays available for pick path sequencing.");
            return;
        }

        // Convert the list of WarehouseBay objects into Allocations
        List<Allocation> allocations = new ArrayList<>();

        for (WarehouseBay bay : warehouseBays) {
            // Example logic: pick 1 box per bay (adjust if you have real quantities)
            allocations.add(new Allocation(bay, 1));
        }

        // Call the USEI04 logic (PickPathSequencer)
        InventoryDispatch.PickPathSequencer sequencer = new InventoryDispatch.PickPathSequencer();
        sequencer.generateAndDisplayRoutes(allocations);

        System.out.println("\nPick Path Sequencing completed successfully.");
    }
}