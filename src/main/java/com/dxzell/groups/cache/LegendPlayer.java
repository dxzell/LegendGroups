package com.dxzell.groups.cache;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class LegendPlayer { //class for playlegend player objects

    private String currentGroupName;
    private long remainingTime;

    public LegendPlayer(String currentGroupName, long remainingTime) {
        this.currentGroupName = currentGroupName;
        this.remainingTime = remainingTime == 0 ? 0 : (remainingTime - System.currentTimeMillis());
    }

    public String getCurrentGroupName() {
        return currentGroupName;
    }

    public void setCurrentGroupName(String currentGroupName, LegendGroup group, Player player) {
        this.currentGroupName = currentGroupName;

        Scoreboard scoreboard = player.getScoreboard();
        Team players;
        if (group != null) {
            int priority = group.getPriority();
            players = scoreboard.getTeam(priority + "");
            if (players == null) {
                players = scoreboard.registerNewTeam(priority + "");
            }
            players.addEntry(player.getName());
            players.setPrefix(ChatColor.translateAlternateColorCodes('&', group.getPrefix()) + " ");
        } else {
            players = scoreboard.getTeam("");
            if (players == null) {
                players = scoreboard.registerNewTeam("");
            }
            players.addEntry(player.getName());
            players.setPrefix("");
        }
    }

    public long getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(long remainingTime, boolean reset) {
        this.remainingTime = reset ? (remainingTime == 0 ? 0 : (remainingTime - System.currentTimeMillis())) : remainingTime;
    }
}
