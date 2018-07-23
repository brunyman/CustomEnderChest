package net.craftersland.customenderchest;

import java.util.HashSet;
import java.util.Set;
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
import org.bukkit.inventory.ItemStack;

public class PlayerHandler implements Listener {

    private EnderChest enderchest;
    private Set<UUID> interactCooldown = new HashSet<UUID>();

	public PlayerHandler(EnderChest enderchest) {
		this.enderchest = enderchest;
	}

	@EventHandler
	public void onPlayerJoinEvent(final PlayerJoinEvent e) {
		Bukkit.getScheduler().runTaskAsynchronously(enderchest, new Runnable() {

			@Override
			public void run() {
				if (e.getPlayer().isOnline()) {
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

	@EventHandler
	public void onPlayerInteract(final PlayerInteractEvent e) {
		if (e.getClickedBlock() != null) {
			if (e.getClickedBlock().getType() == Material.ENDER_CHEST) {
				if (enderchest.getConfigHandler().getBoolean("settings.disable-enderchest-click") == false) {
					if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
						if (interactCooldown.contains(e.getPlayer().getUniqueId()) == false) {
							if (e.getPlayer().isSneaking() == false) {
								e.setCancelled(true);
								addInteractCooldown(e.getPlayer().getUniqueId());
								enderchest.getEnderChestUtils().openMenu(e.getPlayer());
							} else {
								if (EnderChest.is19Server == false) {
									if (hasItemInHand(e.getPlayer().getItemInHand()) == false) {
										e.setCancelled(true);
										addInteractCooldown(e.getPlayer().getUniqueId());
										enderchest.getEnderChestUtils().openMenu(e.getPlayer());
									}
								} else {
									if (hasItemInHand(e.getPlayer().getInventory().getItemInMainHand()) == false && hasItemInHand(e.getPlayer().getInventory().getItemInOffHand()) == false) {
										e.setCancelled(true);
										addInteractCooldown(e.getPlayer().getUniqueId());
										enderchest.getEnderChestUtils().openMenu(e.getPlayer());
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private void addInteractCooldown(final UUID u) {
		interactCooldown.add(u);
		Bukkit.getScheduler().runTaskLaterAsynchronously(enderchest, new Runnable() {

			@Override
			public void run() {
				interactCooldown.remove(u);
			}
			
		}, 2L);
	}

	private boolean hasItemInHand(ItemStack item) {
		if (item == null) {
			return false;
		} else {
			if (item.getType() == Material.AIR) {
				return false;
			}
		}
		return true;
	}

	private void saveEnderchest(final Inventory inv, final Player p, final UUID u) {
		Bukkit.getScheduler().runTaskAsynchronously(enderchest, new Runnable() {

			@Override
			public void run() {
				if (u == null) {
					enderchest.getStorageInterface().saveEnderChest(p, inv);
				} else {
					enderchest.getStorageInterface().saveEnderChest(u, inv);
					//enderchest.admin.remove(inv);
				}
			}
			
		});
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();
		if (p != null) {
			if (e.getInventory() != null) {
				try {
					if (enderchest.getDataHandler().isLiveEnderchest(e.getInventory()) == true) {
						if (enderchest.admin.containsKey(e.getInventory()) == true) {
							UUID u = enderchest.admin.get(e.getInventory());
							if (u.equals(e.getPlayer().getUniqueId()) == false) {
								enderchest.admin.remove(e.getInventory());
							}
							//enderchest.getDataHandler().setData(u, e.getInventory());
							//saveEnderchest(e.getInventory(), (Player) e.getPlayer(), u);
						} else {
							enderchest.getDataHandler().setData(e.getPlayer().getUniqueId(), e.getInventory());
							saveEnderchest(e.getInventory(), (Player) e.getPlayer(), null);
						}
					} else if (enderchest.admin.containsKey(e.getInventory()) == true) {
						saveEnderchest(e.getInventory(), (Player) e.getPlayer(), enderchest.admin.get(e.getInventory()));
						enderchest.admin.remove(e.getInventory());
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					EnderChest.log.severe("Error saving enderchest data for player: " + p.getName() + ", error: " + ex.getMessage());
				}
			}
		}
	}

}
