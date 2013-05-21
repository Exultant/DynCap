package com.untamedears.DynCap;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class DynCapPlugin extends JavaPlugin implements Listener {
	private DynCapCommands commands;
	private int dynamicPlayerCap = 1000;
	private Logger log;
	private boolean whiteListEnabled = false;
	private List <QueueItem> loginQueue = new ArrayList<QueueItem>();

	public void onEnable() {
		log = this.getLogger();
		commands = new DynCapCommands(this, log);
		
		Bukkit.getPluginManager().registerEvents(this, this);
		for (String command : getDescription().getCommands().keySet()) {
			getCommand(command).setExecutor(commands);
		}

		// Give the console permission
		ConsoleCommandSender console = getServer().getConsoleSender();
		console.addAttachment(this, "dyncap.console", true);
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
		{
		    @Override  
		    public void run() 
		    {
		    	removeOldQueueItems(loginQueue, 60);
		    }}, 0L, 20);
	}

	public void onDisable() {}
	
	@EventHandler
	public void onServerListPingEvent(ServerListPingEvent event) {
		event.setMaxPlayers(getPlayerCap());
	}

	/*@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		updatePlayerCap(getPlayerCount());
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		updatePlayerCap(getPlayerCount() - 1);
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerKickEvent(PlayerKickEvent event) {
		updatePlayerCap(getPlayerCount() - 1);
	}*/

	public void setPlayerCap(int cap) {
		dynamicPlayerCap = cap;
		//updatePlayerCap(getPlayerCount());
	}

	public int getPlayerCap() {
		return dynamicPlayerCap;
	}

	/*private void updatePlayerCap(int playerCount) {
		if (playerCount >= dynamicPlayerCap) {
			//setWhitelist(true, playerCount);
		} else if (playerCount < dynamicPlayerCap) {
			//setWhitelist(false, playerCount);
		}
	}*/
/*
	private void setWhitelist(boolean enabled, int playerCount) {
		if ((enabled && !whiteListEnabled) || (!enabled && whiteListEnabled)) {
			Integer cap = getPlayerCap();
			String state_message = "disabled";
			if (enabled) {
				state_message = "enabled";
			}
			String message = String.format(
					"%d/%d players online dynamic cap %s.",
					playerCount, cap, state_message);
			log.info(message);
		}
		whiteListEnabled = enabled;
		Bukkit.setWhitelist(enabled);
	}*/

	public int getPlayerCount() {
		return this.getServer().getOnlinePlayers().length;
	}
	
	@EventHandler(priority=EventPriority.LOWEST, ignoreCancelled = false)
	public void onAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) 
	{
		//log.info("login event called!");
		String playerName = event.getName().toLowerCase();
		int position = getQueuePosition(loginQueue, playerName);
		//log.info("posistion is:" + position);
		//if the server is not full, and there is no queue
		if (getPlayerCount() < getPlayerCap() && loginQueue.isEmpty())
		{
			//log.info("allowed " + playerName + " to join, server is not full and has no queue!");
			event.allow();
			return;
		}
		//server is either full, or has a queue
		else
		{
			//if the server has a queue, but there is enough space for the player
			if(position != -1 && position + 1 <= getPlayerCap() - getPlayerCount())
			{
				//log.info("allowed " + playerName + " to join, server is not full and he is in a queue!");
				event.allow();
				loginQueue.remove(position);
				return;
			}
			//if the server has a queue, and there is not enough space for the player
			else if (position != -1)
			{
				if (loginQueue.get(position).getSecondsSinceLastAttempt() < 5)
				{
					loginQueue.remove(position);
					event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL, "You rejoined way to quick, you have been moved to the end of the login queue. Next time please join no less than every 10 seconds.");
					return;
				}
				else
				{
				loginQueue.get(position).updateDate();
				event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL, "Your posistion in the queue is: " + (position + 1) + ". Please try again in no less than 10 seconds, and no more than 60 seconds.");
				return;
				}
			}
			else
			{
				//log.info("disallowed " + playerName + " added him to queue");
				QueueItem queueItem = new QueueItem(playerName);
				loginQueue.add(queueItem);
				event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL, "The server is full, you have been added to the login queue. Your current position is: " + loginQueue.size() + ". Please try again in no less than 10 seconds, and no more than 60 seconds.");
				return;
			}
		}
 
	}
	//returning -1  means error/not contained
	private int getQueuePosition(List<QueueItem> queue, String name)
	{
		if (queue.isEmpty())
		{
			//log.info("queue is empty!");
			return -1;
		}
		for (int x = 0; x < queue.size(); x++)
		{
			//log.info("x is:" + x + " and queueItem name is " + queue.get(x).getName() + " while paramter is " + name);
			if (queue.get(x).getName().equalsIgnoreCase(name))
			{
				return x;
			}
		}
		return -1;
	}
	//timeOut is in seconds
	private void removeOldQueueItems(List<QueueItem> queue, int timeOut)
	{
		if (queue.isEmpty())
		{
			return;
		}
		for (int x = 0; x< queue.size(); x++)
		{
			if (queue.get(x).getSecondsSinceLastAttempt() > timeOut)
			{
				queue.remove(x);
				x--;
			}
		}
	}
}

