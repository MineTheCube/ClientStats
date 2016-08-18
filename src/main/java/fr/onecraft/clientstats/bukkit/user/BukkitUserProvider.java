package fr.onecraft.clientstats.bukkit.user;

import fr.onecraft.clientstats.common.user.MixedUser;
import fr.onecraft.clientstats.common.user.UserProvider;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class BukkitUserProvider implements UserProvider {

    private final Server server;

    public BukkitUserProvider(Server server) {
        this.server = server;
    }

    @Override
    public Collection<UUID> getOnlineUsers() {
        List<UUID> users = new ArrayList<>(getOnlineCount());
        for (Player player : server.getOnlinePlayers()) {
            users.add(player.getUniqueId());
        }
        return users;
    }

    @Override
    public int getOnlineCount() {
        return server.getOnlinePlayers().size();
    }

    @Override
    public MixedUser getUser(String name) {
        return BukkitUser.of(server.getPlayer(name));
    }

}
