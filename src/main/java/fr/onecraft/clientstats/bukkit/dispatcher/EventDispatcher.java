package fr.onecraft.clientstats.bukkit.dispatcher;

import fr.onecraft.clientstats.bukkit.user.BukkitUser;
import fr.onecraft.clientstats.common.core.AbstractAPI;
import fr.onecraft.clientstats.common.core.EventListener;
import fr.onecraft.core.event.EventRegister;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventDispatcher extends EventRegister {

    private final EventListener listener;

    public EventDispatcher(AbstractAPI api) {
        this.listener = new EventListener(api);
    }

    @EventHandler
    public void on(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        listener.onPlayerJoin(BukkitUser.of(p), !p.hasPlayedBefore());
    }

    @EventHandler
    public void on(PlayerQuitEvent e) {
        listener.onPlayerQuit(BukkitUser.of(e.getPlayer()));
    }

}