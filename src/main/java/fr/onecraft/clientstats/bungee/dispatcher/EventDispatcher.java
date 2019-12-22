package fr.onecraft.clientstats.bungee.dispatcher;

import fr.onecraft.clientstats.bungee.user.BungeeUser;
import fr.onecraft.clientstats.common.core.AbstractAPI;
import fr.onecraft.clientstats.common.core.EventListener;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class EventDispatcher implements Listener {

    private final EventListener listener;

    public EventDispatcher(AbstractAPI api) {
	this.listener = new EventListener(api);
    }

    @EventHandler
    public void on(PostLoginEvent e) {
	this.listener.onPlayerJoin(BungeeUser.of(e.getPlayer()), false);
    }

    @EventHandler
    public void on(PlayerDisconnectEvent e) {
	this.listener.onPlayerQuit(BungeeUser.of(e.getPlayer()));
    }

}