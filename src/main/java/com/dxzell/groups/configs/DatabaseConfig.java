package com.dxzell.groups.configs;

import com.dxzell.groups.Groups;

public class DatabaseConfig extends Config {

    public DatabaseConfig(Groups main) {
        super(main, "database.yml");
    }

    public String getHost() {
        return ymlFile.getString("host");
    }

    public int getPort() {
        return ymlFile.getInt("port");
    }

    public String getDatabase() {
        return ymlFile.getString("database");
    }

    public String getUsername() {
        return ymlFile.getString("username");
    }

    public String getPassword() {
        return ymlFile.getString("password");
    }
}
