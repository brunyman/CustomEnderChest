package net.craftersland.customenderchest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ConfigHandler {
	
	private EnderChest enderchest;
	
	public ConfigHandler(EnderChest enderchest) {
		this.enderchest = enderchest;
		
		//Create CustomEnderChest plugin folder
    	(new File("plugins"+System.getProperty("file.separator")+"CustomEnderChest")).mkdir();
		
		//Create the config file
		if (!(new File("plugins"+System.getProperty("file.separator")+"CustomEnderChest"+System.getProperty("file.separator")+"config.yml").exists())) {
			EnderChest.log.info("No config file found! Creating new one...");
			enderchest.saveDefaultConfig();
		}
		//Load the config file
		try {
			enderchest.getConfig().load(new File("plugins"+System.getProperty("file.separator")+"CustomEnderChest"+System.getProperty("file.separator")+"config.yml"));
		} catch (Exception e) {
			EnderChest.log.info("Could not load config file!");
			e.printStackTrace();
		}
				
		//Create PlayerData folder for FlatFile storage
		if (getString("database.typeOfDatabase").matches("FlatFile")) {
			File dataFolder = new File(enderchest.getDataFolder(), "PlayerData");
			if (dataFolder.exists()) {
			    return;
			} else {
			    dataFolder.mkdirs();
			    EnderChest.log.info("Creating PlayerData folder...");
			}
		}
	}
	
	//Read config data
	public String getString(String key) {
		if (!enderchest.getConfig().contains(key)) {
			enderchest.getLogger().severe("Could not locate '"+key+"' in the config.yml inside of the CustomEnderChest folder! (Try generating a new one by deleting the current)");
			return "Error could not locate in config:"+key;
		}
			return enderchest.getConfig().getString(key);
	}
	
	//Send chat messages from config
	public void printMessage(Player p, String messageKey) {
		if (enderchest.getConfig().contains(messageKey)){
			List<String> message = new ArrayList<String>();
			String configMsg = enderchest.getConfig().getString(messageKey);
			
			if (configMsg.matches("")) return;
			
			message.add(configMsg);
			
			if (p != null) {				
				//Message format
				p.sendMessage(getString("chatMessages.prefix").replaceAll("&", "§") + message.get(0).replaceAll("&", "§"));
			}
				
		} else {
			enderchest.getLogger().severe("Could not locate '"+messageKey+"' in the config.yml inside of the CustomEnderChest folder!");
			p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + ">> " + ChatColor.RED + "Could not locate '"+messageKey+"' in the config.yml inside of the CustomEnderChest folder!");
		}
			
	}

}
