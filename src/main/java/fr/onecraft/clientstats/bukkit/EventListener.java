package fr.onecraft.clientstats.bukkit;

import fr.onecraft.clientstats.ClientStats;
import fr.onecraft.core.collection.PlayerMap;
import fr.onecraft.core.event.EventRegister;
import fr.onecraft.core.plugin.Core;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener extends EventRegister {

    private final BukkitClientStats plugin = Core.instance();

    private final PlayerMap<Long> playtimes = new PlayerMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (!p.hasPermission(ClientStats.EXEMPT_PERMISSION)) {
            plugin.registerJoin(p, !p.hasPlayedBefore());
            playtimes.put(p, System.currentTimeMillis());
        }
        plugin.updatePlayerCount();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Long playtime = playtimes.remove(e.getPlayer());
        if (playtime != null) {
            plugin.registerPlaytime(System.currentTimeMillis() - playtime);
        }
    }

}