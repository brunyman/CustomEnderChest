package net.craftersland.customenderchest.storage;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface StorageInterface {
	
	//Storage Flat File data methods
    public boolean hasDataFile(UUID playerUUID);
    public boolean deleteDataFile(UUID playerUUID);
	public boolean saveEnderChest(Player p, Inventory inv);
	public boolean saveEnderChest(UUID p, Inventory inv);
	public boolean loadEnderChest(Player p, Inventory inv);
	public boolean loadEnderChest(UUID playerUUID, Inventory inv);
	public String loadName(UUID playerUUID);
	public Integer loadSize(UUID playerUUID);
	//For import command only
	public void saveEnderChest(UUID uuid, Inventory endInv, String playerName, int invSize);

}
