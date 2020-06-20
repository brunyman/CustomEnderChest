package net.craftersland.customenderchest.storage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
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
	public boolean saveEnderChest(UUID uuid, Inventory endInv) {
		/*if (!hasDataFile(uuid)) {
			createAccount(uuid, uuid.toString());
		}*/
		PreparedStatement preparedUpdateStatement = null;
		int storageSize = loadSize(uuid);
		try {        	
			String updateSqlExp = "UPDATE `" + enderchest.getConfigHandler().getString("database.mysql.tableName") + "` " + "SET `enderchest_data` = ?" + ", `size` = ?" + " WHERE `player_uuid` = ?";
			preparedUpdateStatement = enderchest.getMysqlSetup().getConnection().prepareStatement(updateSqlExp);
			if (endInv.getSize() >= storageSize) {
				preparedUpdateStatement.setString(1, encodeInventory(endInv, uuid.toString()));
				preparedUpdateStatement.setInt(2, endInv.getSize());
			} else {
				Inventory storageInv = decodeInventory(getEnderchestString(uuid), null, storageSize);
				for (int i = 0; i < endInv.getSize(); i++) {
					storageInv.setItem(i, endInv.getItem(i));
				}
				preparedUpdateStatement.setString(1, encodeInventory(storageInv, uuid.toString()));
				preparedUpdateStatement.setInt(2, storageSize);
			}
			preparedUpdateStatement.setString(3, uuid.toString() + "");
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
	public void saveEnderChest(UUID uuid, Inventory endInv, String playerName, int invSize) {
		PreparedStatement preparedStatement = null;
		try {			 
	        String sql = "INSERT INTO `" + enderchest.getConfigHandler().getString("database.mysql.tableName") + "`(`player_uuid`, `player_name`, `enderchest_data`, `size`, `last_seen`) " + "VALUES(?, ?, ?, ?, ?)";
	        preparedStatement = enderchest.getMysqlSetup().getConnection().prepareStatement(sql);
	        preparedStatement.setString(1, uuid.toString());
	        preparedStatement.setString(2, playerName);
	        preparedStatement.setString(3, encodeInventory(endInv, uuid.toString()));
	        preparedStatement.setInt(4, invSize);
	        preparedStatement.setString(5, String.valueOf(System.currentTimeMillis()));
	        preparedStatement.executeUpdate();
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
	}
	
	@Override
	public boolean saveEnderChest(Player p, Inventory endInv) {
		if (!hasDataFile(p.getUniqueId())) {
			createAccount(p.getUniqueId(), p);
		}
		PreparedStatement preparedUpdateStatement = null;
		int storageSize = loadSize(p.getUniqueId());
		try {        	
			String updateSqlExp = "UPDATE `" + enderchest.getConfigHandler().getString("database.mysql.tableName") + "` " + "SET `player_name` = ?" + ", `enderchest_data` = ?" + ", `size` = ?" + ", `last_seen` = ?" + " WHERE `player_uuid` = ?";
			preparedUpdateStatement = enderchest.getMysqlSetup().getConnection().prepareStatement(updateSqlExp);
			preparedUpdateStatement.setString(1, p.getName());
			if (endInv.getSize() >= storageSize) {
				preparedUpdateStatement.setString(2, encodeInventory(endInv, p.getName()));
				preparedUpdateStatement.setInt(3, endInv.getSize());
			} else {
				Inventory storageInv = decodeInventory(getEnderchestString(p.getUniqueId()), p.getDisplayName(), storageSize);
				//EnderChest.log.warning("Debug - 1 - " + endInv.getSize() + " - " + storageInv.getSize() + " - " + storageSize);
				for (int i = 0; i < endInv.getSize(); i++) {
					ItemStack item = endInv.getItem(i);
					if (item != null) {
						storageInv.setItem(i, item);
					} 
				}
				preparedUpdateStatement.setString(2, encodeInventory(storageInv, p.getName()));
				preparedUpdateStatement.setInt(3, storageSize);
			}
			preparedUpdateStatement.setString(4, String.valueOf(System.currentTimeMillis()));
			preparedUpdateStatement.setString(5, p.getUniqueId().toString());
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
	        String sql = "SELECT * FROM `" + enderchest.getConfigHandler().getString("database.mysql.tableName") + "` WHERE `player_uuid` = ?";
	        preparedUpdateStatement = enderchest.getMysqlSetup().getConnection().prepareStatement(sql);
	        preparedUpdateStatement.setString(1, uuid.toString());
	        result = preparedUpdateStatement.executeQuery();
	        while (result.next()) {
	        	try {
	        		Inventory mysqlInv = decodeInventory(result.getString("enderchest_data"), uuid.toString(), result.getInt("size"));
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
	        String sql = "SELECT * FROM `" + enderchest.getConfigHandler().getString("database.mysql.tableName") + "` WHERE `player_uuid` = ?";
	        preparedUpdateStatement = enderchest.getMysqlSetup().getConnection().prepareStatement(sql);
	        preparedUpdateStatement.setString(1, p.getUniqueId().toString());
	        result = preparedUpdateStatement.executeQuery();
	        while (result.next()) {
	        	try {
	        		Inventory mysqlInv = decodeInventory(result.getString("enderchest_data"), p.getName(), result.getInt("size"));
	        		
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
	        //e.printStackTrace();
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
	
	private Inventory decodeInventory(String rawData, String playerName, int chestSize) {
		if (enderchest.getModdedSerializer() != null) {
			try {
				ItemStack[] items = enderchest.getModdedSerializer().fromBase64(rawData);
				Inventory inv = Bukkit.getServer().createInventory(null, chestSize);
				if (chestSize >= items.length) {
					inv.setContents(items);
				} else {
					for (int i=0; i<chestSize; i++) {
						if (items[i] != null) {
							inv.addItem(items[i]);
						}
					}
				}
				
				return inv;
			} catch (Exception e) {
				e.printStackTrace();
				try {
					return EncodingUtil.fromBase64(rawData);
				} catch (Exception ex) {
					EnderChest.log.severe("Failed to decode inventory for " + playerName + "! Error: " + ex.getMessage());
					ex.printStackTrace();
				}
			}
		} else {
			try {
				return EncodingUtil.fromBase64(rawData);
			} catch (Exception e) {
				EnderChest.log.severe("Failed to decode inventory for " + playerName + "! Error: " + e.getMessage());
				//TODO
				saveEnderchest(playerName, chestSize);
			}
		}
		return null;
	}
	
	private void saveEnderchest(String playerName, int chestSize) {
		PreparedStatement preparedUpdateStatement = null;
		try {        	
			String updateSqlExp = "UPDATE `" + enderchest.getConfigHandler().getString("database.mysql.tableName") + "` " + "SET `enderchest_data` = ?" + ", `size` = ?" + ", `last_seen` = ?" + " WHERE `player_name` = ?";
			preparedUpdateStatement = enderchest.getMysqlSetup().getConnection().prepareStatement(updateSqlExp);
			preparedUpdateStatement.setString(1, encodeInventory(Bukkit.getServer().createInventory(null, chestSize), playerName));
			preparedUpdateStatement.setInt(2, chestSize);
			preparedUpdateStatement.setString(3, String.valueOf(System.currentTimeMillis()));
			preparedUpdateStatement.setString(4, playerName);
			preparedUpdateStatement.executeUpdate();
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
	}
	
	private String encodeInventory(Inventory inv, String playerName) {
		if (enderchest.getModdedSerializer() != null) {
			try {
				return enderchest.getModdedSerializer().toBase64(inv.getContents());
			} catch (Exception e) {
				EnderChest.log.severe("Failed to save enderchest data for " + playerName + "! Error: " + e.getMessage());
				e.printStackTrace();
			}
		} else {
			try {
				return EncodingUtil.toBase64(inv);
			} catch (Exception e) {
				EnderChest.log.severe("Failed to save enderchest data for " + playerName + "! Error: " + e.getMessage());
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public String getEnderchestString(UUID uuid) {
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
	        	return result.getString("enderchest_data");
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
