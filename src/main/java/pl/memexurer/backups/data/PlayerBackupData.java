package pl.memexurer.backups.data;

import org.bukkit.entity.Player;
import pl.memexurer.backups.utils.InventoryUtils;
import pl.memexurer.database.PluginDatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerBackupData {
    private final PluginDatabaseConnection databaseConnection;
    private final ConcurrentHashMap<UUID, PlayerBackup> backupMap;

    public PlayerBackupData(PluginDatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
        this.backupMap = new ConcurrentHashMap<>();
    }

    public void load() {
        this.createTable();
        ResultSet set = databaseConnection.query("SELECT * FROM `Backups`");
        while(true) {
            try {
                if (!set.next()) break;
            } catch (SQLException e) {
                e.printStackTrace();
            }

            PlayerBackup backup = new PlayerBackup(set);
            backupMap.put(backup.getPlayerUniqueId(), backup);
        }
    }

    public PlayerBackup getBackup(Player player) {
        return backupMap.computeIfAbsent(player.getUniqueId(), unused -> new PlayerBackup(player));
    }

    public Optional<PlayerBackup> findBackupByName(String playerName) {
        return backupMap.values().stream().filter(backup -> backup.getPlayerName().equalsIgnoreCase(playerName)).findAny();
    }

    private void createTable() {
        databaseConnection.update("CREATE TABLE IF NOT EXISTS `Backups` (PlayerName varchar(16), PlayerUniqueId varchar(36), BackupTime bigint(10), BackupInventoryContents blob, BackupArmorContents blob);");
    }

    public void savePlayers() throws Exception{
        PreparedStatement insertStatement = databaseConnection.getConnection().prepareStatement("INSERT INTO `Backups` (PlayerName, PlayerUniqueId, BackupTime, BackupInventoryContents, BackupArmorContents) VALUES (?, ?, ?, ?, ?);");
        PreparedStatement updateStatement = databaseConnection.getConnection().prepareStatement("UPDATE `Backups` SET BackupTime=?, BackupInventoryContents=?, BackupArmorContents=? WHERE PlayerUniqueId=?;");

        for(PlayerBackup backup: backupMap.values()) {
            if(backup.isNeedInsert()) {
                insertStatement.setString(1, backup.getPlayerName());
                insertStatement.setString(2, backup.getPlayerUniqueId().toString());
                insertStatement.setLong(3, backup.getBackupTime());
                insertStatement.setBytes(4, InventoryUtils.getInventoryBytes(backup.getContents()));
                insertStatement.setBytes(5, InventoryUtils.getInventoryBytes(backup.getArmorContents()));
                insertStatement.addBatch();
            } else if(backup.isNeedUpdate()) {
                updateStatement.setLong(1, backup.getBackupTime());
                updateStatement.setBytes(2, InventoryUtils.getInventoryBytes(backup.getContents()));
                updateStatement.setBytes(3, InventoryUtils.getInventoryBytes(backup.getArmorContents()));
                updateStatement.setString(4, backup.getPlayerUniqueId().toString());
                updateStatement.addBatch();
            }
        }

        insertStatement.executeBatch();
        updateStatement.executeBatch();
    }
}
