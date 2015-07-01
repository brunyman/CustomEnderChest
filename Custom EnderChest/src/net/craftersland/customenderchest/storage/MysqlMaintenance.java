package net.craftersland.customenderchest.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.bukkit.Bukkit;

import net.craftersland.customenderchest.EnderChest;

public class MysqlMaintenance {
	
	private EnderChest enderchest;
	private Connection conn;
	
	public MysqlMaintenance(EnderChest enderchest) {
		this.enderchest = enderchest;
		this.conn = enderchest.getMysqlSetup().getConnection();
		
		if (enderchest.getConfigHandler().getString("database.mysql.removeOldUsers.enabled").matches("true") && enderchest.getConfigHandler().getString("database.typeOfDatabase").matches("MySQL")) {
			runMaintenance();
		}
	}
	
    public void runMaintenance() {
		
		Bukkit.getScheduler().runTaskLaterAsynchronously(enderchest, new Runnable() {
			@Override
			public void run() {
				if (conn == null) return;
				EnderChest.log.info("Database maintenance task started...");
				
				//Maintenance on Enderchest Table
				maintenanceEnderchest();
				
				EnderChest.log.info("Database maintenance task ended.");
			}
		}, 400L);
		
	}
    
    private void maintenanceEnderchest() {
		//Maintenance on Inventory and Armor Table
		if (enderchest.getConfigHandler().getString("database.mysql.removeOldUsers.enabled").matches("true")) {
			long inactivityDays = Long.parseLong(enderchest.getConfigHandler().getString("database.mysql.removeOldUsers.inactive"));
			long inactivityMils = inactivityDays * 24 * 60 * 60 * 1000;
			long curentTime = System.currentTimeMillis();
			long inactiveTime = curentTime - inactivityMils;
			String tableName = enderchest.getConfigHandler().getString("database.mysql.tableName");
			
			try {
				String sql = "DELETE FROM `" + tableName + "` WHERE `last_seen` <?";
				PreparedStatement preparedStatement = conn.prepareStatement(sql);
				preparedStatement.setString(1, String.valueOf(inactiveTime));
				
				preparedStatement.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
