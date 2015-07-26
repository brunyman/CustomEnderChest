package net.craftersland.customenderchest.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import net.craftersland.customenderchest.EnderChest;

public class MysqlSetup {
	
	public Connection conn = null;
	
	// Hostname
	private String dbHost;
	// Port -- Standard: 3306
	private String dbPort;
	// Databankname
    private String database;
	// Databank username
	private String dbUser; 
	// Databank password
	private String dbPassword;
	
	private EnderChest enderchest;
	private String tableName = "cec_enderchests";
	
	public MysqlSetup(EnderChest enderchest) {
		this.enderchest = enderchest;
		
		setupDatabase();
	}
	
	public boolean setupDatabase() {
		try {
       	 	//Load Drivers
            Class.forName("com.mysql.jdbc.Driver");
            
            dbHost = enderchest.getConfigHandler().getString("database.mysql.host");
            dbPort = enderchest.getConfigHandler().getString("database.mysql.port");
            database = enderchest.getConfigHandler().getString("database.mysql.databaseName");
            dbUser = enderchest.getConfigHandler().getString("database.mysql.user");
            dbPassword = enderchest.getConfigHandler().getString("database.mysql.password");
            
            String passFix = dbPassword.replaceAll("%", "%25");
            String passFix2 = passFix.replaceAll("\\+", "%2B");
            
            //Connect to database
            conn = DriverManager.getConnection("jdbc:mysql://" + dbHost + ":" + dbPort + "/" + database + "?" + "user=" + dbUser + "&" + "password=" + passFix2);
           
          } catch (ClassNotFoundException e) {
        	  EnderChest.log.severe("Could not locate drivers for mysql!");
            return false;
          } catch (SQLException e) {
        	  EnderChest.log.severe("Could not connect to mysql database!");
            return false;
          }
		
		//Create tables if needed
	      Statement query;
	      try {
	        
	    	query = conn.createStatement();
	        tableName = enderchest.getConfigHandler().getString("database.mysql.tableName");
	        String data = "CREATE TABLE IF NOT EXISTS `" + tableName + "` (id int(10) AUTO_INCREMENT, player_uuid varchar(50) NOT NULL UNIQUE, player_name varchar(50) NOT NULL, enderchest varchar(10000) NOT NULL, size int(3) NOT NULL, last_seen varchar(30) NOT NULL, PRIMARY KEY(id));";
		    query.executeUpdate(data);
	        
	      } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	      }
	      EnderChest.log.info("Mysql connection successful!");
		return true;
	}
	
	public Connection getConnection() {
		checkConnection();
		return conn;
	}
	
	public boolean closeDatabase() {
		try {
			conn.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean checkConnection() {
		try {
			if (conn == null) {
				EnderChest.log.warning("Connection failed. Reconnecting...");
				if (reConnect() == true) return true;
				return false;
			}
			if (!conn.isValid(3)) {
				EnderChest.log.warning("Connection is idle or terminated. Reconnecting...");
				if (reConnect() == true) return true;
				return false;
			}
			if (conn.isClosed() == true) {
				EnderChest.log.warning("Connection is closed. Reconnecting...");
				if (reConnect() == true) return true;
				return false;
			}
			return true;
		} catch (Exception e) {
			EnderChest.log.severe("Could not reconnect to Database!");
		}
		return true;
	}
	
	public boolean reConnect() {
		try {
			dbHost = enderchest.getConfigHandler().getString("database.mysql.host");
            dbPort = enderchest.getConfigHandler().getString("database.mysql.port");
            database = enderchest.getConfigHandler().getString("database.mysql.databaseName");
            dbUser = enderchest.getConfigHandler().getString("database.mysql.user");
            dbPassword = enderchest.getConfigHandler().getString("database.mysql.password");
            
            String passFix = dbPassword.replaceAll("%", "%25");
            String passFix2 = passFix.replaceAll("\\+", "%2B");
            
            long start = 0;
			long end = 0;
			
		    start = System.currentTimeMillis();
		    EnderChest.log.info("Attempting to establish a connection to the MySQL server!");
		    Class.forName("com.mysql.jdbc.Driver");
		    conn = DriverManager.getConnection("jdbc:mysql://" + dbHost + ":" + dbPort + "/" + database + "?" + "user=" + dbUser + "&" + "password=" + passFix2);
		    end = System.currentTimeMillis();
		    EnderChest.log.info("Connection to MySQL server established!");
		    EnderChest.log.info("Connection took " + ((end - start)) + "ms!");
            return true;
		} catch (Exception e) {
			EnderChest.log.severe("Could not connect to MySQL server! because: " + e.getMessage());
			return false;
		}
	}

}
