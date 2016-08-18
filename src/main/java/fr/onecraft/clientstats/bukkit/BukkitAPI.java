package fr.onecraft.clientstats.bukkit;

import fr.onecraft.clientstats.common.base.ServerType;
import fr.onecraft.clientstats.common.base.VersionProvider;
import fr.onecraft.clientstats.common.core.AbstractAPI;
import org.bukkit.ChatColor;

import java.util.logging.Logger;

public class BukkitAPI extends AbstractAPI {

    private final BukkitPlugin plugin;

    public BukkitAPI(VersionProvider provider, BukkitPlugin plugin) {
        super(provider, plugin);
        this.plugin = plugin;
    }

    @Override
    public ServerType getServerType() {
        return ServerType.BUKKIT;
    }

    @Override
    public boolean isEnabled() {
        return plugin.isEnabled();
    }

    @Override
    public Logger getLogger() {
        return plugin.getLogger();
    }

    @Override
    public String colorize(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

}
