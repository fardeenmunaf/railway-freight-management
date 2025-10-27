package app.repositories;

import app.model.InventoryMove;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing inventory moves in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class InventoryMoveRepository {

    private final List<InventoryMove> inventoryMoves = new ArrayList<>();

    public void addInventoryMove(InventoryMove inventoryMove) {
        if (inventoryMove != null && inventoryMove.getMoveId() != null) {
            if (!containsInventoryMove(inventoryMove.getMoveId())) {
                inventoryMoves.add(inventoryMove);
            }
        }
    }

    public List<InventoryMove> getAllInventoryMoves() {
        return new ArrayList<>(inventoryMoves);
    }

    public InventoryMove getInventoryMoveById(Integer moveId) {
        for (InventoryMove move : inventoryMoves) {
            if (moveId.equals(move.getMoveId())) {
                return move;
            }
        }
        return null;
    }

    public boolean updateInventoryMove(Integer moveId, InventoryMove updatedMove) {
        InventoryMove existingMove = getInventoryMoveById(moveId);
        if (existingMove != null) {
            int index = inventoryMoves.indexOf(existingMove);
            inventoryMoves.set(index, updatedMove);
            return true;
        }
        return false;
    }

    public boolean deleteInventoryMove(Integer moveId) {
        return inventoryMoves.removeIf(move -> moveId.equals(move.getMoveId()));
    }

    public Integer getNextMoveId() {
        int maxId = 0;
        for (InventoryMove move : inventoryMoves) {
            if (move.getMoveId() != null && move.getMoveId() > maxId) {
                maxId = move.getMoveId();
            }
        }
        return maxId + 1;
    }

    private boolean containsInventoryMove(Integer moveId) {
        return getInventoryMoveById(moveId) != null;
    }

    public void clear() {
        inventoryMoves.clear();
    }
}

