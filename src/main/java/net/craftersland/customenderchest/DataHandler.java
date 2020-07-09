package net.craftersland.customenderchest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class DataHandler {
	
	private Map<UUID, Inventory> liveData = new HashMap<UUID, Inventory>();
	private Set<Player> joinDelay = new HashSet<Player>();
	private EnderChest pl;
	
	public DataHandler(EnderChest plugin) {
		this.pl = plugin;
		loadAlreadyOnlinePlayers();
	}
	
	public void addJoinDelay(Player p) {
		joinDelay.add(p);
	}
	
	public void removeJoinDelay(Player p) {
		joinDelay.remove(p);
	}
	
	public boolean hasJoinDelay(Player p) {
		return joinDelay.contains(p);
	}
	
	public Inventory getData(UUID playerUUID) {
		return liveData.get(playerUUID);
	}
	
	public void setData(UUID playerUUID, Inventory enderchestInventory) {
		liveData.put(playerUUID, enderchestInventory);
	}
	
	public void removeData(UUID playerUUID) {
		liveData.remove(playerUUID);
	}
	
	public boolean isLiveEnderchest(Inventory inventory) {
		return liveData.containsValue(inventory);
	}
	
	public void clearLiveData() {
		liveData.clear();
	}
	
	public void loadPlayerFromStorage(Player p) {
		if (p.isOnline() == true) {
			int size = pl.getEnderChestUtils().getSize(p);
			if (size == 0) {
				size = 9;
			}
			String enderChestTitle = pl.getEnderChestUtils().getTitle(p);
			Inventory inv = Bukkit.getServer().createInventory(p, size, enderChestTitle);
			if (pl.getStorageInterface().hasDataFile(p.getUniqueId()) == true) {
				pl.getStorageInterface().loadEnderChest(p, inv);
			}
			setData(p.getUniqueId(), inv);
		}
	}
	
	private void loadAlreadyOnlinePlayers() {
		if (Bukkit.getOnlinePlayers().isEmpty() == false) {
			EnderChest.log.info("Loading data for online players...");
			for (Player p : Bukkit.getOnlinePlayers()) {
				loadPlayerFromStorage(p);
			}
		}
	}

}
