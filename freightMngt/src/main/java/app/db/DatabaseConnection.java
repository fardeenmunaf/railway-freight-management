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
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:XEPDB1";
    private static final String DB_USER = "system"; // Altere para seu usuário
    private static final String DB_PASSWORD = "oracle"; // Altere para sua senha

    /**
     * Private constructor to prevent instantiation.
     * Initializes the database connection to Oracle.
     */
    private DatabaseConnection() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            connection.setAutoCommit(true);
            
            System.out.println("✓ Conexão com Oracle XEPDB1 estabelecida com sucesso!");
            
            // Initialize database schema if needed
            initializeDatabase();
            
        } catch (ClassNotFoundException e) {
            System.err.println("✗ Driver Oracle JDBC não encontrado: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("✗ Falha ao conectar à base de dados Oracle: " + e.getMessage());
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
            System.err.println("Erro ao verificar estado da conexão: " + e.getMessage());
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
                System.out.println("Executando schema.sql...");
                executeSQLFile(schemaPath);
                System.out.println("✓ Schema inicializado com sucesso!");
            }
            
            // Execute data.sql if file exists (comentado - descomente se quiser carregar dados)
            /*
            if (Files.exists(Paths.get(dataPath))) {
                System.out.println("Executando data.sql...");
                executeSQLFile(dataPath);
                System.out.println("✓ Dados de exemplo carregados com sucesso!");
            }
            */
            
        } catch (Exception e) {
            System.err.println("Erro ao inicializar base de dados: " + e.getMessage());
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
                            System.err.println("Erro ao executar: " + statement.substring(0, Math.min(100, statement.length())));
                            System.err.println("Erro: " + e.getMessage());
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
                System.out.println("✓ Conexão com a base de dados encerrada com sucesso!");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
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
