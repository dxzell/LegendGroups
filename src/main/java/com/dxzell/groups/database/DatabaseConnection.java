package com.dxzell.groups.database;

import com.dxzell.groups.Groups;
import com.dxzell.groups.configs.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class DatabaseConnection {

    private static Connection connection;

    public static void connect(Groups main) throws SQLException { //creates the database connection
        DatabaseConfig databaseConfig = main.getDatabaseConfig();
        connection = DriverManager.getConnection(
                "jdbc:mysql://" + databaseConfig.getHost() + ":" + databaseConfig.getPort() + "/" + databaseConfig.getDatabase(),
                databaseConfig.getUsername(),
                databaseConfig.getPassword());

        //create tables if not existing already
        SqlStatements.createPlayerTable();
        SqlStatements.createGroupsTable();
    }

    public static boolean isConnected() { //checks connection status
        return connection != null;
    }

    public static void disconnect() { //disconnects from database
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Connection getConnection() {
        return connection;
    }

}
