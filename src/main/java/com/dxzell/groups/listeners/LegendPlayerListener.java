package com.dxzell.groups.listeners;

import com.dxzell.groups.cache.LegendManager;
import com.dxzell.groups.database.DatabaseConnection;
import com.dxzell.groups.database.DatabaseUpdate;
import com.dxzell.groups.database.SqlStatements;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class LegendPlayerListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if(DatabaseConnection.isConnected()) {
            DatabaseUpdate.runAsync(() -> { //non blocking load
                if (!SqlStatements.playerExists(e.getPlayer().getUniqueId()))
                    SqlStatements.addPlayerToData(e.getPlayer().getUniqueId()); //update database
                LegendManager.addPlayer(e.getPlayer().getUniqueId()); //update cache
            });
        }
    }
}
