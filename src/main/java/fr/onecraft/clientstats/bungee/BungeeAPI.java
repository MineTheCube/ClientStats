package fr.onecraft.clientstats.bungee;

import fr.onecraft.clientstats.common.base.ServerType;
import fr.onecraft.clientstats.common.base.VersionProvider;
import fr.onecraft.clientstats.common.core.AbstractAPI;
import net.md_5.bungee.api.ChatColor;

import javax.annotation.Nonnull;
import java.util.logging.Logger;

public class BungeeAPI extends AbstractAPI {

    private final BungeePlugin plugin;

    public BungeeAPI(BungeePlugin plugin, VersionProvider provider) {
        super(provider, plugin);
        this.plugin = plugin;
    }

    @Nonnull
    @Override
    public ServerType getServerType() {
        return ServerType.BUNGEE;
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
