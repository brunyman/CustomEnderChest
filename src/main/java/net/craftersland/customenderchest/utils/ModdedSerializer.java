package net.craftersland.customenderchest.utils;

import java.io.IOException;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.utility.StreamSerializer;

import net.craftersland.customenderchest.EnderChest;

public class ModdedSerializer {
	
	//private EnderChest pl;
	
	public ModdedSerializer(EnderChest pl) {
		//this.pl = pl;
	}
	
	public String toBase64(ItemStack[] itemStacks) throws IOException {
		StringBuilder stringBuilder = new StringBuilder();
	    for (int i = 0; i < itemStacks.length; i++) {
	      if (i > 0) {
	        stringBuilder.append(";");
	      }
	      if ((itemStacks[i] != null) && (itemStacks[i].getType() != Material.AIR)) {
	    	  stringBuilder.append(StreamSerializer.getDefault().serializeItemStack(itemStacks[i]));
	      }
	    }
	    return stringBuilder.toString();
    }
	
	public ItemStack[] fromBase64(String data) throws IOException {
		String[] strings = data.split(";");
	    ItemStack[] itemStacks = new ItemStack[strings.length];
	    for (int i = 0; i < strings.length; i++) {
	      if (!strings[i].equals("")) {
	    	  try {
	    		  itemStacks[i] = StreamSerializer.getDefault().deserializeItemStack(strings[i]);
	    	  } catch (Exception e) {
	    		  EnderChest.log.warning("Error decoding item using ProtocolLib, Error: " + e.getMessage());
	    	  }
	      }
	    }
	    return itemStacks;
    }

}
