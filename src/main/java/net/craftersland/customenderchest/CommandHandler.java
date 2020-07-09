package net.craftersland.customenderchest;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
		Player p = null;

			if (cmdlabel.equalsIgnoreCase("customec") || cmdlabel.equalsIgnoreCase("customenderchest") || cmdlabel.equalsIgnoreCase("ec")) {
				if (args.length == 0) {
					if (sender instanceof Player) {
						p = (Player) sender;
						sendHelp(p);
						return true;
					} else {
						sendConsoleHelp(sender);
						return false;
					}
				} else if (args.length == 1) {
						if (args[0].equalsIgnoreCase("open")) {
							if (sender instanceof Player) {
								p = (Player) sender;
								if (p.hasPermission("CustomEnderChest.commands") || p.hasPermission("CustomEnderChest.admin")) {
									int size = enderchest.getEnderChestUtils().getSize(p);
									if (size == 0) {
										enderchest.getConfigHandler().printMessage(p, "chatMessages.noPermission");
										enderchest.getSoundHandler().sendFailedSound(p);
										return false;
									}
									if (enderchest.getDataHandler().hasJoinDelay(p)) {
										p.sendMessage(enderchest.getConfigHandler().getStringWithColor("chatMessages.prefix") + enderchest.getConfigHandler().getStringWithColor("chatMessages.joinDelay"));
										enderchest.getSoundHandler().sendFailedSound(p);
										return false;
									}
									enderchest.getEnderChestUtils().openMenu(p);
									return true;
								} else {
									enderchest.getSoundHandler().sendFailedSound(p);
									enderchest.getConfigHandler().printMessage(p, "chatMessages.noPermission");
								}
								/*if (p.hasPermission("CustomEnderChest.admin")) {
									enderchest.getSoundHandler().sendFailedSound(p);
									enderchest.getConfigHandler().printMessage(p, "chatMessages.openCmdUsage");
									return false;
								}*/
								return false;
							} else {
								sender.sendMessage(ChatColor.DARK_RED + ">> " + ChatColor.RED + "You can't run this command by console!");
								return false;
							}
					    } else if (args[0].equalsIgnoreCase("importfromflatfile")) {
					    	if (sender instanceof Player) {
					    		p = (Player) sender;
					    		if (p.hasPermission("CustomEnderChest.admin") == false) {
					    			enderchest.getSoundHandler().sendFailedSound(p);
									enderchest.getConfigHandler().printMessage(p, "chatMessages.noPermission");
									return true;
					    		}
					    	}
					    	enderchest.getFileToMysqlCmd().runCmd(sender, false);
					    } else if (args[0].equalsIgnoreCase("delete")) {
					    	if (sender instanceof Player) {
					    		p = (Player) sender;
					    		if (p.hasPermission("CustomEnderChest.admin")) {
					    			enderchest.getSoundHandler().sendFailedSound(p);
					    			enderchest.getConfigHandler().printMessage(p, "chatMessages.deleteCmdUsage");
					    			return true;
					    		}
					    		enderchest.getSoundHandler().sendFailedSound(p);
					    		enderchest.getConfigHandler().printMessage(p, "chatMessages.noPermission");
					    		return true;
					    	} else {
					    		sender.sendMessage(ChatColor.DARK_RED + ">> " + ChatColor.RED + "Usage example: " + ChatColor.GRAY + "/customec delete John" + ChatColor.RED + " or " + ChatColor.GRAY + "/customec delete f694517d-d6cf-32f1-972b-dfc677ceac45");
					    		return true;
					    	}
					} else if (args[0].equalsIgnoreCase("reload")) {
							if (sender instanceof Player) {
								p = (Player) sender;
								if (p.hasPermission("CustomEnderChest.admin")) {
									enderchest.getConfigHandler().loadConfig();
									enderchest.getSoundHandler().sendCompleteSound(p);
									enderchest.getConfigHandler().printMessage(p, "chatMessages.reload");
									return true;
								}
								enderchest.getSoundHandler().sendFailedSound(p);
								enderchest.getConfigHandler().printMessage(p, "chatMessages.noPermission");
								return true;
							} else {
								enderchest.getConfigHandler().loadConfig();
								sender.sendMessage(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + ">> " + ChatColor.GREEN + "Configuration reloaded!");
								return true;
							}
					} else {
						if (sender instanceof Player) {
							p = (Player) sender;
							sendHelp(p);
							return false;
						} else {
							sendConsoleHelp(sender);
							return false;
						}
					}
				} else if (args.length == 2) {
					if (args[0].equalsIgnoreCase("open")) {
						if (sender instanceof Player) {
							p = (Player) sender;
							if (p.hasPermission("CustomEnderChest.admin")) {
								Player target = Bukkit.getPlayer(args[1]);
								if (target != null) {
									if (target.isOnline()) {
										if (!enderchest.getStorageInterface().hasDataFile(target.getUniqueId())) {
											enderchest.getSoundHandler().sendFailedSound(p);
											enderchest.getConfigHandler().printMessage(p, "chatMessages.noEnderchest");
											return false;
										}
										Inventory inv = enderchest.getDataHandler().getData(target.getUniqueId());
										enderchest.getSoundHandler().sendEnderchestOpenSound(p);
										enderchest.admin.put(inv, target.getUniqueId());
										p.openInventory(inv);
										return true;
								        }
								    } else {
								    	try {
								    		UUID targetUUID = UUID.fromString(args[1]);
									    	if (!enderchest.getStorageInterface().hasDataFile(targetUUID)) {
									    		enderchest.getSoundHandler().sendFailedSound(p);
												enderchest.getConfigHandler().printMessage(p, "chatMessages.openUuidFail");
												return false;
											}
									    	int size = enderchest.getStorageInterface().loadSize(targetUUID);
											String enderChestTitle = enderchest.getEnderChestUtils().getCmdTitle(targetUUID);
											Inventory inv = Bukkit.getServer().createInventory(p, size, enderChestTitle);
											enderchest.getStorageInterface().loadEnderChest(targetUUID, inv);
											enderchest.getSoundHandler().sendEnderchestOpenSound(p);
											enderchest.admin.put(inv, targetUUID);
											p.openInventory(inv);
											return true;
								    	} catch (Exception e) {
								    		enderchest.getSoundHandler().sendFailedSound(p);
								    		enderchest.getConfigHandler().printMessage(p, "chatMessages.openNameOffline");
								    		return false;
								    	}
								    }
							}
							enderchest.getSoundHandler().sendFailedSound(p);
							enderchest.getConfigHandler().printMessage(p, "chatMessages.noPermission");
							return false;
						} else {
							sender.sendMessage(ChatColor.DARK_RED + ">> " + ChatColor.RED + "You can't run this command by console!");
							return false;
						}
					} else if (args[0].equalsIgnoreCase("importfromflatfile")) {
						if (args[1].equalsIgnoreCase("overwrite")) {
							if (sender instanceof Player) {
					    		p = (Player) sender;
					    		if (p.hasPermission("CustomEnderChest.admin") == false) {
					    			enderchest.getSoundHandler().sendFailedSound(p);
									enderchest.getConfigHandler().printMessage(p, "chatMessages.noPermission");
									return true;
					    		}
					    	}
					    	enderchest.getFileToMysqlCmd().runCmd(sender, true);
						} else {
							sender.sendMessage("Usage: /customec importfromflatfile overwrite");
						}
				    } else if (args[0].equalsIgnoreCase("delete")) {
						if (sender instanceof Player) {
							p = (Player) sender;
							if (p.hasPermission("CustomEnderChest.admin")) {
								Player target = Bukkit.getPlayer(args[1]);
								if (target != null) {
									if (target.isOnline()) {
										if (!enderchest.getStorageInterface().hasDataFile(target.getUniqueId())) {
											enderchest.getSoundHandler().sendFailedSound(p);
											enderchest.getConfigHandler().printMessage(p, "chatMessages.noEnderchest");
											return false;
										}
										enderchest.getStorageInterface().deleteDataFile(target.getUniqueId());
										enderchest.getSoundHandler().sendCompleteSound(p);
										enderchest.getConfigHandler().printMessage(p, "chatMessages.delete");
										return true;
									}
								} else {
									try {
										UUID targetUUID = UUID.fromString(args[1]);
										if (!enderchest.getStorageInterface().hasDataFile(targetUUID)) {
											enderchest.getSoundHandler().sendFailedSound(p);
											enderchest.getConfigHandler().printMessage(p, "chatMessages.openUuidFail");
											return false;
										}
										enderchest.getConfigHandler().printMessage(p, "chatMessages.delete");
										enderchest.getStorageInterface().deleteDataFile(targetUUID);
										enderchest.getSoundHandler().sendCompleteSound(p);
										return true;
									} catch (Exception e) {
										enderchest.getSoundHandler().sendFailedSound(p);
										enderchest.getConfigHandler().printMessage(p, "chatMessages.deleteNameOffline");
							    		return false;
									}
								}

							}
							enderchest.getSoundHandler().sendFailedSound(p);
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
					} else {
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
			}
			return false;
	}
	
	public void sendHelp(Player p) {
		enderchest.getSoundHandler().sendAnvilLandSound(p);
		for (String h : enderchest.getConfigHandler().getStringList("chatMessages.Help.header")) {
			p.sendMessage(h.replaceAll("&", "§"));
		}
		if (p.hasPermission("CustomEnderChest.admin")) {
			for (String h : enderchest.getConfigHandler().getStringList("chatMessages.Help.admin")) {
				p.sendMessage(h.replaceAll("&", "§"));
			}
		} else {
			for (String h : enderchest.getConfigHandler().getStringList("chatMessages.Help.user")) {
				p.sendMessage(h.replaceAll("&", "§"));
			}
			if (p.hasPermission("CustomEnderChest.commands")) {
				for (String h : enderchest.getConfigHandler().getStringList("chatMessages.Help.command")) {
					p.sendMessage(h.replaceAll("&", "§"));
				}
			}
			for (String h : enderchest.getConfigHandler().getStringList("chatMessages.Help.userFooter")) {
				p.sendMessage(h.replaceAll("&", "§"));
			}
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
