package fr.onecraft.clientstats.bungee.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import fr.onecraft.clientstats.common.user.MixedUser;
import fr.onecraft.clientstats.common.user.UserProvider;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeUserProvider implements UserProvider {

    private final ProxyServer server;

    public BungeeUserProvider(ProxyServer server) {
	this.server = server;
    }

    @Override
    public Collection<UUID> getOnlineIds() {
	List<UUID> users = new ArrayList<>(this.getOnlineCount());
	for (ProxiedPlayer player : this.server.getPlayers())
	    users.add(player.getUniqueId());
	return users;
    }

    @Override
    public Collection<String> getOnlineNames() {
	List<String> users = new ArrayList<>(this.getOnlineCount());
	for (ProxiedPlayer player : this.server.getPlayers())
	    users.add(player.getName());
	return users;
    }

    @Override
    public int getOnlineCount() {
	return this.server.getOnlineCount();
    }

    @Override
    public MixedUser getUser(String name) {
	return BungeeUser.of(this.server.getPlayer(name));
    }

}
