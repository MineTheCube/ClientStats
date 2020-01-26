package fr.onecraft.clientstats.bukkit.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import fr.onecraft.clientstats.common.user.MixedUser;
import fr.onecraft.clientstats.common.user.UserProvider;

public class BukkitUserProvider implements UserProvider {

    private final Server server;

    public BukkitUserProvider(Server server) {
	this.server = server;
    }

    @Override
    public Collection<UUID> getOnlineIds() {
	List<UUID> users = new ArrayList<>(this.getOnlineCount());
	for (Player player : this.server.getOnlinePlayers())
	    users.add(player.getUniqueId());
	return users;
    }

    @Override
    public Collection<String> getOnlineNames() {
	List<String> users = new ArrayList<>(this.getOnlineCount());
	for (Player player : this.server.getOnlinePlayers())
	    users.add(player.getName());
	return users;
    }

    @Override
    public int getOnlineCount() {
	return this.server.getOnlinePlayers().size();
    }

    @Override
    public MixedUser getUser(String name) {
	return BukkitUser.of(this.server.getPlayer(name));
    }

}
