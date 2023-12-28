package com.dxzell.groups.cache;

import com.dxzell.groups.Groups;
import com.dxzell.groups.database.DatabaseUpdate;
import com.dxzell.groups.database.SqlStatements;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public abstract class LegendManager { //serves as a cache for group and player data

    private static HashMap<UUID, LegendPlayer> legendPlayers = new HashMap<>();
    private static List<LegendGroup> legendGroups = new ArrayList<>();
    private static Groups mainInstance;

    public static void loadGroups(Groups main) {
        legendGroups = SqlStatements.loadGroups();
        mainInstance = main;

        Bukkit.getScheduler().runTaskTimerAsynchronously(mainInstance, () -> {
          legendPlayers.keySet().forEach(key -> {
              LegendPlayer currentPlayer = legendPlayers.get(key);
              if(currentPlayer.getRemainingTime() != 0) { //player is in this group temporary
                  currentPlayer.setRemainingTime(currentPlayer.getRemainingTime() - 1000, false);
                  if(currentPlayer.getRemainingTime() < 0) { //time ran out
                      SqlStatements.setPlayerGroup(key, "");
                      legendPlayers.get(key).setCurrentGroupName("", getLegendGroup(""), Bukkit.getPlayer(key));
                      currentPlayer.setRemainingTime(0, false);
                  }
              }
          });
        }, 0L, 20L);
    }

    public static void addPlayer(UUID uuid) { //adds player to cache
        LegendPlayer legendPlayer = SqlStatements.getLegendPlayer(uuid);
        Player player = Bukkit.getServer().getPlayer(uuid);
        legendPlayer.setCurrentGroupName(legendPlayer.getCurrentGroupName(), getLegendGroup(legendPlayer.getCurrentGroupName()), player);
        legendPlayers.put(uuid, legendPlayer);

        //if player has no group and there is an existing default group, put him in the default group
        String defaultGroup = mainInstance.getMessagesConfig().getDefaultGroup();
        if(legendPlayer.getCurrentGroupName().isEmpty() && !defaultGroup.isEmpty() && groupExists(defaultGroup)) {
            DatabaseUpdate.runAsync(() -> {
            legendPlayer.setCurrentGroupName(defaultGroup, getLegendGroup(defaultGroup), player);
            SqlStatements.addPlayerToGroup(uuid, defaultGroup, 0);
            });
        }
    }

    public static void removeGroup(String groupName) {
        legendGroups.remove(getLegendGroup(groupName));
    }

    public static void addGroup(String name, int priotity, String prefix) { //adds group to cache
        legendGroups.add(new LegendGroup(name, priotity, prefix));
    }

    public static void changePlayerGroup(UUID uuid, String groupName, long expireMillis) { //update players current group
        LegendPlayer legendPlayer = legendPlayers.get(uuid);
        if (legendPlayer != null) {
            legendPlayer.setCurrentGroupName(groupName, getLegendGroup(groupName), Bukkit.getServer().getPlayer(uuid));
            legendPlayer.setRemainingTime(expireMillis, true);
        }
    }

    public static boolean groupExists(String groupName) { //checks if group exists
        return getLegendGroup(groupName) != null;
    }

    public static void removePlayersFromGroup(String groupName) { //removes all players from given group
        legendPlayers.keySet().forEach(uuid -> {
            LegendPlayer currentPlayer = legendPlayers.get(uuid);
            if (currentPlayer.getCurrentGroupName().equalsIgnoreCase(groupName)) {
                currentPlayer.setCurrentGroupName("", null, Bukkit.getPlayer(uuid));
            }
        });
    }

    public static void setPrefix(String groupName, String prefix) {
        LegendGroup group = getLegendGroup(groupName);
        if (group != null) {
            group.setPrefix(prefix);
        }
    }

    public static LegendGroup getLegendGroup(String groupName) {
        for (LegendGroup group : legendGroups) {
            if (group.getName().equalsIgnoreCase(groupName)) {
                return group;
            }
        }
        return null;
    }
}
