package app.db.dao.train;

import app.model.Train;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO interface for CRUD operations on Train.
 * Defines methods for manipulating Train data in the database.
 *
 * @author G113 2DK
 * @version 1.0
 */
public interface TrainDAO {

    /**
     * Creates a new Train record in the database.
     *
     * @param train the Train object to be created
     * @throws SQLException if an error occurs during the operation
     */
    void create(Train train) throws SQLException;

    /**
     * Retrieves a Train based on its unique identifier.
     *
     * @param id the identifier of the Train
     * @return the Train found or null if it does not exist
     * @throws SQLException if an error occurs during the operation
     */
    Train get(int id) throws SQLException;

    /**
     * Retrieves all Trains stored in the database.
     *
     * @return list with all Trains
     * @throws SQLException if an error occurs during the operation
     */
    List<Train> getAll() throws SQLException;

    /**
     * Updates the data of an existing Train in the database.
     *
     * @param train the Train object with updated data
     * @throws SQLException if an error occurs during the operation
     */
    void update(Train train) throws SQLException;

    /**
     * Removes a Train from the database based on its unique identifier.
     *
     * @param id the identifier of the Train to remove
     * @throws SQLException if an error occurs during the operation
     */
    void delete(int id) throws SQLException;

    /**
     * Retrieves a Train by its operational code.
     *
     * @param code the operational code of the Train
     * @return the Train found or null if it does not exist
     * @throws SQLException if an error occurs during the operation
     */
    Train getByCode(int code) throws SQLException;

    /**
     * Retrieves all Trains operated by a specific company.
     *
     * @param operatorId the identifier of the operator company
     * @return list with Trains for the specified operator
     * @throws SQLException if an error occurs during the operation
     */
    List<Train> getByOperator(int operatorId) throws SQLException;

    /**
     * Retrieves all Trains departing from a specific station.
     *
     * @param departure the departure station name
     * @return list with Trains departing from the specified station
     * @throws SQLException if an error occurs during the operation
     */
    List<Train> getByDeparture(String departure) throws SQLException;

    /**
     * Retrieves all Trains with a specific destination.
     *
     * @param destination the destination station name
     * @return list with Trains going to the specified destination
     * @throws SQLException if an error occurs during the operation
     */
    List<Train> getByDestination(String destination) throws SQLException;

    /**
     * Checks if a Train exists with the specified ID.
     *
     * @param id the identifier of the Train
     * @return true if it exists, false otherwise
     * @throws SQLException if an error occurs during the operation
     */
    boolean exists(int id) throws SQLException;
}

