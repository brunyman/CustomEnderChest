package net.craftersland.customenderchest;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
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
		
		Player p = e.getPlayer();
		if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		
		Block b = e.getClickedBlock();
		if ((p.isSneaking()) && (p.getItemInHand().getType() != Material.AIR)) {
			return;
		}
		if (b.getType() != Material.ENDER_CHEST) {
			return;
		}
		e.setCancelled(true);
		
		openMenu(p);
			
	}
	
	//Player inventory close event
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		HumanEntity hE = e.getPlayer();
		Player p = (Player)hE;
		Inventory inv = e.getInventory();
		
		if (enderchest.admin.containsKey(inv.getTitle())) {
			p.playSound(p.getLocation(), Sound.CHEST_CLOSE, 1, 1);
			enderchest.getStorageInterface().saveEnderChest(enderchest.admin.get(inv.getTitle()), p, inv);
			enderchest.admin.remove(inv.getTitle());
			return;
		}
		if (!inv.getTitle().matches(enderchest.getEnderChestUtils().getTitle(p))) {
			return;
		}
		p.playSound(p.getLocation(), Sound.CHEST_CLOSE, 1, 1);
		enderchest.getStorageInterface().saveEnderChest(p, inv);
	}
	
	//Opening the enderchest
	public void openMenu(Player p) {
		//Cancel vanilla enderchest
		p.closeInventory();
				
		int size = enderchest.getEnderChestUtils().getSize(p);
		//No enderchest permission
		if (size == 0) {
			enderchest.getConfigHandler().printMessage(p, "chatMessages.noPermission");
			p.playSound(p.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
			return;
		}
					
		String enderChestTitle = enderchest.getEnderChestUtils().getTitle(p);
		Inventory inv = Bukkit.getServer().createInventory(p, size, enderChestTitle);
		//Load enderchest inventory from data source
		enderchest.getStorageInterface().loadEnderChest(p, inv);
		//Open the enderchest inventory
		p.playSound(p.getLocation(), Sound.CHEST_OPEN, 1, 1);
		p.openInventory(inv);
	}

}
