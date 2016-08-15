package fr.onecraft.clientstats.bungee;

import fr.onecraft.clientstats.ClientStatsAPI;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EventListener implements Listener {

    private final Map<UUID, Long> playtimes = new HashMap<>();
    private final BungeeClientStats plugin;

    public EventListener(BungeeClientStats plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PostLoginEvent e) {
        ProxiedPlayer p = e.getPlayer();
        if (!p.hasPermission(ClientStatsAPI.EXEMPT_PERMISSION)) {
            plugin.registerJoin(p);
            playtimes.put(p.getUniqueId(), System.currentTimeMillis());
        }
        plugin.updatePlayerCount();
    }

    @EventHandler
    public void onPlayerQuit(PlayerDisconnectEvent e) {
        Long playtime = playtimes.remove(e.getPlayer().getUniqueId());
        if (playtime != null) {
            plugin.registerPlaytime(System.currentTimeMillis() - playtime);
        }
    }

}