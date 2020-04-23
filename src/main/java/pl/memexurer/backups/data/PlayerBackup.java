package pl.memexurer.backups.data;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import pl.memexurer.backups.utils.InventoryUtils;

import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.UUID;

public class PlayerBackup {
    private String playerName;
    private UUID playerUniqueId;
    private long backupTime;
    private ItemStack[] contents;
    private ItemStack[] armorContents;

    private boolean needInsert;
    private boolean needUpdate;

    public PlayerBackup(ResultSet set) {
        try {
            this.playerName = set.getString("PlayerName");
            this.playerUniqueId = UUID.fromString(set.getString("PlayerUniqueId"));
            this.backupTime = set.getLong("BackupTime");
            this.contents = InventoryUtils.readItemStacks(set.getBytes("BackupInventoryContents"));
            this.armorContents = InventoryUtils.readItemStacks(set.getBytes("BackupArmorContents"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public PlayerBackup(Player player) {
        this.playerName = player.getName();
        this.playerUniqueId = player.getUniqueId();
        this.needInsert = true;
    }

    public boolean isNeedInsert() {
        return needInsert;
    }

    public boolean isNeedUpdate() {
        return needUpdate;
    }

    public String getPlayerName() {
        return playerName;
    }

    public UUID getPlayerUniqueId() {
        return playerUniqueId;
    }

    public ItemStack[] getContents() {
        return contents;
    }

    public ItemStack[] getArmorContents() {
        return armorContents;
    }

    public long getBackupTime() {
        return backupTime;
    }

    public void createBackup() {
        this.backupTime = System.currentTimeMillis();
        Player player = Bukkit.getPlayer(playerUniqueId);
        this.contents = InventoryUtils.cloneItemStacks(player.getInventory().getContents());
        this.armorContents = InventoryUtils.cloneItemStacks(player.getInventory().getArmorContents());
        this.needUpdate = true;
    }

    public Player restoreBackup() {
        Player player = Bukkit.getPlayer(playerUniqueId);

        player.getInventory().setContents(contents);
        player.getInventory().setArmorContents(armorContents);

        return player;
    }
}
