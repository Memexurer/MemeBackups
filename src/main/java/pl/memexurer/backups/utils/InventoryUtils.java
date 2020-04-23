package pl.memexurer.backups.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public final class InventoryUtils {
    public static byte[] getInventoryBytes(ItemStack[] items) throws IllegalStateException {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(stream);

            dataOutput.writeInt(items.length);
            for (ItemStack item : items) dataOutput.writeObject(item);

            dataOutput.close();

            return stream.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    public static ItemStack[] readItemStacks(byte[] bytes) throws IOException {
        try {
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(new ByteArrayInputStream(bytes));

            ItemStack[] items = new ItemStack[dataInput.readInt()];
            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return items;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }


    public static ItemStack[] cloneItemStacks(ItemStack[] contents) {
        ItemStack[] itemStacks = new ItemStack[contents.length];
        for(int i = 0; i < contents.length; i++) {
            if(contents[i] == null) continue;
            itemStacks[i] = new ItemStack(contents[i]);
        }
        return itemStacks;
    }
}
