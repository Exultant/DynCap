package com.untamedears.DynCap;

import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
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

		if (label.equalsIgnoreCase("getQueueSize") || label.equalsIgnoreCase("gqs")) {
			return getQueueSize(sender);
		}
		//commands below here can only be issued from the console
		if (sender instanceof Player) {
			return false;
		}
		
		if (label.equalsIgnoreCase("setCap")) {
			return setcapCmd(args);
		} else if (label.equalsIgnoreCase("getCap")) {
			return getcapCmd();
		} else if (label.equalsIgnoreCase("reloadQueue")) {
			return reloadQueueConfig();
		} else if (label.equalsIgnoreCase("getqueueinfo") || label.equalsIgnoreCase("gqi")) {
			return getQueueInfo(args);
		} else if (label.equalsIgnoreCase("getjoinaverage") || label.equalsIgnoreCase("gja")) {
			return getJoinAverage();
		} else if (label.equalsIgnoreCase("resetjoinaverage") || label.equalsIgnoreCase("rja")) {
			return resetJoinAverage();
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
	
	private boolean getQueueSize(CommandSender sender) 
	{
		Integer queueSize = plugin.getQueueSize();
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
			player.sendMessage(queueSize + " players are in the queue.");
			return true;
		}
		else if (sender instanceof ConsoleCommandSender)
		{
			log.info(queueSize + " players in the queue.");
			return true;
		}

		return false;
	}

	private boolean reloadQueueConfig() 
	{
		plugin.reloadConfig();
		plugin.initConfig();
		log.info("queue config reloaded");
		return true;
	}
	
	private boolean getQueueInfo(String[] args) 
	{
		if (args.length < 1 || args.length > 2) { return false; }
		
		if (args.length == 1)
		{
			//display info on 1 slot
			if (isInteger(args[0]))
			{
				int startIndex = (Integer.parseInt(args[0]) - 1);
				QueueItem queueItem = plugin.getQueueItem(startIndex);
				if (queueItem != null)
				{
					log.info("Player name is " + queueItem.getName() + " . " + queueItem.getSecondsSinceLastAttempt() + " seconds have elapsed since " + queueItem.getName() + "'s last join attempt.");
					return true;
				}
				else
				{
					log.info("Could not find information about index " + args[0]);
					return false;
				}
			}
			//search by name
			else
			{
				String playerName = args[0];
				int index = plugin.getQueuePosition(playerName);
				if (index != -1)
				{
					QueueItem queueItem = plugin.getQueueItem(index);
					log.info(playerName + " is number " + (index+1) + " in the login queue. " + queueItem.getSecondsSinceLastAttempt() + " seconds have elapsed since " + queueItem.getName() + "'s last join attempt.");
					return true;
				}
				else
				{
					log.info("Could not find " + playerName + " in the queue.");
					return false;
				}
			}
		}
		else if (args.length == 2)
		{
			if (isInteger(args[0]) && isInteger(args[1]))
			{
				int startIndex = (Integer.parseInt(args[0]) - 1);
				int endIndex = (Integer.parseInt(args[1]) - 1);
				for (int x = startIndex; x <= endIndex; x++)
				{
					QueueItem queueItem = plugin.getQueueItem(x);
					if (queueItem != null)
					{
						log.info("Player name is " + queueItem.getName() + " . " + queueItem.getSecondsSinceLastAttempt() + " seconds have elapsed since " + queueItem.getName() + "'s last join attempt.");
					}
					else
					{
						log.info("Could not find information about index " + (x + 1));
					}				
				}
				return true;
			}
			else
			{
				log.info("Please enter two integers above 0");
			}
		}
		return false;
	}
	
	private static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    // only got here if we didn't return false
	    return true;
	}
	

	private boolean resetJoinAverage() 
	{
		plugin.resetAverageTimeToJoin();
		log.info("Average join time has been reset!");
		return true;
	}

	private boolean getJoinAverage() 
	{
		float average = plugin.getAverageTimeToJoin();
		log.info("Average join time is " + average + " seconds.");
		return true;
	}

}
