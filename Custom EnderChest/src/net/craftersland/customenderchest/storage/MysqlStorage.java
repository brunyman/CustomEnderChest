package net.craftersland.customenderchest.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.craftersland.customenderchest.EnderChest;
import net.craftersland.customenderchest.utils.EncodingUtil;

public class MysqlStorage implements StorageInterface {
	
	private EnderChest enderchest;
	private Connection conn;
	private String tableName;
	
	public MysqlStorage(EnderChest enderchest) {
		this.enderchest = enderchest;
		
	}
	
	@Override
	public boolean hasDataFile(UUID player) {
		conn = enderchest.getMysqlSetup().getConnection();
		try {
			tableName = enderchest.getConfigHandler().getString("database.mysql.tableName");
	 
	        String sql = "SELECT `player_uuid` FROM `" + tableName + "` WHERE `player_uuid` = ?";
	        PreparedStatement preparedUpdateStatement = conn.prepareStatement(sql);
	        preparedUpdateStatement.setString(1, player.toString());
	        
	        
	        ResultSet result = preparedUpdateStatement.executeQuery();
	 
	        while (result.next()) {
	        	return true;
	        }
	      } catch (SQLException e) {
	        e.printStackTrace();
	      }
	      return false;
	}
	
	@Override
	public boolean deleteDataFile(UUID player) {
		conn = enderchest.getMysqlSetup().getConnection();
		try {
			tableName = enderchest.getConfigHandler().getString("database.mysql.tableName");
	 
			String sql = "DELETE FROM `" + tableName + "` WHERE `player_uuid` =?";
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, String.valueOf(player));
			
			preparedStatement.executeUpdate();
	 
	        return true;
	      } catch (SQLException e) {
	        return false;
	      }
	}
	
	public boolean createAccount(UUID uuid, Player p) {
		conn = enderchest.getMysqlSetup().getConnection();
		try {
			tableName = enderchest.getConfigHandler().getString("database.mysql.tableName");
			 
	        String sql = "INSERT INTO `" + tableName + "`(`player_uuid`, `player_name`, `enderchest`, `size`, `last_seen`) " + "VALUES(?, ?, ?, ?, ?)";
	        PreparedStatement preparedStatement = conn.prepareStatement(sql);
	        
	        preparedStatement.setString(1, uuid.toString());
	        preparedStatement.setString(2, p.getName() + "");
	        preparedStatement.setString(3, "none");
	        preparedStatement.setInt(4, 0);
	        preparedStatement.setString(5, String.valueOf(System.currentTimeMillis()) + "");
	        
	        preparedStatement.executeUpdate();
	        return true;
	      } catch (SQLException e) {
	        e.printStackTrace();
	      }
		return false;
	}
	
	@Override
	public boolean saveEnderChest(UUID uuid, Player p, Inventory endInv) {
		if (!hasDataFile(uuid)) {
			createAccount(uuid, p);
		}
		conn = enderchest.getMysqlSetup().getConnection();
		try {
			tableName = enderchest.getConfigHandler().getString("database.mysql.tableName");
        	
			String updateSqlExp = "UPDATE `" + tableName + "` " + "SET `player_name` = ?" + ", `enderchest` = ?" + ", `size` = ?" + ", `last_seen` = ?" + " WHERE `player_uuid` = ?";
			PreparedStatement preparedUpdateStatement = conn.prepareStatement(updateSqlExp);
			preparedUpdateStatement.setString(1, p.getName() + "");
			preparedUpdateStatement.setString(2, EncodingUtil.toBase64(endInv) + "");
			preparedUpdateStatement.setInt(3, endInv.getSize());
			preparedUpdateStatement.setString(4, String.valueOf(System.currentTimeMillis()));
			preparedUpdateStatement.setString(5, uuid.toString() + "");
			
			preparedUpdateStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
        return false;
	}
	
	@Override
	public boolean saveEnderChest(Player p, Inventory endInv) {
		if (!hasDataFile(p.getUniqueId())) {
			createAccount(p.getUniqueId(), p);
		}
		conn = enderchest.getMysqlSetup().getConnection();
		try {
			tableName = enderchest.getConfigHandler().getString("database.mysql.tableName");
        	
			String updateSqlExp = "UPDATE `" + tableName + "` " + "SET `player_name` = ?" + ", `enderchest` = ?" + ", `size` = ?" + ", `last_seen` = ?" + " WHERE `player_uuid` = ?";
			PreparedStatement preparedUpdateStatement = conn.prepareStatement(updateSqlExp);
			preparedUpdateStatement.setString(1, p.getName() + "");
			preparedUpdateStatement.setString(2, EncodingUtil.toBase64(endInv) + "");
			preparedUpdateStatement.setInt(3, endInv.getSize());
			preparedUpdateStatement.setString(4, String.valueOf(System.currentTimeMillis()));
			preparedUpdateStatement.setString(5, p.getUniqueId().toString() + "");
			
			preparedUpdateStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
        return false;
	}
	
	@Override
	public boolean loadEnderChest(UUID uuid, Inventory endInv) {
		if (!hasDataFile(uuid)) {
			createAccount(uuid, null);
		}
		conn = enderchest.getMysqlSetup().getConnection();
		try {
			tableName = enderchest.getConfigHandler().getString("database.mysql.tableName");
	 
	        String sql = "SELECT `enderchest` FROM `" + tableName + "` WHERE `player_uuid` = ?";
	        
	        PreparedStatement preparedUpdateStatement = conn.prepareStatement(sql);
	        preparedUpdateStatement.setString(1, uuid.toString());
	        ResultSet result = preparedUpdateStatement.executeQuery();
	 
	        while (result.next()) {
	        	try {
	        		Inventory mysqlInv = EncodingUtil.fromBase64(result.getString("enderchest"));
	        		for (int i = 0; i < endInv.getSize(); i++) {
	        			ItemStack item = mysqlInv.getItem(i);
	        			endInv.setItem(i, item);
	                }
		        	return true;
	        	} catch (Exception e) {
	        		return false;
	        	}
	        }
	      } catch (SQLException e) {
	        e.printStackTrace();
	      }
		return false;
	}
	
	@Override
	public boolean loadEnderChest(Player p, Inventory endInv) {
		if (!hasDataFile(p.getUniqueId())) {
			createAccount(p.getUniqueId(), p);
		}
		conn = enderchest.getMysqlSetup().getConnection();
		try {
			tableName = enderchest.getConfigHandler().getString("database.mysql.tableName");
	 
	        String sql = "SELECT `enderchest` FROM `" + tableName + "` WHERE `player_uuid` = ?";
	        
	        PreparedStatement preparedUpdateStatement = conn.prepareStatement(sql);
	        preparedUpdateStatement.setString(1, p.getUniqueId().toString());
	        ResultSet result = preparedUpdateStatement.executeQuery();
	 
	        while (result.next()) {
	        	try {
	        		Inventory mysqlInv = EncodingUtil.fromBase64(result.getString("enderchest"));
	        		
	        		for (int i = 0; i < endInv.getSize(); i++) {
	        			ItemStack item = mysqlInv.getItem(i);
	        			endInv.setItem(i, item);
	                }
	        		
		        	//endInv.setContents(mysqlInv.getContents());
		        	return true;
	        	} catch (Exception e) {
	        		return false;
	        	}
	        }
	      } catch (SQLException e) {
	        e.printStackTrace();
	      }
		return false;
	}
	
	public String loadName(UUID uuid) {
		if (!hasDataFile(uuid)) {
			createAccount(uuid, null);
		}
		conn = enderchest.getMysqlSetup().getConnection();
		try {
			tableName = enderchest.getConfigHandler().getString("database.mysql.tableName");
	 
	        String sql = "SELECT `player_name` FROM `" + tableName + "` WHERE `player_uuid` = ?";
	        
	        PreparedStatement preparedUpdateStatement = conn.prepareStatement(sql);
	        preparedUpdateStatement.setString(1, uuid.toString());
	        ResultSet result = preparedUpdateStatement.executeQuery();
	 
	        while (result.next()) {
	        	return result.getString("player_name");
	        }
	      } catch (SQLException e) {
	        e.printStackTrace();
	      }
		return null;
	}
	
	public Integer loadSize(UUID uuid) {
		if (!hasDataFile(uuid)) {
			createAccount(uuid, null);
		}
		conn = enderchest.getMysqlSetup().getConnection();
		try {
			tableName = enderchest.getConfigHandler().getString("database.mysql.tableName");
	 
	        String sql = "SELECT `size` FROM `" + tableName + "` WHERE `player_uuid` = ?";
	        
	        PreparedStatement preparedUpdateStatement = conn.prepareStatement(sql);
	        preparedUpdateStatement.setString(1, uuid.toString());
	        ResultSet result = preparedUpdateStatement.executeQuery();
	 
	        while (result.next()) {
	        	return result.getInt("size");
	        }
	      } catch (SQLException e) {
	        e.printStackTrace();
	      }
		return null;
	}

}
