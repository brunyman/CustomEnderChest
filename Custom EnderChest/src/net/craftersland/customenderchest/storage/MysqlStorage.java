package net.craftersland.customenderchest.storage;

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
	
	public MysqlStorage(EnderChest enderchest) {
		this.enderchest = enderchest;
		
	}
	
	@Override
	public boolean hasDataFile(UUID player) {
		ResultSet result = null;
		PreparedStatement preparedUpdateStatement = null;
		try {	 
	        String sql = "SELECT `player_uuid` FROM `" + enderchest.getConfigHandler().getString("database.mysql.tableName") + "` WHERE `player_uuid` = ?";
	        preparedUpdateStatement = enderchest.getMysqlSetup().getConnection().prepareStatement(sql);
	        preparedUpdateStatement.setString(1, player.toString());
	        result = preparedUpdateStatement.executeQuery();
	        while (result.next()) {
	        	return true;
	        }
	      } catch (SQLException e) {
	        e.printStackTrace();
	      } finally {
				try {
					if (result != null) {
						result.close();
					}
					if (preparedUpdateStatement != null) {
						preparedUpdateStatement.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
		  }
	      return false;
	}
	
	@Override
	public boolean deleteDataFile(UUID player) {
		PreparedStatement preparedStatement = null;
		try {	 
			String sql = "DELETE FROM `" + enderchest.getConfigHandler().getString("database.mysql.tableName") + "` WHERE `player_uuid` =?";
			preparedStatement = enderchest.getMysqlSetup().getConnection().prepareStatement(sql);
			preparedStatement.setString(1, String.valueOf(player));
			preparedStatement.executeUpdate();
	        return true;
		} catch (SQLException e) {
	        return false;
		} finally {
				try {
					if (preparedStatement != null) {
						preparedStatement.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}
	
	public boolean createAccount(UUID uuid, Player p) {
		PreparedStatement preparedStatement = null;
		try {			 
	        String sql = "INSERT INTO `" + enderchest.getConfigHandler().getString("database.mysql.tableName") + "`(`player_uuid`, `player_name`, `enderchest_data`, `size`, `last_seen`) " + "VALUES(?, ?, ?, ?, ?)";
	        preparedStatement = enderchest.getMysqlSetup().getConnection().prepareStatement(sql);
	        preparedStatement.setString(1, uuid.toString());
	        preparedStatement.setString(2, p.getName() + "");
	        preparedStatement.setString(3, "none");
	        preparedStatement.setInt(4, 0);
	        preparedStatement.setString(5, String.valueOf(System.currentTimeMillis()) + "");
	        preparedStatement.executeUpdate();
	        return true;
	      } catch (SQLException e) {
	        e.printStackTrace();
	      } finally {
				try {
					if (preparedStatement != null) {
						preparedStatement.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
		  }
		return false;
	}
	
	@Override
	public boolean saveEnderChest(UUID uuid, Player p, Inventory endInv) {
		if (!hasDataFile(uuid)) {
			createAccount(uuid, p);
		}
		PreparedStatement preparedUpdateStatement = null;
		try {        	
			String updateSqlExp = "UPDATE `" + enderchest.getConfigHandler().getString("database.mysql.tableName") + "` " + "SET `player_name` = ?" + ", `enderchest_data` = ?" + ", `size` = ?" + ", `last_seen` = ?" + " WHERE `player_uuid` = ?";
			preparedUpdateStatement = enderchest.getMysqlSetup().getConnection().prepareStatement(updateSqlExp);
			preparedUpdateStatement.setString(1, p.getName() + "");
			preparedUpdateStatement.setString(2, EncodingUtil.toBase64(endInv) + "");
			preparedUpdateStatement.setInt(3, endInv.getSize());
			preparedUpdateStatement.setString(4, String.valueOf(System.currentTimeMillis()));
			preparedUpdateStatement.setString(5, uuid.toString() + "");
			preparedUpdateStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (preparedUpdateStatement != null) {
					preparedUpdateStatement.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
        return false;
	}
	
	@Override
	public boolean saveEnderChest(Player p, Inventory endInv) {
		if (!hasDataFile(p.getUniqueId())) {
			createAccount(p.getUniqueId(), p);
		}
		PreparedStatement preparedUpdateStatement = null;
		try {        	
			String updateSqlExp = "UPDATE `" + enderchest.getConfigHandler().getString("database.mysql.tableName") + "` " + "SET `player_name` = ?" + ", `enderchest_data` = ?" + ", `size` = ?" + ", `last_seen` = ?" + " WHERE `player_uuid` = ?";
			preparedUpdateStatement = enderchest.getMysqlSetup().getConnection().prepareStatement(updateSqlExp);
			preparedUpdateStatement.setString(1, p.getName() + "");
			preparedUpdateStatement.setString(2, EncodingUtil.toBase64(endInv) + "");
			preparedUpdateStatement.setInt(3, endInv.getSize());
			preparedUpdateStatement.setString(4, String.valueOf(System.currentTimeMillis()));
			preparedUpdateStatement.setString(5, p.getUniqueId().toString() + "");
			preparedUpdateStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (preparedUpdateStatement != null) {
					preparedUpdateStatement.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
        return false;
	}
	
	@Override
	public boolean loadEnderChest(UUID uuid, Inventory endInv) {
		if (!hasDataFile(uuid)) {
			createAccount(uuid, null);
		}
		PreparedStatement preparedUpdateStatement = null;
		ResultSet result = null;
		try {	 
	        String sql = "SELECT `enderchest_data` FROM `" + enderchest.getConfigHandler().getString("database.mysql.tableName") + "` WHERE `player_uuid` = ?";
	        preparedUpdateStatement = enderchest.getMysqlSetup().getConnection().prepareStatement(sql);
	        preparedUpdateStatement.setString(1, uuid.toString());
	        result = preparedUpdateStatement.executeQuery();
	        while (result.next()) {
	        	try {
	        		Inventory mysqlInv = EncodingUtil.fromBase64(result.getString("enderchest_data"));
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
	      } finally {
				try {
					if (result != null) {
						result.close();
					}
					if (preparedUpdateStatement != null) {
						preparedUpdateStatement.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
		  }
		return false;
	}
	
	@Override
	public boolean loadEnderChest(Player p, Inventory endInv) {
		if (!hasDataFile(p.getUniqueId())) {
			createAccount(p.getUniqueId(), p);
		}
		PreparedStatement preparedUpdateStatement = null;
		ResultSet result = null;
		try {	 
	        String sql = "SELECT `enderchest_data` FROM `" + enderchest.getConfigHandler().getString("database.mysql.tableName") + "` WHERE `player_uuid` = ?";
	        preparedUpdateStatement = enderchest.getMysqlSetup().getConnection().prepareStatement(sql);
	        preparedUpdateStatement.setString(1, p.getUniqueId().toString());
	        result = preparedUpdateStatement.executeQuery();
	        while (result.next()) {
	        	try {
	        		Inventory mysqlInv = EncodingUtil.fromBase64(result.getString("enderchest_data"));
	        		
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
	      } finally {
				try {
					if (result != null) {
						result.close();
					}
					if (preparedUpdateStatement != null) {
						preparedUpdateStatement.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
		  }
		return false;
	}
	
	public String loadName(UUID uuid) {
		if (!hasDataFile(uuid)) {
			createAccount(uuid, null);
		}
		PreparedStatement preparedUpdateStatement = null;
		ResultSet result = null;
		try {	 
	        String sql = "SELECT `player_name` FROM `" + enderchest.getConfigHandler().getString("database.mysql.tableName") + "` WHERE `player_uuid` = ?";
	        preparedUpdateStatement = enderchest.getMysqlSetup().getConnection().prepareStatement(sql);
	        preparedUpdateStatement.setString(1, uuid.toString());
	        result = preparedUpdateStatement.executeQuery();
	        while (result.next()) {
	        	return result.getString("player_name");
	        }
	      } catch (SQLException e) {
	        e.printStackTrace();
	      } finally {
				try {
					if (result != null) {
						result.close();
					}
					if (preparedUpdateStatement != null) {
						preparedUpdateStatement.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
		  }
		return null;
	}
	
	public Integer loadSize(UUID uuid) {
		if (!hasDataFile(uuid)) {
			createAccount(uuid, null);
		}
		PreparedStatement preparedUpdateStatement = null;
		ResultSet result = null;
		try {	 
	        String sql = "SELECT `size` FROM `" + enderchest.getConfigHandler().getString("database.mysql.tableName") + "` WHERE `player_uuid` = ?";
	        preparedUpdateStatement = enderchest.getMysqlSetup().getConnection().prepareStatement(sql);
	        preparedUpdateStatement.setString(1, uuid.toString());
	        result = preparedUpdateStatement.executeQuery();
	        while (result.next()) {
	        	return result.getInt("size");
	        }
	      } catch (SQLException e) {
	        e.printStackTrace();
	      } finally {
				try {
					if (result != null) {
						result.close();
					}
					if (preparedUpdateStatement != null) {
						preparedUpdateStatement.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
		  }
		return null;
	}

}
