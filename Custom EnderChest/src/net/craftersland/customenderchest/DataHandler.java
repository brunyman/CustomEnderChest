package net.craftersland.customenderchest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.inventory.Inventory;

public class DataHandler {

	private Map<UUID, Inventory> liveData = new HashMap<UUID, Inventory>();

	public DataHandler() {
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

}
