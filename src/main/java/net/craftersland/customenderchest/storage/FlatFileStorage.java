package net.craftersland.customenderchest.storage;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import net.craftersland.customenderchest.EnderChest;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class FlatFileStorage implements StorageInterface {
	
    private EnderChest enderchest;
	
	public FlatFileStorage(EnderChest enderchest) {
		this.enderchest = enderchest;
		
	}
	
	    //Check if player has a data file
		@Override
		public boolean hasDataFile(UUID playerUUID) {
			return (new File("plugins"+System.getProperty("file.separator")+"CustomEnderChest"+System.getProperty("file.separator")+"PlayerData"+System.getProperty("file.separator")+playerUUID+".yml")).exists();
		}
		
		//Create a data file if there is none
		public boolean createDataFile(UUID playerUUID, Player player) {
			try {
				File dataFile = new File("plugins"+System.getProperty("file.separator")+"CustomEnderChest"+System.getProperty("file.separator")+"PlayerData", playerUUID + ".yml");
				if (!dataFile.exists())	{
					dataFile.createNewFile();
					FileConfiguration ymlFormat = YamlConfiguration.loadConfiguration(dataFile);
	 			    String playerName = player.getName();
					ymlFormat.set("PlayerLastName", playerName);
					
					int slots = enderchest.getEnderChestUtils().getSize(player);
					ymlFormat.set("EnderChestSize", slots);
					ymlFormat.save(dataFile);
					
					return true;
				}
				
			} catch (Exception e) {
				enderchest.getLogger().severe("Could not create data file " + playerUUID + "!");
				e.printStackTrace();
			}
			return false;
		}
		
		//Save enderchest inventory data from name
		public boolean saveInventory(UUID playerUUID, Player player, Integer size, ItemStack inventory) {
			if (!hasDataFile(playerUUID)) {
				createDataFile(playerUUID, player);
			}
			
			try {
				File dataFile = new File("plugins"+System.getProperty("file.separator")+"CustomEnderChest"+System.getProperty("file.separator")+"PlayerData", playerUUID + ".yml");
				FileConfiguration ymlFormat = YamlConfiguration.loadConfiguration(dataFile);
				String playerName = player.getName();
				ymlFormat.set("PlayerLastName", playerName);
				
				int slots = enderchest.getEnderChestUtils().getSize(player);
				ymlFormat.set("EnderChestSize", slots);
				
				ymlFormat.set("EnderChestInventory." + size, inventory);
				
				ymlFormat.save(dataFile);
				return true;
				
			} catch (Exception e) {
				enderchest.getLogger().severe("Could not save inventory of "+playerUUID+"!");
				e.printStackTrace();
			}
			return false;
		}
		
		//Save enderchest inventory data from uuid
		public boolean saveInventory(UUID playerUUID, Integer size, ItemStack inventory) {
		
			try {
				File dataFile = new File("plugins"+System.getProperty("file.separator")+"CustomEnderChest"+System.getProperty("file.separator")+"PlayerData", playerUUID + ".yml");
				FileConfiguration ymlFormat = YamlConfiguration.loadConfiguration(dataFile);
				
				ymlFormat.set("EnderChestInventory." + size, inventory);
				
				ymlFormat.save(dataFile);
				return true;
				
			} catch (Exception e) {
				enderchest.getLogger().severe("Could not save inventory of "+playerUUID+"!");
				e.printStackTrace();
			}
			return false;
		}
		
		//Save enderchest inventory and all data from name
		@Override
		public boolean saveEnderChest(Player p, Inventory inv) {
			File dataFile = new File("plugins"+System.getProperty("file.separator")+"CustomEnderChest"+System.getProperty("file.separator")+"PlayerData", p.getUniqueId() + ".yml");
			if (dataFile.exists()) {
				int storageSize = loadSize(p.getUniqueId());
				if (storageSize == 0) return false;
				Inventory storageInv = Bukkit.getServer().createInventory(null, storageSize);
				FileConfiguration ymlFormat = YamlConfiguration.loadConfiguration(dataFile);
				ArrayList<ItemStack> items = new ArrayList<ItemStack>();
				for (int i = 0; i < storageSize; i++) {
					ItemStack item = ymlFormat.getItemStack("EnderChestInventory." + i);
					items.add(item);
				}
				ItemStack[] itemsList = (ItemStack[])items.toArray(new ItemStack[items.size()]);
				storageInv.setContents(itemsList);
				if (inv.getSize() >= storageSize) {
					for (int i = 0; i < inv.getSize(); i++) {
						ItemStack item = inv.getContents()[i];
						saveInventory(p.getUniqueId(), p, i, item);
					}
				} else {
					for (int i = 0; i < inv.getSize(); i++) {
						storageInv.setItem(i, inv.getItem(i));
					}
					for (int i = 0; i < storageInv.getSize(); i++) {
						ItemStack item = storageInv.getContents()[i];
						saveInventory(p.getUniqueId(), p, i, item);
					}
				}
			} else {
				for (int i = 0; i < inv.getSize(); i++) {
					ItemStack item = inv.getContents()[i];
					saveInventory(p.getUniqueId(), p, i, item);
				}
			}
			return true;
		}
		
		//Save enderchest inventory data only from uuid
		@Override
		public boolean saveEnderChest(UUID p, Inventory inv) {
			File dataFile = new File("plugins"+System.getProperty("file.separator")+"CustomEnderChest"+System.getProperty("file.separator")+"PlayerData", p + ".yml");
			if (dataFile.exists()) {
				int storageSize = loadSize(p);
				Inventory storageInv = Bukkit.getServer().createInventory(null, storageSize);
				FileConfiguration ymlFormat = YamlConfiguration.loadConfiguration(dataFile);
				ArrayList<ItemStack> items = new ArrayList<ItemStack>();
				for (int i = 0; i < storageSize; i++) {
					ItemStack item = ymlFormat.getItemStack("EnderChestInventory." + i);
					items.add(item);
				}
				ItemStack[] itemsList = (ItemStack[])items.toArray(new ItemStack[items.size()]);
				storageInv.setContents(itemsList);
				if (inv.getSize() >= storageSize) {
					for (int i = 0; i < inv.getSize(); i++) {
						ItemStack item = inv.getContents()[i];
						saveInventory(p, i, item);
					}
				} else {
					for (int i = 0; i < inv.getSize(); i++) {
						storageInv.setItem(i, inv.getItem(i));
					}
					for (int i = 0; i < storageInv.getSize(); i++) {
						ItemStack item = storageInv.getContents()[i];
						saveInventory(p, i, item);
					}
				}
			} else {
				for (int i = 0; i < inv.getSize(); i++) {
					ItemStack item = inv.getContents()[i];
					saveInventory(p, i, item);
				}
			}
			return true;
		}
		
		//load enderchest inventory data by name
		@Override
		public boolean loadEnderChest(Player p, Inventory inv){
			if (!hasDataFile(p.getUniqueId())) {
				createDataFile(p.getUniqueId(), p);
			}
			File dataFile = new File("plugins"+System.getProperty("file.separator")+"CustomEnderChest"+System.getProperty("file.separator")+"PlayerData", p.getUniqueId() + ".yml");
			FileConfiguration ymlFormat = YamlConfiguration.loadConfiguration(dataFile);
			ArrayList<ItemStack> items = new ArrayList<ItemStack>();
			for (int i = 0; i < inv.getSize(); i++) {
				ItemStack item = ymlFormat.getItemStack("EnderChestInventory." + i);
				items.add(item);
			}
			ItemStack[] itemsList = (ItemStack[])items.toArray(new ItemStack[items.size()]);
			inv.setContents(itemsList);
			items.clear();
			return true;
		}
		
		//load enderchest inventory data by uuid
		@Override
		public boolean loadEnderChest(UUID playerUUID, Inventory inv){
			File dataFile = new File("plugins"+System.getProperty("file.separator")+"CustomEnderChest"+System.getProperty("file.separator")+"PlayerData", playerUUID + ".yml");
			FileConfiguration ymlFormat = YamlConfiguration.loadConfiguration(dataFile);
			ArrayList<ItemStack> items = new ArrayList<ItemStack>();
			for (int i = 0; i < inv.getSize(); i++) {
				ItemStack item = ymlFormat.getItemStack("EnderChestInventory." + i);
				items.add(item);
			}
			ItemStack[] itemsList = (ItemStack[])items.toArray(new ItemStack[items.size()]);
			inv.setContents(itemsList);
			items.clear();
			return true;
		}
			
	    //load player name from storage file
		@Override
		public String loadName(UUID playerUUID){
			if (!hasDataFile(playerUUID)) {
				return null;
			}
			File dataFile = new File("plugins"+System.getProperty("file.separator")+"CustomEnderChest"+System.getProperty("file.separator")+"PlayerData", playerUUID + ".yml");
			FileConfiguration ymlFormat = YamlConfiguration.loadConfiguration(dataFile);
			String name = ymlFormat.getString("PlayerLastName");
			return name;
		}
				
		//load chest size from storage file
		@Override
		public Integer loadSize(UUID playerUUID){
			if (!hasDataFile(playerUUID)) {
				return 0;
			}
			File dataFile = new File("plugins"+System.getProperty("file.separator")+"CustomEnderChest"+System.getProperty("file.separator")+"PlayerData", playerUUID + ".yml");
			FileConfiguration ymlFormat = YamlConfiguration.loadConfiguration(dataFile);
			Integer size = ymlFormat.getInt("EnderChestSize");
			return size;
		}
				
		//Delete a data file if there is none
		@Override
		public boolean deleteDataFile(UUID playerUUID) {
			try {
				File dataFile = new File("plugins"+System.getProperty("file.separator")+"CustomEnderChest"+System.getProperty("file.separator")+"PlayerData", playerUUID + ".yml");
				if (dataFile.exists())
				{
					dataFile.delete();
					return true;
				}
				
			} catch (Exception e) {
				enderchest.getLogger().severe("Could not delete data file " + playerUUID + "!");
				e.printStackTrace();
			}
			return false;
		}

		@Override
		public void saveEnderChest(UUID uuid, Inventory endInv, String playerName, int invSize) {
			// TODO Auto-generated method stub
			
		}

}
