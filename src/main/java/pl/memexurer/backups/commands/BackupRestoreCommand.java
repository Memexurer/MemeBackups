package pl.memexurer.backups.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pl.memexurer.backups.MemeBackupsPlugin;
import pl.memexurer.backups.data.PlayerBackup;
import pl.memexurer.backups.data.PlayerBackupData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;

public class BackupRestoreCommand implements CommandExecutor {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd HH:mm");

    private PlayerBackupData backupData;

    public BackupRestoreCommand(PlayerBackupData data) {
        this.backupData = data;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("memebackups.restore")) {
            sender.sendMessage(ChatColor.RED + "Nie posiadasz wystarczajacych permisji do uzycia tej komendy.");
            return true;
        }

        if(args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Poprawne uzycie: /" + label + " (nick gracza)");
            return true;
        }

        Optional<PlayerBackup> backup = backupData.findBackupByName(args[0]);
        if(!backup.isPresent()) {
            sender.sendMessage(ChatColor.RED + "Nie znaleziono gracza!");
            return true;
        }

        backup.get().restoreBackup().sendMessage(ChatColor.GREEN + "Wczytano backup z " + getTime(backup.get().getBackupTime()) + "!");
        sender.sendMessage(ChatColor.GREEN + "Wczytano backup gracza " + args[0] + " z " + getTime(backup.get().getBackupTime()) + "!");
        return true;
    }

    private String getTime(long time) {
        return DATE_FORMAT.format(new Date(time));
    }
}
