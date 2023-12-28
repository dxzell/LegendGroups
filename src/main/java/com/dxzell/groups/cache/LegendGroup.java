package com.dxzell.groups.cache;

import java.util.ArrayList;
import java.util.List;

public class LegendGroup { //class for playlegend group objects

    private String name;
    private String prefix;
    private int priority; //the lower, the higher in tablist
    private List<String> permissions = new ArrayList<>();

    public LegendGroup(String name, int priority, String prefix) {
        this.name = name;
        this.prefix = prefix;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }

        if(!(obj instanceof LegendGroup)) {
           return false;
        }
        LegendGroup group = (LegendGroup) obj;
        return group.getName().equalsIgnoreCase(this.getName());
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public List<String> getPermissions() {
        return permissions;
    }
}
