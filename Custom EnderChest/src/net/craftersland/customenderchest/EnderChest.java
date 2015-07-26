package net.craftersland.customenderchest;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import net.craftersland.customenderchest.storage.FlatFileStorage;
import net.craftersland.customenderchest.storage.MysqlMaintenance;
import net.craftersland.customenderchest.storage.MysqlSetup;
import net.craftersland.customenderchest.storage.MysqlStorage;
import net.craftersland.customenderchest.storage.StorageInterface;
import net.craftersland.customenderchest.utils.EnderChestUtils;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class EnderChest extends JavaPlugin {
	
	public static Logger log;
	public Map<String, UUID> admin = new HashMap<String, UUID>();
	
	private ConfigHandler configHandler;
	private StorageInterface storageInterface;
	private EnderChestUtils enderchestUtils;
	private MysqlSetup mysqlSetup;
	private MysqlMaintenance mysqlMaintenance;
	@SuppressWarnings("unused")
	private boolean enabled = false;
	
	    //Loading plugin
		public void onEnable() {
			log = getLogger();
			log.info("Loading CustomEnderChest v" + getDescription().getVersion() + "...");
			
			//Create CustomEnderChest plugin folder
	    	(new File("plugins"+System.getProperty("file.separator")+"CustomEnderChest")).mkdir();
	    	
	    	//Load Configuration
	        configHandler = new ConfigHandler(this);
	        
	        //Load util classes
	        enderchestUtils = new EnderChestUtils(this);
	        
	        //Get data system to use
	        if (configHandler.getString("database.typeOfDatabase").equalsIgnoreCase("mysql") == true) {
	        	log.info("Using MySQL database for data.");
	        	log.info("Connecting to database...");
	        	
	        	mysqlSetup = new MysqlSetup(this);
	        	storageInterface = new MysqlStorage(this);
	        	mysqlMaintenance = new MysqlMaintenance(this);
	        	
	        	if (mysqlSetup.getConnection() == null) {
	        		getServer().getPluginManager().disablePlugin(this);
                    return;
	        	}
	        } else {
	        	log.info("Using FlatFile system for data.");
	        	
	        	//Initiate FlatFile data source interface
		      	storageInterface = new FlatFileStorage(this);	
	        }
	      	
	        //Register Listeners
	    	PluginManager pm = getServer().getPluginManager();
	    	pm.registerEvents(new PlayerHandler(this), this);
	    	CommandHandler cH = new CommandHandler(this);
	    	getCommand("customec").setExecutor(cH);
	    	getCommand("customenderchest").setExecutor(cH);
	    	enabled = true;
	    	log.info("CustomEnderChest has been successfully loaded!");
		}
		
		//Disabling plugin
		public void onDisable() {
			if (enabled = true) {
				//Closing database connection
				if (configHandler.getString("database.typeOfDatabase").equalsIgnoreCase("mysql")) {
					if (mysqlSetup.getConnection() != null) {
						log.info("Closing MySQL connection...");
						mysqlSetup.closeDatabase();
					}
				}
			}
			log.info("CustomEnderChest has been disabled.");
			
		}
		
		//Getting other classes public
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
		public MysqlMaintenance getMysqlMaintenance() {
			return mysqlMaintenance;
		}

}
