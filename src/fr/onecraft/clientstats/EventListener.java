package fr.onecraft.clientstats;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EventListener implements Listener {

    private Main plugin;

	public EventListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        String version = plugin.getVersion(player);

        if (version != null) {
        	plugin.joinedPlayers.put(uuid, version);
        }

        plugin.totalJoined++;

    }

}