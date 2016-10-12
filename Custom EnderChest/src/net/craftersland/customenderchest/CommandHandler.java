package net.craftersland.customenderchest;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class CommandHandler implements CommandExecutor {
	
private EnderChest enderchest;
	
	public CommandHandler(EnderChest enderchest) {
		this.enderchest = enderchest;
	}
	
	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String cmdlabel, final String[] args) {
		final Player p;

			if (cmdlabel.equalsIgnoreCase("customec") || cmdlabel.equalsIgnoreCase("customenderchest")) {
				if (args.length == 0) {
					if (sender instanceof Player) {
						p = (Player) sender;
						sendHelp(p);
						return true;
					} else {
						sendConsoleHelp(sender);
						return false;
					}
				}
				if (args.length == 1) {
						if (args[0].equalsIgnoreCase("open")) {
							if (sender instanceof Player) {
								p = (Player) sender;
								if (p.hasPermission("CustomEnderChest.commands") || p.hasPermission("CustomEnderChest.admin")) {
									int size = enderchest.getEnderChestUtils().getSize(p);
									if (size == 0) {
										enderchest.getConfigHandler().printMessage(p, "chatMessages.noPermission");
										if (EnderChest.is19Server == true) {
											p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1.0F, 1.0F);
										} else {
											p.playSound(p.getLocation(), Sound.valueOf("NOTE_PLING"), 1.0F, 1.0F);
										}
										return false;
									}
									String enderChestTitle = enderchest.getEnderChestUtils().getTitle(p);
									Inventory inv = Bukkit.getServer().createInventory(p, size, enderChestTitle);
									enderchest.getStorageInterface().loadEnderChest(p, inv);
									if (EnderChest.is19Server == true) {
										p.playSound(p.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0F, 1.0F);
									} else {
										p.playSound(p.getLocation(), Sound.valueOf("CHEST_OPEN"), 1.0F, 1.0F);
									}
									p.openInventory(inv);
									return true;
								}
								if (p.hasPermission("CustomEnderChest.admin")) {
									if (EnderChest.is19Server == true) {
										p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1.0F, 1.0F);
									} else {
										p.playSound(p.getLocation(), Sound.valueOf("NOTE_PLING"), 1.0F, 1.0F);
									}
									enderchest.getConfigHandler().printMessage(p, "chatMessages.openCmdUsage");
									return false;
								}
								if (EnderChest.is19Server == true) {
									p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1.0F, 1.0F);
								} else {
									p.playSound(p.getLocation(), Sound.valueOf("NOTE_PLING"), 1.0F, 1.0F);
								}
								enderchest.getConfigHandler().printMessage(p, "chatMessages.noPermission");
								return false;
							} else {
								sender.sendMessage(ChatColor.DARK_RED + ">> " + ChatColor.RED + "You can't run this command by console!");
								return false;
							}
					    }
					if (args[0].equalsIgnoreCase("delete")) {
						if (sender instanceof Player) {
							p = (Player) sender;
							if (p.hasPermission("CustomEnderChest.admin")) {
								if (EnderChest.is19Server == true) {
									p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1.0F, 1.0F);
								} else {
									p.playSound(p.getLocation(), Sound.valueOf("NOTE_PLING"), 1.0F, 1.0F);
								}
								enderchest.getConfigHandler().printMessage(p, "chatMessages.deleteCmdUsage");
								return false;
							}
							if (EnderChest.is19Server == true) {
								p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1.0F, 1.0F);
							} else {
								p.playSound(p.getLocation(), Sound.valueOf("NOTE_PLING"), 1.0F, 1.0F);
							}
							enderchest.getConfigHandler().printMessage(p, "chatMessages.noPermission");
							return false;
						} else {
							sender.sendMessage(ChatColor.DARK_RED + ">> " + ChatColor.RED + "Usage example: " + ChatColor.GRAY + "/customec delete John" + ChatColor.RED + " or " + ChatColor.GRAY + "/customec delete f694517d-d6cf-32f1-972b-dfc677ceac45");
							return true;
						}
					}
					if (args[0].equalsIgnoreCase("reload")) {
						if (sender instanceof Player) {
							p = (Player) sender;
							if (p.hasPermission("CustomEnderChest.admin")) {
								try {
									enderchest.getConfig().load(new File("plugins"+System.getProperty("file.separator")+"CustomEnderChest"+System.getProperty("file.separator")+"config.yml"));
								} catch (Exception e) {
									enderchest.getConfigHandler().printMessage(p, "chatMessages.reloadFail");
									if (EnderChest.is19Server == true) {
										p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1.0F, 1.0F);
									} else {
										p.playSound(p.getLocation(), Sound.valueOf("NOTE_PLING"), 1.0F, 1.0F);
									}
									e.printStackTrace();
									return false;
								}
								if (EnderChest.is19Server == true) {
									p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
								} else {
									p.playSound(p.getLocation(), Sound.valueOf("LEVEL_UP"), 1.0F, 1.0F);
								}
								enderchest.getConfigHandler().printMessage(p, "chatMessages.reload");
								return true;
							}
							if (EnderChest.is19Server == true) {
								p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1.0F, 1.0F);
							} else {
								p.playSound(p.getLocation(), Sound.valueOf("NOTE_PLING"), 1.0F, 1.0F);
							}
							enderchest.getConfigHandler().printMessage(p, "chatMessages.noPermission");
							return false;
						} else {
							try {
								enderchest.getConfig().load(new File("plugins"+System.getProperty("file.separator")+"CustomEnderChest"+System.getProperty("file.separator")+"config.yml"));
							} catch (Exception e) {
								sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + ">> " + ChatColor.RED + "Could not load config! Check logs!");
								e.printStackTrace();
								return false;
							}
							sender.sendMessage(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + ">> " + ChatColor.GREEN + "Configuration reloaded!");
							return true;
						}
					}
					if (sender instanceof Player) {
						p = (Player) sender;
						sendHelp(p);
						return false;
					} else {
						sendConsoleHelp(sender);
						return false;
					}
				}
				
				if (args.length == 2) {
					if (args[0].equalsIgnoreCase("open")) {
						if (sender instanceof Player) {
							p = (Player) sender;
							if (p.hasPermission("CustomEnderChest.admin")) {
								Player target = Bukkit.getPlayer(args[1]);
								if (target != null) {
									if (target.isOnline()) {
										if (!enderchest.getStorageInterface().hasDataFile(target.getUniqueId())) {
											if (EnderChest.is19Server == true) {
												p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1.0F, 1.0F);
											} else {
												p.playSound(p.getLocation(), Sound.valueOf("NOTE_PLING"), 1.0F, 1.0F);
											}
											enderchest.getConfigHandler().printMessage(p, "chatMessages.noEnderchest");
											return false;
										}
										int size = enderchest.getStorageInterface().loadSize(target.getUniqueId());
										String enderChestTitle = enderchest.getEnderChestUtils().getCmdTitle(target);
										Inventory inv = Bukkit.getServer().createInventory(p, size, enderChestTitle);
										enderchest.getStorageInterface().loadEnderChest(target, inv);
										if (EnderChest.is19Server == true) {
											p.playSound(p.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0F, 1.0F);
										} else {
											p.playSound(p.getLocation(), Sound.valueOf("CHEST_OPEN"), 1.0F, 1.0F);
										}
										p.openInventory(inv);
										enderchest.admin.put(enderChestTitle, target.getUniqueId());
										return true;
								        }
								    } else {
								    	try {
								    		UUID targetUUID = UUID.fromString(args[1]);
									    	if (!enderchest.getStorageInterface().hasDataFile(targetUUID)) {
									    		if (EnderChest.is19Server == true) {
													p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1.0F, 1.0F);
												} else {
													p.playSound(p.getLocation(), Sound.valueOf("NOTE_PLING"), 1.0F, 1.0F);
												}
												enderchest.getConfigHandler().printMessage(p, "chatMessages.openUuidFail");
												return false;
											}
									    	int size = enderchest.getStorageInterface().loadSize(targetUUID);
											String enderChestTitle = enderchest.getEnderChestUtils().getCmdTitle(targetUUID);
											Inventory inv = Bukkit.getServer().createInventory(p, size, enderChestTitle);
											enderchest.getStorageInterface().loadEnderChest(targetUUID, inv);
											if (EnderChest.is19Server == true) {
												p.playSound(p.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0F, 1.0F);
											} else {
												p.playSound(p.getLocation(), Sound.valueOf("CHEST_OPEN"), 1.0F, 1.0F);
											}
											p.openInventory(inv);
											enderchest.admin.put(enderChestTitle, targetUUID);
											return true;
								    	} catch (Exception e) {
								    		if (EnderChest.is19Server == true) {
												p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1.0F, 1.0F);
											} else {
												p.playSound(p.getLocation(), Sound.valueOf("NOTE_PLING"), 1.0F, 1.0F);
											}
								    		enderchest.getConfigHandler().printMessage(p, "chatMessages.openNameOffline");
								    		return false;
								    	}
								    }
							}
							if (EnderChest.is19Server == true) {
								p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1.0F, 1.0F);
							} else {
								p.playSound(p.getLocation(), Sound.valueOf("NOTE_PLING"), 1.0F, 1.0F);
							}
							enderchest.getConfigHandler().printMessage(p, "chatMessages.noPermission");
							return false;
						} else {
							sender.sendMessage(ChatColor.DARK_RED + ">> " + ChatColor.RED + "You can't run this command by console!");
							return false;
						}
					}
					if (args[0].equalsIgnoreCase("delete")) {
						if (sender instanceof Player) {
							p = (Player) sender;
							if (p.hasPermission("CustomEnderChest.admin")) {
								Player target = Bukkit.getPlayer(args[1]);
								if (target != null) {
									if (target.isOnline()) {
										if (!enderchest.getStorageInterface().hasDataFile(target.getUniqueId())) {
											if (EnderChest.is19Server == true) {
												p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1.0F, 1.0F);
											} else {
												p.playSound(p.getLocation(), Sound.valueOf("NOTE_PLING"), 1.0F, 1.0F);
											}
											enderchest.getConfigHandler().printMessage(p, "chatMessages.noEnderchest");
											return false;
										}
										enderchest.getStorageInterface().deleteDataFile(target.getUniqueId());
										if (EnderChest.is19Server == true) {
											p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
										} else {
											p.playSound(p.getLocation(), Sound.valueOf("LEVEL_UP"), 1.0F, 1.0F);
										}
										enderchest.getConfigHandler().printMessage(p, "chatMessages.delete");
										return true;
									}
								} else {
									try {
										UUID targetUUID = UUID.fromString(args[1]);
										if (!enderchest.getStorageInterface().hasDataFile(targetUUID)) {
											if (EnderChest.is19Server == true) {
												p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1.0F, 1.0F);
											} else {
												p.playSound(p.getLocation(), Sound.valueOf("NOTE_PLING"), 1.0F, 1.0F);
											}
											enderchest.getConfigHandler().printMessage(p, "chatMessages.openUuidFail");
											return false;
										}
										enderchest.getConfigHandler().printMessage(p, "chatMessages.delete");
										enderchest.getStorageInterface().deleteDataFile(targetUUID);
										if (EnderChest.is19Server == true) {
											p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
										} else {
											p.playSound(p.getLocation(), Sound.valueOf("LEVEL_UP"), 1.0F, 1.0F);
										}
										return true;
									} catch (Exception e) {
										if (EnderChest.is19Server == true) {
											p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1.0F, 1.0F);
										} else {
											p.playSound(p.getLocation(), Sound.valueOf("NOTE_PLING"), 1.0F, 1.0F);
										}
										enderchest.getConfigHandler().printMessage(p, "chatMessages.deleteNameOffline");
							    		return false;
									}
								}

							}
							if (EnderChest.is19Server == true) {
								p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1.0F, 1.0F);
							} else {
								p.playSound(p.getLocation(), Sound.valueOf("NOTE_PLING"), 1.0F, 1.0F);
							}
							enderchest.getConfigHandler().printMessage(p, "chatMessages.noPermission");
							return false;
						} else {
							Player target = Bukkit.getPlayer(args[1]);
							if (target != null) {
								if (target.isOnline()) {
									if (!enderchest.getStorageInterface().hasDataFile(target.getUniqueId())) {
										sender.sendMessage(ChatColor.DARK_RED + ">> " + ChatColor.RED + "Player does not have and Ender Chest yet!");
										return false;
									}
									enderchest.getStorageInterface().deleteDataFile(target.getUniqueId());
									sender.sendMessage(ChatColor.DARK_GREEN + ">> " + ChatColor.GREEN + "Player " + ChatColor.GRAY + "" + target.getName() + ChatColor.GREEN + " enderchest data removed!");
									return true;
								}
							} else {
								try {
									UUID targetUUID = UUID.fromString(args[1]);
									if (!enderchest.getStorageInterface().hasDataFile(targetUUID)) {
										sender.sendMessage(ChatColor.DARK_RED + ">> " + ChatColor.RED + "Player does not have and Ender Chest or wrong UUID!");
										return false;
									}
									sender.sendMessage(ChatColor.DARK_GREEN + ">> " + ChatColor.GREEN + "Player " + ChatColor.GRAY + "" + enderchest.getStorageInterface().loadName(targetUUID) + ChatColor.GREEN + " enderchest data removed!");
									enderchest.getStorageInterface().deleteDataFile(targetUUID);
									return true;
								} catch (Exception e) {
									sender.sendMessage(ChatColor.DARK_RED + ">> " + ChatColor.RED + "Player offline or wrong UUID! Use: " + ChatColor.GRAY + "/customec delete <playerUUID>");
						    		return false;
								}
							}
						}
					}
					if (sender instanceof Player) {
						p = (Player) sender;
						sendHelp(p);
						return false;
					} else {
						sendConsoleHelp(sender);
						return false;
					}
				}
			}
			return false;
	}
	
	public void sendHelp(Player p) {
		if (EnderChest.is19Server == true) {
			p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0F, 1.0F);
		} else {
			p.playSound(p.getLocation(), Sound.valueOf("ANVIL_LAND"), 1.0F, 1.0F);
		}
		p.sendMessage(" ");
		p.sendMessage(ChatColor.DARK_PURPLE + "-=-=-=-=-=-=-=-< " + ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "CustomEnderChest" + ChatColor.DARK_PURPLE + " >-=-=-=-=-=-=-=-=-");
		if (p.hasPermission("CustomEnderChest.admin")) {
			p.sendMessage(" ");
			p.sendMessage(ChatColor.LIGHT_PURPLE + "        Open your enderchest:");
			p.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + ">> " + ChatColor.WHITE + "/customec open");
			p.sendMessage(" ");
			p.sendMessage(ChatColor.LIGHT_PURPLE + "        Open other player's Ender Chest:");
			p.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + ">> " + ChatColor.WHITE + "/customec open <playerName>" + ChatColor.GRAY + " - for online players.");
			p.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + ">> " + ChatColor.WHITE + "/customec open <playerUUID>" + ChatColor.GRAY + " - for offline players.");
			p.sendMessage(ChatColor.GRAY + "" + "      Example: " + ChatColor.WHITE + "/customec open John" + ChatColor.GRAY + " or " + ChatColor.WHITE + "/customec open f694517d-d6cf-32f1-972b-dfc677ceac45");
			p.sendMessage(" ");
			p.sendMessage(ChatColor.LIGHT_PURPLE + "        Delete other player's Ender Chest:");
			p.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + ">> " + ChatColor.WHITE + "/customec delete <playerName>" + ChatColor.GRAY + " - for online players.");
			p.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + ">> " + ChatColor.WHITE + "/customec delete <playerUUID>" + ChatColor.GRAY + " - for offline players.");
			p.sendMessage(ChatColor.GRAY + "" + "      Example: " + ChatColor.WHITE + "/customec delete John" + ChatColor.GRAY + " or " + ChatColor.WHITE + "/customec delete f694517d-d6cf-32f1-972b-dfc677ceac45");
			p.sendMessage(" ");
			p.sendMessage(ChatColor.LIGHT_PURPLE + "        Reload plugin config:");
			p.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + ">> " + ChatColor.WHITE + "/customec reload");
			p.sendMessage(" ");
			p.sendMessage(ChatColor.DARK_PURPLE + "-=-=-=-=-=-=-=-=-< " + ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Admin Help Page" + ChatColor.DARK_PURPLE + " >-=-=-=-=-=-=-=-=-");
			p.sendMessage(" ");
		} else {
			p.sendMessage(" ");
			p.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Get a better custom Ender Chest!");
			p.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "Plugin Download: " + ChatColor.WHITE + "Coming soon");
			p.sendMessage(" ");
			if (p.hasPermission("CustomEnderChest.commands")) {
				p.sendMessage(ChatColor.LIGHT_PURPLE + "        Open your enderchest:");
				p.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + ">> " + ChatColor.WHITE + "/customec open");
				p.sendMessage(" ");
			}
			p.sendMessage(ChatColor.DARK_PURPLE + "-=-=-=-=-=-=-=-=-=-< " + ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Help Page" + ChatColor.DARK_PURPLE + " >-=-=-=-=-=-=-=-=-=-");
			p.sendMessage(" ");
		}
	}
	
	public void sendConsoleHelp(CommandSender sender) {
		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.DARK_PURPLE + "-=-=-=-=-=-=-=-< " + ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "CustomEnderChest" + ChatColor.DARK_PURPLE + " >-=-=-=-=-=-=-=-=-");
			sender.sendMessage(" ");
			sender.sendMessage(ChatColor.LIGHT_PURPLE + "        Delete other player's Ender Chest:");
			sender.sendMessage(ChatColor.DARK_GRAY + ">> " + ChatColor.WHITE + "/customec delete <playerName>" + ChatColor.GRAY + " - for online players.");
			sender.sendMessage(ChatColor.DARK_GRAY + ">> " + ChatColor.WHITE + "/customec delete <playerUUID>" + ChatColor.GRAY + " - for offline players.");
			sender.sendMessage(ChatColor.GRAY + "" + "      Example: " + ChatColor.WHITE + "/customec delete John" + ChatColor.GRAY + " or " + ChatColor.WHITE + "/customec delete f694517d-d6cf-32f1-972b-dfc677ceac45");
			sender.sendMessage(" ");
			sender.sendMessage(ChatColor.LIGHT_PURPLE + "        Reload plugin config:");
			sender.sendMessage(ChatColor.DARK_GRAY + ">> " + ChatColor.WHITE + "/customec reload");
			sender.sendMessage(" ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-=-=-=-=-=-=-=-=-< " + ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Console Help Page" + ChatColor.DARK_PURPLE + " >-=-=-=-=-=-=-=-=-");
			sender.sendMessage(" ");
	}

}
