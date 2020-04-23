package pl.memexurer.backups;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import pl.memexurer.backups.commands.BackupRestoreCommand;
import pl.memexurer.backups.config.impl.PluginConfiguration;
import pl.memexurer.backups.data.PlayerBackupData;
import pl.memexurer.backups.utils.ChatUtil;
import pl.memexurer.database.DatabaseCredentials;
import pl.memexurer.database.PluginDatabaseConnection;

import java.io.File;

public final class MemeBackupsPlugin extends JavaPlugin implements Listener {
    private final PluginConfiguration configuration = new PluginConfiguration(this);
    private PlayerBackupData backupData;

    @Override
    public void onEnable() {
        if (!(new File(getDataFolder(), "config.yml").exists())) saveResource("config.yml", false);
        configuration.load();

        DatabaseCredentials credentials = new DatabaseCredentials(configuration.DATABASE_CREDITENTIALS);
        PluginDatabaseConnection databaseConnection = PluginDatabaseConnection.findDatabaseService(credentials, this);
        System.out.println("ta" + databaseConnection + "\nda" + credentials);
        if (configuration.ENABLE_SAVE_TASK) Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            Bukkit.broadcastMessage(ChatUtil.fixColor(configuration.SAVE_TASK_START_MESSAGE));
            for (Player p : Bukkit.getOnlinePlayers()) backupData.getBackup(p).createBackup();
            Bukkit.broadcastMessage(ChatUtil.fixColor(configuration.SAVE_TASK_END_MESSAGE));
        }, configuration.SAVE_TASK_INTERVAL * 1200, configuration.SAVE_TASK_INTERVAL * 1200);

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, () -> {
            try {
                backupData.savePlayers();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, configuration.DATA_SAVE_INTERVAL * 1200, configuration.DATA_SAVE_INTERVAL * 1200);

        Bukkit.getPluginManager().registerEvent(PlayerDeathEvent.class, this, EventPriority.NORMAL, (listener, event) -> backupData.getBackup(((PlayerDeathEvent) event).getEntity()).createBackup(), this);
        getCommand("backup").setExecutor(new BackupRestoreCommand(backupData));

        this.backupData = new PlayerBackupData(databaseConnection);
        this.backupData.load();
    }

    @Override
    public void onDisable() {
        try {
            backupData.savePlayers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
