package com.example.skywars;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.Location;
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
        broadcast("<gold>" + player.getName() + " was eliminated!");
        player.setGameMode(GameMode.SPECTATOR);
        alivePlayers.remove(player);
        checkWinCondition();
    }

    public void addPlayer(Player player) {
        if (state != GameState.WAITING && state != GameState.COUNTDOWN) {
            broadcast("<gold>A game is already in progress!");
            return;
        }
        allPlayers.add(player);
        broadcast("<gold" + player.getName() + " joined! ("
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
        state = GameState.COUNTDOWN;
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
        state = GameState.IN_GAME;
        alivePlayers = new ArrayList<>(allPlayers);
        broadcast("<gold>Game started! Fight!");
        World world = Bukkit.getWorld("world");
        List<Location> spawns = List.of(
                new Location(world, -23, 81, 35),
                new Location(world, -30, 81, 22),
                new Location(world, -35, 81, -23),
                new Location(world, -23, 81, -30),
                new Location(world, 23, 81, -35),
                new Location(world, 30, 81, -23),
                new Location(world, 35, 81, 23),
                new Location(world, 23, 81, 30)
        );
        for (int i = 0; i < alivePlayers.size(); i++) {
            alivePlayers.get(i).teleport(spawns.get(i));
            alivePlayers.get(i).setGameMode(GameMode.SURVIVAL);
        }
    }

    private void checkWinCondition() {
        if (alivePlayers.size() == 1) {
            Player winner = alivePlayers.getFirst();
            broadcast("<gold>" + winner.getName() + " wins!");
            state = GameState.ENDED;
            countdown = 10;
            countdownTask = Bukkit.getScheduler()
                    .runTaskTimer(plugin, () -> {
                        if (countdown <= 0) {
                            countdownTask.cancel();
                            startGame();
                            return;
                        }
                        broadcast("<gold>Game ending in "
                                + countdown + "...");
                        countdown--;
                    }, 0L, 20L); // 20tps ideally so 1 second = 20 ticks

            for (Player p : allPlayers) {
                p.teleport(new Location(
                        Bukkit.getWorld("world"), 0, 170, 0));
                p.setGameMode(GameMode.ADVENTURE);
            }

            state = GameState.WAITING;
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
}