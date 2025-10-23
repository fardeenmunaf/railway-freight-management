package app.controller;

import app.model.InventoryMove;
import app.repositories.InventoryMoveRepository;
import app.repositories.Repositories;
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
    private final InventoryMoveRepository repository;

    /**
     * Default constructor that initializes the controller with the inventory move repository.
     */
    public InventoryMoveController() {
        this.repository = Repositories.getInstance().getInventoryMoveRepository();
    }

    /**
     * Creates and adds a new inventory move to the system.
     *
     * @param inventoryMove the InventoryMove object to be created
     * @return the created InventoryMove object
     */
    public InventoryMove createInventoryMove(InventoryMove inventoryMove) {
        if (inventoryMove != null) {
            repository.addInventoryMove(inventoryMove);
        }
        return inventoryMove;
    }

    /**
     * Retrieves all inventory moves from the system.
     *
     * @return a list of all InventoryMove objects
     */
    public List<InventoryMove> getAllInventoryMoves() {
        return repository.getAllInventoryMoves();
    }

    /**
     * Retrieves an inventory move by its unique identifier.
     *
     * @param inventoryMoveId the unique identifier of the inventory move
     * @return an Optional containing the InventoryMove if found, or empty if not found
     */
    public Optional<InventoryMove> getInventoryMoveById(Integer inventoryMoveId) {
        InventoryMove inventoryMove = repository.getInventoryMoveById(inventoryMoveId);
        return Optional.ofNullable(inventoryMove);
    }

    /**
     * Updates an existing inventory move in the system.
     *
     * @param inventoryMoveId the unique identifier of the inventory move to update
     * @param updatedInventoryMove the InventoryMove object with updated information
     * @return true if the inventory move was updated successfully, false otherwise
     */
    public boolean updateInventoryMove(Integer inventoryMoveId, InventoryMove updatedInventoryMove) {
        if (updatedInventoryMove == null) {
            return false;
        }
        return repository.updateInventoryMove(inventoryMoveId, updatedInventoryMove);
    }

    /**
     * Deletes an inventory move from the system.
     *
     * @param inventoryMoveId the unique identifier of the inventory move to delete
     * @return true if the inventory move was deleted successfully, false otherwise
     */
    public boolean deleteInventoryMove(Integer inventoryMoveId) {
        return repository.deleteInventoryMove(inventoryMoveId);
    }

    /**
     * Gets the next available inventory move ID.
     *
     * @return the next inventory move ID to be used
     */
    public Integer getNextInventoryMoveId() {
        return repository.getNextMoveId();
    }

    /**
     * Clears all inventory moves from the system.
     */
    public void clearAllInventoryMoves() {
        repository.clear();
    }
}
