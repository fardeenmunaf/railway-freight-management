package app.db.dao.trainassembly;

import java.sql.SQLException;

/**
 * DAO interface for Train Assembly operations (USLP09).
 * Provides methods to assemble train compositions and validate them using PL/SQL functions.
 *
 * @author G113 2DK
 * @version 1.0
 */
public interface TrainAssemblyDAO {

    /**
     * Assembles a train composition by assigning locomotives to a train service.
     * Calls PL/SQL function fn_assemble_train_composition.
     *
     * @param serviceId the train service ID
     * @param locomotiveIds comma-separated string of locomotive IDs (e.g., "1,2,3")
     * @return number of locomotives successfully assigned
     * @throws SQLException if an error occurs during the operation
     */
    int assembleTrainComposition(Integer serviceId, String locomotiveIds) throws SQLException;

    /**
     * Validates a train composition to ensure it meets all technical requirements.
     * Calls PL/SQL function fn_validate_train_composition.
     *
     * @param serviceId the train service ID
     * @return validation result message ("VALID" or error description)
     * @throws SQLException if an error occurs during the operation
     */
    String validateTrainComposition(Integer serviceId) throws SQLException;
}

