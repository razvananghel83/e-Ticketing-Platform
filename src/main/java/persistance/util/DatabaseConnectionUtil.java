package persistance.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConnectionUtil {

    private static final String POSTGRESQL_JDBC_DRIVER = "org.postgresql.Driver";
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/eticketing_db";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "123456";

    private static volatile DatabaseConnectionUtil instance;
    private Connection connection;

    public static Connection getDatabaseConnection() {
        if (instance == null) {
            synchronized (DatabaseConnectionUtil.class) {
                if (instance == null) {
                    instance = new DatabaseConnectionUtil();
                }
            }
        }
        return instance.connection;
    }


    private DatabaseConnectionUtil() {
        try {
            Class.forName(POSTGRESQL_JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            if (connection != null && !connection.isClosed()) {
                System.out.println("PostgreSQL connection established successfully");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Failed to connect to PostgreSQL database: " + e.getMessage());
        }
    }

    public static void closeConnection() {
        if (instance != null && instance.connection != null) {
            try {
                instance.connection.close();
                System.out.println("Database connection closed");
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
}