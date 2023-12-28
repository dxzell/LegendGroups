package com.dxzell.groups;

import com.dxzell.groups.cache.LegendManager;
import com.dxzell.groups.commands.LegendGroupCommand;
import com.dxzell.groups.configs.DatabaseConfig;
import com.dxzell.groups.configs.MessagesConfig;
import com.dxzell.groups.database.DatabaseConnection;
import com.dxzell.groups.database.DatabaseUpdate;
import com.dxzell.groups.listeners.LegendPlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;

public final class Groups extends JavaPlugin {

    private DatabaseConfig databaseConfig = new DatabaseConfig(this);
    private MessagesConfig messagesConfig = new MessagesConfig(this);

    @Override
    public void onEnable() {
        //Database connection
        try {
            DatabaseConnection.connect(this);
            LegendManager.loadGroups(this);
        } catch (SQLException e) {
            System.out.println("Database connection failed.");
        }

        //Listeners
        Bukkit.getPluginManager().registerEvents(new LegendPlayerListener(), this);

        //Commands
        getCommand("legendgroup").setExecutor(new LegendGroupCommand(messagesConfig));
    }

    @Override
    public void onDisable() {
        DatabaseConnection.disconnect();
        DatabaseUpdate.stopExecutorService();
    }

    public DatabaseConfig getDatabaseConfig() {
        return databaseConfig;
    }

    public MessagesConfig getMessagesConfig() {
        return messagesConfig;
    }
}
