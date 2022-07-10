package org.gunivers.shutdownplugin;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class Main extends JavaPlugin {

    private ShutdownTimer timer;
    private Pterodactyl pterodactyl;

    @Override
    public void onEnable() {

        saveDefaultConfig();
        FileConfiguration config = getConfig();

        try {
            pterodactyl = new Pterodactyl(config);
        } catch (IOException e) {
            throw new RuntimeException(
                    "[ShutdownPlugin] Couldn't reach Pterodactyl API, please check your configuration", e);
        }

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerJoinListener(), this);
        pm.registerEvents(new PlayerQuitListener(), this);

        long delay = config.getLong("shutdownDelay");
        timer = new ShutdownTimer(this::shutdownServer, delay);
        timer.restart();
    }

    private void shutdownServer() {
        Bukkit.broadcastMessage("[ShutdownPlugin] One hour without activity, shutting down server...");
        pterodactyl.shutdownServer();
    }

    private class PlayerJoinListener implements Listener {
        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent evt) {
            timer.stop();
        }
    }

    private class PlayerQuitListener implements Listener {
        @EventHandler
        public void onPlayerQuit(PlayerQuitEvent evt) {
            timer.restart();
        }
    }
}
