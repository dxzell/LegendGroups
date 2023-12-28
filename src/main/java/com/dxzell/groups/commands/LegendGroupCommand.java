package com.dxzell.groups.commands;

import com.dxzell.groups.cache.LegendManager;
import com.dxzell.groups.configs.MessagesConfig;
import com.dxzell.groups.database.DatabaseConnection;
import com.dxzell.groups.database.DatabaseUpdate;
import com.dxzell.groups.database.SqlStatements;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LegendGroupCommand implements CommandExecutor {

    private MessagesConfig messagesConfig;

    public LegendGroupCommand(MessagesConfig messagesConfig) {
        this.messagesConfig = messagesConfig;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (command.getName().equalsIgnoreCase("legendgroup") && args.length > 0 && DatabaseConnection.isConnected()) {
                switch (args[0]) {
                    case "create" -> { // /legendgroup create <name> <priority> <prefix>
                        if (args.length == 4) {
                            try {
                                int priority = Integer.parseInt(args[2]);
                                DatabaseUpdate.runAsync(() -> {
                                    if (!LegendManager.groupExists(args[1])) {
                                        SqlStatements.addGroupToGroupsTable(args[1], priority, args[3]); //database update
                                        LegendManager.addGroup(args[1], priority, args[3]); //cache update
                                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', messagesConfig.getSuccessfullyCreatedGroup(args[1])));
                                    } else {
                                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', messagesConfig.getGroupNameExistsAlready()));
                                    }
                                });
                            } catch (NumberFormatException e) {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', messagesConfig.getPriorityNoNumber()));
                            }
                        } else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', messagesConfig.getWrongCreateCommand()));
                        }
                    }

                    case "add" -> { // /legendgroup add <playerName> <groupName> oder /legendgroup add <playerName> <groupName> <days> <minutes> <seconds>
                        if (args.length == 3 || args.length == 6) {
                            Player addPlayer = Bukkit.getServer().getPlayer(args[1]);
                            String groupName = args[2];
                            if (addPlayer != null) { //player online/exists
                                DatabaseUpdate.runAsync(() -> {
                                    if (LegendManager.groupExists(groupName)) { //group exists
                                        if (args.length == 3) { //permanent
                                            SqlStatements.addPlayerToGroup(addPlayer.getUniqueId(), groupName, 0); //database update
                                            LegendManager.changePlayerGroup(addPlayer.getUniqueId(), groupName, 0); //cache update
                                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', messagesConfig.getSuccessfullyAddedPlayerToGroup(groupName)));
                                        } else { //temporary
                                            try {
                                                int days = Integer.parseInt(args[3]);
                                                int minutes = Integer.parseInt(args[4]);
                                                int seconds = Integer.parseInt(args[5]);
                                                long millis = ((days * 86400000) + (minutes * 60000) + (seconds * 1000)) + System.currentTimeMillis();
                                                SqlStatements.addPlayerToGroup(addPlayer.getUniqueId(), groupName, millis); //database update
                                                LegendManager.changePlayerGroup(addPlayer.getUniqueId(), groupName, millis); //cache update
                                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', messagesConfig.getSuccessfullyAddedPlayerToGroupTemporary(groupName, days + "d " + minutes + "m " + seconds + "s")));
                                            } catch (NumberFormatException e) {
                                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', messagesConfig.getTimeNoNumbers()));
                                            }
                                        }
                                    } else {
                                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', messagesConfig.getGroupDoesNotExist()));
                                    }
                                });
                            } else {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', messagesConfig.getPlayerCurrentlyOffline()));
                            }
                        } else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', messagesConfig.getWrongAddPlayerCommand()));
                        }
                    }

                    case "delete" -> { // /legendgroup delete <groupName>
                        if (args.length == 2) {
                            String groupName = args[1];
                            if (LegendManager.groupExists(groupName)) {
                                DatabaseUpdate.runAsync(() -> {
                                    SqlStatements.removeGroupFromGroups(groupName);
                                    SqlStatements.getAllPlayersFromGroup(groupName).forEach(uuid -> SqlStatements.removePlayerFromGroup(uuid.toString()));
                                    LegendManager.removePlayersFromGroup(groupName);
                                    LegendManager.removeGroup(groupName);
                                });
                            } else {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', messagesConfig.getGroupDoesNotExist()));
                            }
                        }
                    }

                    case "set" -> { // /legendgroup set default <groupName>
                        if (args.length == 3 && args[1].equalsIgnoreCase("default")) {
                            String groupName = args[2];
                            if (LegendManager.groupExists(groupName)) {
                                messagesConfig.setDefaultGroup(groupName);
                            } else {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', messagesConfig.getGroupDoesNotExist()));
                            }
                        } else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', messagesConfig.getWrongSetDefaultCommand()));
                        }
                    }
                }
            }
        }
        return false;
    }
}
