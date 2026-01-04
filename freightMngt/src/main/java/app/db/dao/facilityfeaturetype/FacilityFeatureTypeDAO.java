package app.db.dao.facilityfeaturetype;

import app.model.FacilityFeatureType;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO interface for CRUD operations on FacilityFeatureType entity.
 * Defines methods for data manipulation of FacilityFeatureType in the database.
 *
 * @author G113 2DK
 * @version 1.0
 */
public interface FacilityFeatureTypeDAO {

    /**
     * Creates a new FacilityFeatureType record in the database.
     *
     * @param facilityFeatureType the FacilityFeatureType object to be created
     * @throws SQLException if an error occurs during the operation
     */
    void create(FacilityFeatureType facilityFeatureType) throws SQLException;

    /**
     * Retrieves a FacilityFeatureType based on its unique identifier.
     *
     * @param id the identifier of the FacilityFeatureType
     * @return the found FacilityFeatureType or null if it doesn't exist
     * @throws SQLException if an error occurs during the operation
     */
    FacilityFeatureType get(int id) throws SQLException;

    /**
     * Retrieves all FacilityFeatureTypes stored in the database.
     *
     * @return list with all FacilityFeatureTypes
     * @throws SQLException if an error occurs during the operation
     */
    List<FacilityFeatureType> getAll() throws SQLException;

    /**
     * Updates the data of an existing FacilityFeatureType in the database.
     *
     * @param facilityFeatureType the FacilityFeatureType object with updated data
     * @throws SQLException if an error occurs during the operation
     */
    void update(FacilityFeatureType facilityFeatureType) throws SQLException;

    /**
     * Removes a FacilityFeatureType from the database based on its unique identifier.
     *
     * @param id the identifier of the FacilityFeatureType to remove
     * @throws SQLException if an error occurs during the operation
     */
    void delete(int id) throws SQLException;

    /**
     * Retrieves a FacilityFeatureType by name.
     *
     * @param name the name of the FacilityFeatureType
     * @return the found FacilityFeatureType or null if it doesn't exist
     * @throws SQLException if an error occurs during the operation
     */
    FacilityFeatureType getByName(String name) throws SQLException;
}

