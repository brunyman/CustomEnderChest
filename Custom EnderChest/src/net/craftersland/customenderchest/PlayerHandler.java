package net.craftersland.customenderchest;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

public class PlayerHandler implements Listener {
	
    private EnderChest enderchest;
	
	public PlayerHandler(EnderChest enderchest) {
		this.enderchest = enderchest;
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
		
		openMenu(e.getPlayer());
	}
	
	//Player inventory close event
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();
		if (p != null) {
			if (e.getInventory() != null) {
				if (e.getInventory().getTitle() != null) {
					try {
						if (enderchest.admin.containsKey(e.getInventory().getTitle())) {
							enderchest.getSoundHandler().sendEnderchestCloseSound(p);
							enderchest.getStorageInterface().saveEnderChest(enderchest.admin.get(e.getInventory().getTitle()), p, e.getInventory());
							enderchest.admin.remove(e.getInventory().getTitle());
						} else if (e.getInventory().getTitle().matches(enderchest.getEnderChestUtils().getTitle(p))) {
							enderchest.getSoundHandler().sendEnderchestCloseSound(p);
							enderchest.getStorageInterface().saveEnderChest(p, e.getInventory());
						}
					} catch (Exception ex) {
						EnderChest.log.severe("Error saving enderchest data for player: " + p.getName() + " . Error: " + ex.getMessage());
						ex.printStackTrace();
					}
				}
			}
		}
	}
	
	//Opening the enderchest
	public void openMenu(Player p) {
		//Cancel vanilla enderchest
		p.closeInventory();
				
		int size = enderchest.getEnderChestUtils().getSize(p);
		//No enderchest permission
		if (size == 0) {
			enderchest.getConfigHandler().printMessage(p, "chatMessages.noPermission");
			enderchest.getSoundHandler().sendFailedSound(p);
			return;
		}
					
		String enderChestTitle = enderchest.getEnderChestUtils().getTitle(p);
		Inventory inv = Bukkit.getServer().createInventory(p, size, enderChestTitle);
		//Load enderchest inventory from data source
		enderchest.getStorageInterface().loadEnderChest(p, inv);
		//Open the enderchest inventory
		enderchest.getSoundHandler().sendEnderchestOpenSound(p);
		p.openInventory(inv);
	}

}
