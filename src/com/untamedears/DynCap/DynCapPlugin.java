package com.untamedears.DynCap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class DynCapPlugin extends JavaPlugin implements Listener {
	private DynCapCommands commands;
	private int dynamicPlayerCap = 1000;
	private Logger log;
	private boolean whiteListEnabled = false;
	
	public void onEnable() {
		log = this.getLogger();
		commands = new DynCapCommands(this, log);
		
		Bukkit.getPluginManager().registerEvents(this, this);
		for (String command : getDescription().getCommands().keySet()) {
			getCommand(command).setExecutor(commands);
		}
	}
	
	public void onDisable() {
		
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		updatePlayerCap();
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		updatePlayerCap();
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerKickEvent(PlayerKickEvent event) {
		updatePlayerCap();
	}
	
	public void setPlayerCap(int cap) {
		dynamicPlayerCap = cap;
		updatePlayerCap();
	}
	
	public int getPlayerCap() {
		return dynamicPlayerCap;
	}
	
	private void updatePlayerCap() {
		int playerCount = getPlayerCount();
		if (playerCount >= dynamicPlayerCap) {
			setWhitelist(true);
		} else if (playerCount < dynamicPlayerCap) {
			setWhitelist(false);
		}
	}
	
	private void setWhitelist(boolean enabled) {
		Bukkit.setWhitelist(enabled);
		Integer playerCount  = getPlayerCount();
		Integer cap = getPlayerCap();
		String message = playerCount.toString()+"/"+cap.toString()+" players online ";
		if (enabled && !whiteListEnabled) {
			message+=" dynamic cap enabled.";
			log.info(message);
		} else if (!enabled && whiteListEnabled) {
			message+=" dynamic cap disabled.";
			log.info(message);
		}
		whiteListEnabled = enabled;
	}
	
	public int getPlayerCount() {
		return this.getServer().getOnlinePlayers().length;
	}
}
