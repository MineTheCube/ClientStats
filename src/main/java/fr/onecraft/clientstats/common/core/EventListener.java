package fr.onecraft.clientstats.common.core;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import fr.onecraft.clientstats.ClientStatsAPI;
import fr.onecraft.clientstats.common.user.MixedUser;

public class EventListener {

    private final Map<UUID, Long> playtimes = new HashMap<>();
    private final AbstractAPI plugin;

    public EventListener(AbstractAPI plugin) {
	this.plugin = plugin;
    }

    public void onPlayerJoin(MixedUser player, boolean isNew) {
	if (!player.hasPermission(ClientStatsAPI.EXEMPT_PERMISSION)) {
	    this.plugin.registerJoin(player, isNew);
	    this.playtimes.put(player.getUniqueId(), System.currentTimeMillis());
	}
	this.plugin.updatePlayerCount();
    }

    public void onPlayerQuit(MixedUser player) {
	Long playtime = this.playtimes.remove(player.getUniqueId());
	if (playtime != null) {
	    this.plugin.registerPlaytime(System.currentTimeMillis() - playtime);
	}
    }

}