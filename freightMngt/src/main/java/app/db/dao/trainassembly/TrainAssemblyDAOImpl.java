package app.db.dao.trainassembly;

import app.db.dao.BaseDAOImpl;

import java.sql.*;

/**
 * DAO implementation for Train Assembly operations in the Oracle database.
 * Implements train composition assembly and validation using PL/SQL functions.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class TrainAssemblyDAOImpl extends BaseDAOImpl implements TrainAssemblyDAO {

    /**
     * Constructor that initializes the database connection.
     */
    public TrainAssemblyDAOImpl() {
        super();
    }

    @Override
    public int assembleTrainComposition(Integer serviceId, String locomotiveIds) throws SQLException {
        String sql = "{? = call fn_assemble_train_composition(?, ?)}";

        try (CallableStatement cstmt = connection.prepareCall(sql)) {
            // Register OUT parameter for the return value
            cstmt.registerOutParameter(1, Types.INTEGER);
            // Set IN parameters
            cstmt.setInt(2, serviceId);
            cstmt.setString(3, locomotiveIds);

            // Execute the function
            cstmt.execute();

            // Get the result (number of locomotives assigned)
            return cstmt.getInt(1);
        }
    }

    @Override
    public String validateTrainComposition(Integer serviceId) throws SQLException {
        String sql = "{? = call fn_validate_train_composition(?)}";

        try (CallableStatement cstmt = connection.prepareCall(sql)) {
            // Register OUT parameter for the return value
            cstmt.registerOutParameter(1, Types.VARCHAR);
            // Set IN parameter
            cstmt.setInt(2, serviceId);

            // Execute the function
            cstmt.execute();

            // Get the result (validation message)
            return cstmt.getString(1);
        }
    }
}


