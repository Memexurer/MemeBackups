package pl.memexurer.backups.config;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

public class CustomConfiguration {
    private File file;
    private FileConfiguration configuration;

    public CustomConfiguration(JavaPlugin plugin) {
        this.file = new File(plugin.getDataFolder(), "config.yml");
        if (!file.exists())
            plugin.saveResource("config.yml", false);
        try {
            this.configuration = YamlConfiguration.loadConfiguration( new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public File getFile() {
        return file;
    }

    public void load() {
        for (Field f : getClass().getDeclaredFields()) {
            if (!f.isAnnotationPresent(ConfigurationSource.class)) continue;

            try {
                ConfigurationSource source = f.getAnnotation(ConfigurationSource.class);
                f.set(this, configuration.get(source.path()));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public void save() {
        for (Field f : getClass().getDeclaredFields()) {
            if (!f.isAnnotationPresent(ConfigurationSource.class)) continue;

            try {
                configuration.set(f.getAnnotation(ConfigurationSource.class).path(), f.get(this));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
