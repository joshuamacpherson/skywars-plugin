package com.example.skywars;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

import net.kyori.adventure.text.minimessage.MiniMessage;

public class GameManager {

    private final int MAX_PLAYERS = 8;
    private int countdown;
    private final SkywarsPlugin plugin;
    private GameState state = GameState.WAITING;
    private final List<Player> allPlayers = new ArrayList<>();
    private List<Player> alivePlayers = new ArrayList<>();
    private BukkitTask countdownTask;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public GameManager(SkywarsPlugin plugin) {
        this.plugin = plugin;
    }

    public void eliminatePlayer(Player player) {
        broadcast("<red>" + player.getName() + " was eliminated!");
        player.setGameMode(GameMode.SPECTATOR);
        alivePlayers.remove(player);
        checkWinCondition();
    }

    public void addPlayer(Player player) {
        if (state != GameState.WAITING && state != GameState.COUNTDOWN) {
            broadcast("<yellow>A game is already in progress!");
            return;
        }
        allPlayers.add(player);
        broadcast("<yellow>" + player.getName() + " joined! ("
                + allPlayers.size() + " players)");

        if (allPlayers.size() >= 2 && state == GameState.WAITING) {
            startCountdown();
        }
    }

    public void removePlayer(Player player) {
        allPlayers.remove(player);
        alivePlayers.remove(player);
        if (state == GameState.IN_GAME) {
            checkWinCondition();
        }
    }

    private void startCountdown() {
        setState(GameState.COUNTDOWN);
        countdown = 10;
        countdownTask = Bukkit.getScheduler()
                .runTaskTimer(plugin, () -> {
                    if (countdown <= 0) {
                        countdownTask.cancel();
                        startGame();
                        return;
                    }
                    broadcast("<gold>Game starts in "
                            + countdown + "...");
                    countdown--;
                }, 0L, 20L); // 20tps ideally so 1 second = 20 ticks
    }

    private void startGame() {
        setState(GameState.IN_GAME);
        alivePlayers = new ArrayList<>(allPlayers);
        broadcast("<gold>Game started! Fight!");
        List<Location> spawns = getSpawns();
        for (int i = 0; i < alivePlayers.size(); i++) {
            alivePlayers.get(i).teleport(spawns.get(i));
            alivePlayers.get(i).setGameMode(GameMode.SURVIVAL);
        }
    }

    private void checkWinCondition() {
        if (alivePlayers.size() == 1) {
            Player winner = alivePlayers.getFirst();
            broadcast("<gold>" + winner.getName() + " wins!");
            setState(GameState.ENDED);
            countdown = 10;
            countdownTask = Bukkit.getScheduler()
                    .runTaskTimer(plugin, () -> {
                        if (countdown <= 0) {
                            countdownTask.cancel();
                            Location lobby = getLobbyLocation();
                            for (Player p : allPlayers) {
                                p.teleport(lobby);
                                p.setGameMode(GameMode.ADVENTURE);
                            }
                            setState(GameState.WAITING);
                            return;
                        }
                        broadcast("<gold>Game ending in "
                                + countdown + "...");
                        countdown--;
                    }, 0L, 20L); // 20tps ideally so 1 second = 20 ticks
        }
    }

    private void broadcast(String message) {
        Bukkit.getServer().sendMessage(mm.deserialize(message));
    }

    public GameState getState() {
        return state;
    }

    public List<Player> getAllPlayers() {
        return allPlayers;
    }

    private void setState(GameState newState) {
        this.state = newState;
        World world = Bukkit.getWorld("world");
        if (world != null) {
            world.setGameRule(GameRules.PVP, newState == GameState.IN_GAME);
        }
    }

    private List<Location> getSpawns() {
        World world = Bukkit.getWorld("world");
        List<Location> spawns = new ArrayList<>();
        for (var section : plugin.getConfig().getMapList("spawns")) {
            double x = ((Number) section.get("x")).doubleValue();
            double y = ((Number) section.get("y")).doubleValue();
            double z = ((Number) section.get("z")).doubleValue();
            spawns.add(new Location(world, x, y, z));
        }
        return spawns;
    }

    public Location getLobbyLocation() {
        var config = plugin.getConfig();
        World world = Bukkit.getWorld("world");
        return new Location(world,
                config.getDouble("lobby.x"),
                config.getDouble("lobby.y"),
                config.getDouble("lobby.z")
        );
    }
}