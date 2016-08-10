package fr.onecraft.clientstats.bukkit;

import fr.onecraft.core.collection.PlayerMap;
import fr.onecraft.core.event.EventRegister;
import fr.onecraft.core.plugin.Core;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener extends EventRegister {

	private final BukkitClientStats plugin = Core.instance();

    private final PlayerMap<Long> playtimes = new PlayerMap<>();

    @EventHandler
    @SuppressWarnings("ConstantConditions")
    public void onPlayerJoin(PlayerJoinEvent e) {
    	plugin.incrementJoined(e.getPlayer(), !e.getPlayer().hasPlayedBefore());
        playtimes.put(e.getPlayer(), System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Long playtime = playtimes.remove(e.getPlayer());
        if (playtime != null) {
            plugin.addPlaytime(System.currentTimeMillis() -  playtime);
        }
    }

}