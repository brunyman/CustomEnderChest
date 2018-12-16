package net.craftersland.customenderchest;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import net.craftersland.customenderchest.commands.FileToMysqlCmd;
import net.craftersland.customenderchest.storage.FlatFileStorage;
import net.craftersland.customenderchest.storage.MysqlSetup;
import net.craftersland.customenderchest.storage.MysqlStorage;
import net.craftersland.customenderchest.storage.StorageInterface;
import net.craftersland.customenderchest.utils.EnderChestUtils;
import net.craftersland.customenderchest.utils.ModdedSerializer;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class EnderChest extends JavaPlugin {
	
	public static Logger log;
	public Map<Inventory, UUID> admin = new HashMap<Inventory, UUID>();
	public static boolean is19Server = true;
	public static boolean is13Server = false;
	public static String pluginName = "CustomEnderChest";
	
	private static ConfigHandler configHandler;
	private static StorageInterface storageInterface;
	private static EnderChestUtils enderchestUtils;
	private static DataHandler dH;
	private static MysqlSetup mysqlSetup;
	private static SoundHandler sH;
	private static ModdedSerializer ms;
	private static FileToMysqlCmd ftmc;
	
		public void onEnable() {
			log = getLogger();
			getMcVersion();	    	
	        configHandler = new ConfigHandler(this);
	        checkForModdedNBTsupport();
	        enderchestUtils = new EnderChestUtils(this);
	        if (configHandler.getString("database.typeOfDatabase").equalsIgnoreCase("mysql") == true) {
	        	log.info("Using MySQL database for data.");
	        	mysqlSetup = new MysqlSetup(this);
	        	storageInterface = new MysqlStorage(this);
	        } else {
	        	log.info("Using FlatFile system for data. IMPORTANT! We recommend MySQL.");
	        	File pluginFolder = new File("plugins" + System.getProperty("file.separator") + pluginName + System.getProperty("file.separator") + "PlayerData");
	    		if (pluginFolder.exists() == false) {
	        		pluginFolder.mkdir();
	        	}
		      	storageInterface = new FlatFileStorage(this);	
	        }
	        dH = new DataHandler(this);
	        sH = new SoundHandler(this);
	        ftmc = new FileToMysqlCmd(this);
	    	PluginManager pm = getServer().getPluginManager();
	    	pm.registerEvents(new PlayerHandler(this), this);
	    	CommandHandler cH = new CommandHandler(this);
	    	getCommand("customec").setExecutor(cH);
	    	getCommand("ec").setExecutor(cH);
	    	getCommand("customenderchest").setExecutor(cH);
	    	log.info(pluginName + " loaded successfully!");
		}
		
		//Disabling plugin
		public void onDisable() {
			Bukkit.getScheduler().cancelTasks(this);
			if (configHandler.getString("database.typeOfDatabase").equalsIgnoreCase("mysql")) {
				if (mysqlSetup.getConnection() != null) {
					log.info("Closing database connection...");
					mysqlSetup.closeDatabase();
				}
			}
			log.info("Cleaning internal data...");
			dH.clearLiveData();
			HandlerList.unregisterAll(this);
			log.info(pluginName + " is disabled!");
		}
		
		private boolean getMcVersion() {
			String[] serverVersion = Bukkit.getBukkitVersion().split("-");
		    String version = serverVersion[0];
		    
		    if (version.matches("1.7.10") || version.matches("1.7.9") || version.matches("1.7.5") || version.matches("1.7.2") || version.matches("1.8.8") || version.matches("1.8.3") || version.matches("1.8.4") || version.matches("1.8")) {
		    	is19Server = false;
		    	return true;
		    }
		    if (version.matches("1.13") || version.matches("1.13.1") || version.matches("1.13.2")) {
		    	is19Server = true;
		    	is13Server = true;
		    	return true;
		    }
		    return false;
		}
		
		private void checkForModdedNBTsupport() {
			if (configHandler.getBoolean("settings.modded-NBT-data-support") == true) {
				if (configHandler.getString("database.typeOfDatabase").equalsIgnoreCase("mysql")) {
					if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null) {
						ms = new ModdedSerializer(this);
						log.info("ProtocolLib dependency found. Modded NBT data support is enabled!");
			        } else {
			        	log.warning("ProtocolLib dependency not found!!! Modded NBT data support is disabled!");
			        }
				} else {
					log.warning("NBT Modded data support only works for MySQL storage. Modded NBT data support is disabled!");
				}
			}
	    }
		
		public ConfigHandler getConfigHandler() {
			return configHandler;
		}
		public StorageInterface getStorageInterface() {
			return storageInterface;
		}
		public EnderChestUtils getEnderChestUtils() {
			return enderchestUtils;
		}
		public MysqlSetup getMysqlSetup() {
			return mysqlSetup;
		}
		public SoundHandler getSoundHandler() {
			return sH;
		}
		public DataHandler getDataHandler() {
			return dH;
		}
		public ModdedSerializer getModdedSerializer() {
			return ms;
		}
		public FileToMysqlCmd getFileToMysqlCmd() {
			return ftmc;
		}

}
