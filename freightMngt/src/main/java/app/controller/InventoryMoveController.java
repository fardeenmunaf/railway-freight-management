package app.controller;

import app.model.InventoryMove;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing InventoryMove entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for InventoryMove objects.
 * This controller manages inventory move data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class InventoryMoveController {
    private List<InventoryMove> inventoryMoves;

    /**
     * Default constructor that initializes the inventory move list.
     */
    public InventoryMoveController() {
        this.inventoryMoves = new ArrayList<>();
    }

    /**
     * Creates and adds a new inventory move to the system.
     *
     * @param inventoryMove the InventoryMove object to be created
     * @return the created InventoryMove object
     */
    public InventoryMove createInventoryMove(InventoryMove inventoryMove) {
        if (inventoryMove != null) {
            inventoryMoves.add(inventoryMove);
        }
        return inventoryMove;
    }

    /**
     * Retrieves all inventory moves from the system.
     *
     * @return a list of all InventoryMove objects
     */
    public List<InventoryMove> getAllInventoryMoves() {
        return new ArrayList<>(inventoryMoves);
    }

    /**
     * Retrieves an inventory move by its unique identifier.
     *
     * @param inventoryMoveId the unique identifier of the inventory move
     * @return an Optional containing the InventoryMove if found, or empty if not found
     */
    public Optional<InventoryMove> getInventoryMoveById(Integer inventoryMoveId) {
        return inventoryMoves.stream()
                .filter(inventoryMove -> inventoryMove.getInventoryMoveId().equals(inventoryMoveId))
                .findFirst();
    }

    /**
     * Updates an existing inventory move in the system.
     *
     * @param inventoryMoveId the unique identifier of the inventory move to update
     * @param updatedInventoryMove the InventoryMove object with updated information
     * @return true if the inventory move was updated successfully, false otherwise
     */
    public boolean updateInventoryMove(Integer inventoryMoveId, InventoryMove updatedInventoryMove) {
        Optional<InventoryMove> existingInventoryMove = getInventoryMoveById(inventoryMoveId);
        if (existingInventoryMove.isPresent()) {
            int index = inventoryMoves.indexOf(existingInventoryMove.get());
            inventoryMoves.set(index, updatedInventoryMove);
            return true;
        }
        return false;
    }

    /**
     * Deletes an inventory move from the system.
     *
     * @param inventoryMoveId the unique identifier of the inventory move to delete
     * @return true if the inventory move was deleted successfully, false otherwise
     */
    public boolean deleteInventoryMove(Integer inventoryMoveId) {
        return inventoryMoves.removeIf(inventoryMove -> inventoryMove.getInventoryMoveId().equals(inventoryMoveId));
    }
}

