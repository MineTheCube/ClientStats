package fr.onecraft.clientstats.bungee.user;

import fr.onecraft.clientstats.common.user.MixedUser;
import fr.onecraft.clientstats.common.user.UserProvider;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class BungeeUserProvider implements UserProvider {

    private final ProxyServer server;

    public BungeeUserProvider(ProxyServer server) {
        this.server = server;
    }

    @Override
    public Collection<UUID> getOnlineUsers() {
        List<UUID> users = new ArrayList<>(getOnlineCount());
        for (ProxiedPlayer player : server.getPlayers()) {
            users.add(player.getUniqueId());
        }
        return users;
    }

    @Override
    public int getOnlineCount() {
        return server.getOnlineCount();
    }

    @Override
    public MixedUser getUser(String name) {
        return BungeeUser.of(server.getPlayer(name));
    }

}
