package fr.onecraft.clientstats.bukkit;

import java.util.logging.Logger;

import javax.annotation.Nonnull;

import org.bukkit.ChatColor;

import fr.onecraft.clientstats.common.base.ServerType;
import fr.onecraft.clientstats.common.base.VersionProvider;
import fr.onecraft.clientstats.common.core.AbstractAPI;

public class BukkitAPI extends AbstractAPI {

    private final BukkitPlugin plugin;

    public BukkitAPI(VersionProvider provider, BukkitPlugin plugin) {
	super(provider, plugin);
	this.plugin = plugin;
    }

    @Nonnull
    @Override
    public ServerType getServerType() {
	return ServerType.BUKKIT;
    }

    @Override
    public boolean isEnabled() {
	return this.plugin.isEnabled();
    }

    @Override
    public Logger getLogger() {
	return this.plugin.getLogger();
    }

    @Override
    public String colorize(String string) {
	return ChatColor.translateAlternateColorCodes('&', string);
    }

}
