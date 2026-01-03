package app.db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages database connections to Oracle XEPDB1 for the freight management system.
 * This class implements the Singleton pattern to ensure only one database connection exists.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;
    
    // Oracle XEPDB1 Configuration
    // Using service name format (/) instead of SID format (:)
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521/XEPDB1";

    // IMPORTANT: Use the user that OWNS the tables (LOCOMOTIVE, WAGON, etc.)
    // Common usernames: "system", "freight_user", "g113", "admin", etc.
    // You can check the owner by running in SQL*Plus: SELECT OWNER FROM ALL_TABLES WHERE TABLE_NAME = 'LOCOMOTIVE';
    private static final String DB_USER = "APPUSER"; // Using SYSTEM user to access APPUSER schema
    private static final String DB_PASSWORD = "admin"; // SYSTEM password

    // If you're connecting as a different user, specify the schema owner here
    // Example: If tables are owned by "freight_user" but you connect as "system", set this to "freight_user"
    // Leave as empty string ("") if DB_USER is the schema owner
    public static final String SCHEMA_OWNER = "APPUSER"; // Tables are owned by APPUSER schema

    /**
     * Private constructor to prevent instantiation.
     * Initializes the database connection to Oracle.
     */
    private DatabaseConnection() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            connection.setAutoCommit(true);
            
            System.out.println("✓ Connection to Oracle XEPDB1 established successfully!");


        } catch (ClassNotFoundException e) {
            System.err.println("✗ Oracle JDBC Driver not found: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("✗ Failed to connect to Oracle database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gets the singleton instance of DatabaseConnection.
     *
     * @return the singleton instance
     */
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    /**
     * Gets the active database connection.
     *
     * @return the database connection
     */
    public Connection getConnection() {
        try {
            // Check if connection is still valid, reconnect if needed
            if (connection == null || connection.isClosed()) {
                instance = new DatabaseConnection();
            }
        } catch (SQLException e) {
            System.err.println("Error checking connection state: " + e.getMessage());
        }
        return connection;
    }

    /**
     * Initializes the database schema by executing the schema.sql file.
     */
    private void initializeDatabase() {
        String schemaPath = "../sql-scripts/USBD03/schema.sql";
        String dataPath = "../sql-scripts/USBD04/data.sql";
        
        try {
            // Execute schema.sql if file exists
            if (Files.exists(Paths.get(schemaPath))) {
                System.out.println("Executing schema.sql...");
                executeSQLFile(schemaPath);
                System.out.println("✓ Schema initialized successfully!");
            }
            
            // Execute data.sql if file exists (commented - uncomment if you want to load data)
            /*
            if (Files.exists(Paths.get(dataPath))) {
                System.out.println("Executing data.sql...");
                executeSQLFile(dataPath);
                System.out.println("✓ Sample data loaded successfully!");
            }
            */
            
        } catch (Exception e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Executes a SQL file.
     *
     * @param filePath the path to the SQL file
     * @throws IOException if file reading fails
     * @throws SQLException if SQL execution fails
     */
    private void executeSQLFile(String filePath) throws IOException, SQLException {
        StringBuilder sql = new StringBuilder();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Skip comments and empty lines
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--")) {
                    continue;
                }
                sql.append(line).append(" ");
            }
        }
        
        // Split by semicolon and execute each statement
        String[] statements = sql.toString().split(";");
        
        try (Statement stmt = connection.createStatement()) {
            for (String statement : statements) {
                statement = statement.trim();
                if (!statement.isEmpty()) {
                    try {
                        stmt.execute(statement);
                    } catch (SQLException e) {
                        // Ignore table/object already exists errors
                        if (!e.getMessage().contains("already exists") && 
                            !e.getMessage().contains("ORA-00955")) {
                            System.err.println("Error executing: " + statement.substring(0, Math.min(100, statement.length())));
                            System.err.println("Error: " + e.getMessage());
                        }
                    }
                }
            }
        }
    }

    /**
     * Closes the database connection.
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Database connection closed successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Tests the database connection.
     *
     * @return true if connection is valid, false otherwise
     */
    public boolean testConnection() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(5);
        } catch (SQLException e) {
            return false;
        }
    }
}
