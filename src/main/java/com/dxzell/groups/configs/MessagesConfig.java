package com.dxzell.groups.configs;

import com.dxzell.groups.Groups;

public class MessagesConfig extends Config{

    public MessagesConfig(Groups main) {
        super(main, "messages.yml");
    }

    public String getWrongCreateCommand() {
        return ymlFile.getString("wrongCreateCommand");
    }

    public String getPriorityNoNumber() {
        return ymlFile.getString("priorityNoNumber");
    }

    public String getGroupNameExistsAlready() {
        return ymlFile.getString("groupNameExistsAlready");
    }

    public String getWrongAddPlayerCommand() {
        return ymlFile.getString("wrongAddPlayerCommand");
    }

    public String getPlayerCurrentlyOffline() {
        return ymlFile.getString("playerNotOnline");
    }

    public String getTimeNoNumbers() {
        return ymlFile.getString("timeNoNumbers");
    }

    public String getGroupDoesNotExist() {
        return ymlFile.getString("groupDoesNotExist");
    }

    public String getDefaultGroup() {
        return ymlFile.getString("defaultGroup");
    }

    public void setDefaultGroup(String groupName) {
        ymlFile.set("defaultGroup", groupName);
        saveFile();
    }

    public String getWrongSetDefaultCommand() {
        return ymlFile.getString("wrongSetDefaultCommand");
    }

    public String getSuccessfullyCreatedGroup(String name) {
        return ymlFile.getString("successfullyCreatedGroup").replace("[name]", name);
    }

    public String getSuccessfullyAddedPlayerToGroup(String name) {
        return ymlFile.getString("successfullyAddedPlayerToGroup").replace("[name]", name);
    }

    public String getSuccessfullyAddedPlayerToGroupTemporary(String name, String time) {
        return ymlFile.getString("successfullyAddedPlayerToGroupTemporary").replace("[name]", name).replace("[time]", time);
    }

    public String getInfo(String name, String time) {
        return ymlFile.getString("info").replace("[name]", name.isEmpty() ? "No group" : name).replace("[time]", time);
    }
}
