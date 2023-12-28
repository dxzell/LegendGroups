package com.dxzell.groups.database;

import com.dxzell.groups.cache.LegendGroup;
import com.dxzell.groups.cache.LegendPlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SqlStatements {

    public static void createPlayerTable() { //table for every player
        updateStatement("CREATE TABLE IF NOT EXISTS player_data (uuid VARCHAR(50), current_group_name VARCHAR(50), expire_millis BIGINT, PRIMARY KEY(uuid));");
    }

    public static void createGroupTable(String groupName) { //table for each group with all permissions
        updateStatement("CREATE TABLE IF NOT EXISTS " + groupName +
                " (permissions VARCHAR(50));");
    }

    public static void createGroupsTable() { //table for every group
        updateStatement("CREATE TABLE IF NOT EXISTS groups (name VARCHAR(50), priority INTEGER, prefix VARCHAR(50), PRIMARY KEY(name));");
    }

    public static void addGroupToGroupsTable(String groupName, int priority, String prefix) {
        updateStatement("INSERT INTO groups (name, priority, prefix) VALUES ('" + groupName + "'," + priority + ", '" + prefix + "');");
        createGroupTable(groupName);
    }

    public static void removePrefix(String groupName) {
        updateStatement("UPDATE groups SET prefix = '' WHERE name = '" + groupName + "';");
    }

    public static void setPrefix(String prefix, String groupName) {
        updateStatement("UPDATE groups SET prefix = '" + prefix + "' WHERE name = '" + groupName + "';");
    }

    public static void removeGroupFromGroups(String groupName) {
        updateStatement("DROP TABLE IF EXISTS " + groupName.toLowerCase());
        updateStatement("DELETE FROM groups WHERE name = '" + groupName + "';");
    }

    public static boolean groupNameExistsAlready(String groupName) {
        ResultSet set = queryStatement("SELECT * FROM groups WHERE name = '" + groupName + "';");
        try {
            return set.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<UUID> getAllPlayersFromGroup(String groupName) {
        List<UUID> players = new ArrayList<>();
        ResultSet set = queryStatement("SELECT * FROM player_data WHERE current_group_name = '" + groupName + "';");
        try {
            while (set.next()) {
                players.add(UUID.fromString(set.getString("uuid")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return players;
    }

    public static void addPlayerToData(UUID playerUUID) {
        updateStatement("INSERT INTO player_data (uuid, current_group_name, expire_millis) VALUES ('" + playerUUID + "', '', 0);");
    }

    public static void addPlayerToGroup(UUID playerUUID, String groupName, long expireMillis) {
        updateStatement("UPDATE player_data SET current_group_name = '" + groupName + "', expire_millis = " + expireMillis + " WHERE uuid = '" + playerUUID + "'");
    }

    public static void removePermission(String groupName, String permission) {
        updateStatement("DELETE FROM " + groupName.toLowerCase() + " WHERE permissions = '" + permission + "'");
    }

    public static void removePlayerFromGroup(String playerUUID) {
        updateStatement("UPDATE player_data SET current_group_name = '' WHERE uuid = '" + playerUUID + "';");
    }

    public static int getGroupNumberByName(String groupName) {
        ResultSet set = queryStatement("SELECT groupNumber FROM groups WHERE name = '" + groupName + "';");
        try {
            set.next();
            return set.getInt("groupNumber");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean groupExists(String groupName) {
        ResultSet set = queryStatement("SELECT * FROM groups WHERE name = '" + groupName + "';");
        try {
            return set.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean playerExists(UUID uuid) {
        ResultSet set = queryStatement("SELECT uuid FROM player_data WHERE uuid = '" + uuid.toString() + "';");
        try {
            return set.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static String getPlayersGroup(UUID uuid) {
        ResultSet set = queryStatement("SELECT current_group_name FROM player_data WHERE uuid = '" + uuid.toString() + "';");
        try {
            set.next();
            return set.getString("current_group_name");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static void setPlayerGroup(UUID uuid, String groupName) {
        updateStatement("UPDATE player_data SET current_group_name = '" + groupName + "' WHERE uuid = '" + uuid + "';");
    }

    public static void addGroupPermission(String groupName, String permission) {
        updateStatement("INSERT INTO " + groupName + " (permissions) VALUES ('" + permission + "');");
    }

    public static List<String> getPermissions(String groupName) {
        List<String> permissions = new ArrayList<>();
        ResultSet set = queryStatement("SELECT permissions FROM " + groupName + ";");
        try {
            while (set.next()) {
                permissions.add(set.getString("permissions"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return permissions;
    }

    public static String getPrefix(String groupName) {
        ResultSet set = queryStatement("SELECT prefix FROM groups WHERE name = '" + groupName + "';");
        try {
            set.next();
            return set.getString("prefix");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<LegendGroup> loadGroups() {
        if(DatabaseConnection.isConnected()) {
            try {
                List<LegendGroup> groups = new ArrayList<>();
                PreparedStatement preparedStatement = DatabaseConnection.getConnection().prepareStatement(
                        "SELECT * FROM groups"
                );
                ResultSet set = preparedStatement.executeQuery();
                while (set.next()) {
                    groups.add(new LegendGroup(set.getString("name"), set.getInt("priority"), set.getString("prefix")));
                }
                return groups;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public static LegendPlayer getLegendPlayer(UUID uuid) {
        ResultSet set = queryStatement("SELECT * FROM player_data WHERE uuid = '" + uuid + "';");
        try {
            set.next();
            return new LegendPlayer(set.getString("current_group_name"), set.getLong("expire_millis"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void updateStatement(String sql) { //creates statement for table updates
        if (DatabaseConnection.isConnected()) {
            try {
                PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql);
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static ResultSet queryStatement(String sql) { //creates statement for table queries
        if (DatabaseConnection.isConnected()) {
            try {
                PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql);
                return statement.executeQuery();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            return null;
        }
    }
}
