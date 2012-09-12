package com.untamedears.DynCap;

import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DynCapCommands implements CommandExecutor {
	
	private DynCapPlugin plugin;
	private Logger log;
	
	public DynCapCommands(DynCapPlugin p, Logger l) {
		this.plugin = p;
		this.log = l;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//commands can only be issued from the console
		if (sender instanceof Player) {
			return false;
		}
		if (label.equalsIgnoreCase("setcap")) {
			return setcapCmd(args);
		} else if (label.equalsIgnoreCase("getcap")) {
			return getcapCmd();
		}
		return false;
	}
	
	private boolean setcapCmd(String[] args) {
		if (args.length < 1 || args.length > 1) { return false; }
		int cap = 1000;
		try {
			cap = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return false;
		}
		plugin.setPlayerCap(cap);
		Integer playerCap = plugin.getPlayerCap();
		String message = "Dynamic player cap set to: "+playerCap.toString();
		log.info(message);
		return true;
	}
	
	private boolean getcapCmd() {
		Integer playerCap = plugin.getPlayerCap();
		Integer playerCount = plugin.getPlayerCount();
		String message = playerCount.toString() + " / " + playerCap.toString() + " players online.";
		log.info(message);
		return true;
	}

}
