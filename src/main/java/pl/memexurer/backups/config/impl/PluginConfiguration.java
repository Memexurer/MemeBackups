package pl.memexurer.backups.config.impl;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import pl.memexurer.backups.config.ConfigurationSource;
import pl.memexurer.backups.config.CustomConfiguration;

public class PluginConfiguration extends CustomConfiguration {
    public PluginConfiguration(JavaPlugin plugin) {
        super(plugin);
    }

    @ConfigurationSource(path = "database")
    public ConfigurationSection DATABASE_CREDITENTIALS;

    @ConfigurationSource(path = "savetask.enabled")
    public boolean ENABLE_SAVE_TASK;

    @ConfigurationSource(path = "savetask.interval")
    public int SAVE_TASK_INTERVAL;

    @ConfigurationSource(path = "savetask.message.start")
    public String SAVE_TASK_START_MESSAGE;

    @ConfigurationSource(path = "savetask.message.end")
    public String SAVE_TASK_END_MESSAGE;

    @ConfigurationSource(path = "data_save_interval")
    public int DATA_SAVE_INTERVAL;
}
