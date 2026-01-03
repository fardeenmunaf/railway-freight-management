package app.db.dao;

import app.db.DatabaseConnection;

import java.sql.Connection;

/**
 * Base DAO implementation providing common functionality for all DAOs.
 * All DAO implementations should extend this class to get schema-aware table naming.
 *
 * @author G113 2DK
 * @version 1.0
 */
public abstract class BaseDAOImpl {

    protected final Connection connection;

    /**
     * Constructor that initializes the database connection.
     */
    protected BaseDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Gets the fully qualified table name with schema prefix if needed.
     * This method ensures all SQL queries use the correct schema owner.
     *
     * @param tableName the table name (e.g., "LOCOMOTIVE", "WAGON")
     * @return the fully qualified table name (e.g., "APPUSER.LOCOMOTIVE" or just "LOCOMOTIVE")
     */
    protected String getTableName(String tableName) {
        String schema = DatabaseConnection.SCHEMA_OWNER;
        if (schema != null && !schema.trim().isEmpty()) {
            return schema + "." + tableName;
        }
        return tableName;
    }

    /**
     * Wraps a table name in the SQL query with the appropriate schema prefix.
     * Convenience method for use in SQL string building.
     *
     * @param tableName the table name
     * @return the schema-qualified table name
     */
    protected String table(String tableName) {
        return getTableName(tableName);
    }
}

