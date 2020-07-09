package net.craftersland.customenderchest;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundHandler {
	
	private EnderChest pl;
	
	public SoundHandler(EnderChest pl) {
		this.pl = pl;
	}
	
	public void sendAnvilLandSound(Player p) {
		if (pl.getConfigHandler().getBoolean("settings.disable-sounds") == true) return;
		if (EnderChest.is19Server == true) {
			p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 1F, 1F);
		} else {
			p.playSound(p.getLocation(), Sound.valueOf("ANVIL_LAND"), 1F, 1F);
		}
	}
	
	public void sendCompleteSound(Player p) {
		if (pl.getConfigHandler().getBoolean("settings.disable-sounds") == true) return;
		if (EnderChest.is19Server == true) {
			p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
		} else {
			p.playSound(p.getLocation(), Sound.valueOf("LEVEL_UP"), 1F, 1F);
		}
	}
	
	public void sendEnderchestCloseSound(Player p) {
		if (pl.getConfigHandler().getBoolean("settings.disable-sounds") == true) return;
		if (EnderChest.is13Server == true) {
			p.playSound(p.getLocation(), Sound.BLOCK_ENDER_CHEST_CLOSE, 1F, 1F);
		} else if (EnderChest.is19Server == true) {
			p.playSound(p.getLocation(), Sound.valueOf("BLOCK_ENDERCHEST_CLOSE"), 1F, 1F);
		} else {
			p.playSound(p.getLocation(), Sound.valueOf("CHEST_CLOSE"), 1F, 1F);
		}
	}
	
	public void sendEnderchestOpenSound(Player p) {
		if (pl.getConfigHandler().getBoolean("settings.disable-sounds") == true) return;
		if (EnderChest.is13Server == true) {
			p.playSound(p.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1F, 1F);
		} else if (EnderChest.is19Server == true) {
			p.playSound(p.getLocation(), Sound.valueOf("BLOCK_ENDERCHEST_OPEN"), 1F, 1F);
		} else {
			p.playSound(p.getLocation(), Sound.valueOf("CHEST_OPEN"), 1F, 1F);
		}
	}
	
	public void sendFailedSound(Player p) {
		if (pl.getConfigHandler().getBoolean("settings.disable-sounds") == true) return;
		if (EnderChest.is13Server == true) {
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 3F, 3F);
		} else if (EnderChest.is19Server == true) {
			p.playSound(p.getLocation(), Sound.valueOf("BLOCK_NOTE_PLING"), 3F, 3F);
		} else {
			p.playSound(p.getLocation(), Sound.valueOf("NOTE_PLING"), 3F, 3F);
		}
	}

}
