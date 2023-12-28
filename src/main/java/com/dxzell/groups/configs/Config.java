package com.dxzell.groups.configs;

import com.dxzell.groups.Groups;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;

public abstract class Config {
    protected Groups main;
    private File file;
    protected YamlConfiguration ymlFile;

    public Config(Groups main, String fileName) { //blueprint for all configs
        file = new File(main.getDataFolder(), fileName);
        this.main = main;

        if (!main.getDataFolder().exists()) {
            main.getDataFolder().mkdirs();
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
                copyFromResources(main, fileName, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ymlFile = YamlConfiguration.loadConfiguration(file);
        saveFile();
    }

    protected void saveFile() { //saves file
        try {
            ymlFile.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void copyFromResources(Groups main, String fileName, File dest) {
        try (InputStream in = main.getResource(fileName)) {
            if (in == null) {
                throw new RuntimeException("Resource not found: " + fileName);
            }

            try (OutputStream out = new FileOutputStream(dest)) {
                byte[] buffer = new byte[1024];
                int length;

                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to copy resource", e);
        }
    }

    public YamlConfiguration getYmlFile() {
        return ymlFile;
    }
}
