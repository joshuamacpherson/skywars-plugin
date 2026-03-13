package com.example.skywars.listeners;

import com.example.skywars.GameManager;
import com.example.skywars.GameState;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final GameManager gameManager;

    public PlayerListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.getInventory().clear();
        player.setHealth(player.getAttribute(Attribute.MAX_HEALTH).getValue());
        player.setFoodLevel(20);
        player.setSaturation(20);
        gameManager.addPlayer(player);
        player.setGameMode(GameMode.ADVENTURE);
        if (gameManager.getState() == GameState.IN_GAME) {
            player.setGameMode(GameMode.SPECTATOR);
        } else {
            player.teleport(gameManager.getLobbyLocation());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        gameManager.removePlayer(event.getPlayer());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        gameManager.eliminatePlayer(event.getEntity());
        event.getPlayer().setGameMode(GameMode.SPECTATOR);
        event.getPlayer().teleport(new Location(Bukkit.getWorld("world"),0, 50, 0));
    }
}