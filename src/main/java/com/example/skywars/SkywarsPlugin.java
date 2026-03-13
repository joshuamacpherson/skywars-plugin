package com.example.skywars;

import com.example.skywars.listeners.PlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

public class SkywarsPlugin extends JavaPlugin {

    private GameManager gameManager;

    @Override
    public void onEnable() {
        this.gameManager = new GameManager(this);
        getServer().getPluginManager()
                .registerEvents(new PlayerListener(gameManager), this);
        getLogger().info("SkyWars enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("SkyWars disabled!");
    }

    public GameManager getGameManager() {
        return gameManager;
    }
}