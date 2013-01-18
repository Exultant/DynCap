package com.untamedears.DynCap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
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

		// Give the console permission
		ConsoleCommandSender console = getServer().getConsoleSender();
		console.addAttachment(this, "dyncap.console", true);
	}

	public void onDisable() {}

	@EventHandler(priority=EventPriority.MONITOR)
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
	}

	public void setPlayerCap(int cap) {
		dynamicPlayerCap = cap;
		updatePlayerCap(getPlayerCount());
	}

	public int getPlayerCap() {
		return dynamicPlayerCap;
	}

	private void updatePlayerCap(int playerCount) {
		if (playerCount >= dynamicPlayerCap) {
			setWhitelist(true, playerCount);
		} else if (playerCount < dynamicPlayerCap) {
			setWhitelist(false, playerCount);
		}
	}

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
	}

	public int getPlayerCount() {
		return this.getServer().getOnlinePlayers().length;
	}
}
