package net.craftersland.customenderchest;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

public class PlayerHandler implements Listener {
	
    private EnderChest enderchest;
	
	public PlayerHandler(EnderChest enderchest) {
		this.enderchest = enderchest;
	}
	
	@EventHandler
	public void onPlayerJoinEvent(final PlayerJoinEvent e) {
		Bukkit.getScheduler().runTaskAsynchronously(enderchest, new Runnable() {

			@Override
			public void run() {
				if (e.getPlayer().isOnline() == true) {
					int size = enderchest.getEnderChestUtils().getSize(e.getPlayer());
					if (size == 0) {
						size = 9;
					}
					String enderChestTitle = enderchest.getEnderChestUtils().getTitle(e.getPlayer());
					Inventory inv = Bukkit.getServer().createInventory(e.getPlayer(), size, enderChestTitle);
					if (enderchest.getStorageInterface().hasDataFile(e.getPlayer().getUniqueId()) == true) {
						enderchest.getStorageInterface().loadEnderChest(e.getPlayer(), inv);
					}
					enderchest.getDataHandler().setData(e.getPlayer().getUniqueId(), inv);
				}
			}
			
		});
	}
	
	@EventHandler
	public void onPlayerDisconnectEvent(PlayerQuitEvent e) {
		enderchest.getDataHandler().removeData(e.getPlayer().getUniqueId());
	}
	
	//Player click event
	@EventHandler
	public void onPlayerClickEvent(PlayerInteractEvent e) {
		if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		if (e.getClickedBlock().getType() != Material.ENDER_CHEST) {
			return;
		}
		e.setCancelled(true);
		
		enderchest.getEnderChestUtils().openMenu(e.getPlayer());
	}
	
	private void saveEnderchest(final Inventory inv, final Player p, final UUID u) {
		Bukkit.getScheduler().runTaskAsynchronously(enderchest, new Runnable() {

			@Override
			public void run() {
				if (u == null) {
					enderchest.getStorageInterface().saveEnderChest(p, inv);
				} else {
					enderchest.getStorageInterface().saveEnderChest(u, p, inv);
					enderchest.admin.remove(inv);
				}
			}
			
		});
	}
	
	//Player inventory close event
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();
		if (p != null) {
			if (e.getInventory() != null) {
				try {
					if (enderchest.getDataHandler().isLiveEnderchest(e.getInventory()) == true) {
						enderchest.getSoundHandler().sendEnderchestCloseSound(p);
						if (enderchest.admin.containsKey(e.getInventory()) == true) {
							UUID u = enderchest.admin.get(e.getInventory());
							enderchest.getDataHandler().setData(u, e.getInventory());
							saveEnderchest(e.getInventory(),(Player) e.getPlayer(), u);
						} else {
							enderchest.getDataHandler().setData(e.getPlayer().getUniqueId(), e.getInventory());
							saveEnderchest(e.getInventory(), (Player) e.getPlayer(), null);
						}
					} else if (enderchest.admin.containsKey(e.getInventory()) == true) {
						enderchest.getSoundHandler().sendEnderchestCloseSound(p);
						saveEnderchest(e.getInventory(),(Player) e.getPlayer(), enderchest.admin.get(e.getInventory()));
						enderchest.admin.remove(e.getInventory());
					}
				} catch (Exception ex) {
					EnderChest.log.severe("Error saving enderchest data for player: " + p.getName() + " . Error: " + ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}

}
