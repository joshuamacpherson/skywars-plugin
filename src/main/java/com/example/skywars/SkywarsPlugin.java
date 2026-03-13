package com.example.skywars;

import com.example.skywars.listeners.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class SkywarsPlugin extends JavaPlugin {

    private GameManager gameManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.gameManager = new GameManager(this);
        getServer().getPluginManager()
                .registerEvents(new PlayerListener(gameManager), this);

        World world = Bukkit.getWorld("world");
        if (world == null) {
            getLogger().severe("World 'world' not found! Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        world.setSpawnLocation(0, 170, 0);

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