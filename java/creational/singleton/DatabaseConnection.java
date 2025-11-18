package creational.singleton;

import java.util.HashMap;
import java.util.Map;

/**
 * Real-world example: Database Connection Manager
 * Demonstrates practical use of Singleton pattern
 */
public class DatabaseConnection {
    private static volatile DatabaseConnection instance;
    private Map<String, String> connectionPool;
    private boolean connected;

    private DatabaseConnection() {
        System.out.println("Initializing database connection...");
        this.connectionPool = new HashMap<>();
        this.connected = false;
    }

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

    public void connect(String database, String credentials) {
        if (!connected) {
            connectionPool.put(database, credentials);
            connected = true;
            System.out.println("Connected to database: " + database);
        } else {
            System.out.println("Already connected to database");
        }
    }

    public void disconnect() {
        if (connected) {
            connectionPool.clear();
            connected = false;
            System.out.println("Disconnected from database");
        }
    }

    public void executeQuery(String query) {
        if (connected) {
            System.out.println("Executing query: " + query);
        } else {
            System.out.println("Error: Not connected to database");
        }
    }

    public boolean isConnected() {
        return connected;
    }
}
