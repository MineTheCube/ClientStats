package fr.onecraft.clientstats.bungee;

import fr.onecraft.clientstats.ClientStats;
import fr.onecraft.clientstats.bungee.dispatcher.CommandDispatcher;
import fr.onecraft.clientstats.bungee.dispatcher.EventDispatcher;
import fr.onecraft.clientstats.bungee.hooks.ViaVersionDetector;
import fr.onecraft.clientstats.bungee.user.BungeeUserProvider;
import fr.onecraft.clientstats.common.base.Configurable;
import fr.onecraft.clientstats.common.base.VersionProvider;
import fr.onecraft.clientstats.common.core.AbstractAPI;
import fr.onecraft.clientstats.common.user.MixedUser;
import fr.onecraft.config.plugin.PluginConfigurable;
import net.md_5.bungee.api.ProxyServer;

import java.util.UUID;

public class BungeePlugin extends PluginConfigurable implements Configurable {

    // Plugin state
    private boolean enabled = false;

    @Override
    public void onEnable() {

        // User provider
        MixedUser.setProvider(new BungeeUserProvider(getProxy()));

        // Version provider
        VersionProvider provider;
        if (ViaVersionDetector.isUsable()) {
            provider = ViaVersionDetector.getProvider();
        } else {
            provider = new VersionProvider() {
                @Override
                public String getProviderName() {
                    return "Bungeecord";
                }

                @Override
                public int getProtocol(UUID player) {
                    return ProxyServer.getInstance().getPlayer(player).getPendingConnection().getVersion();
                }
            };
        }
        getLogger().info("Hooked into " + provider.getProviderName() + " !");

        // Bungeecord API
        AbstractAPI api = new BungeeAPI(this, provider);

        // Reload config
        api.reload();

        // Register Event
        getProxy().getPluginManager().registerListener(this, new EventDispatcher(api));

        // Handle command
        getProxy().getPluginManager().registerCommand(this, new CommandDispatcher(api));

        // Api is ready
        enabled = true;
        ClientStats.setApi(api);

    }

    @Override
    public void onDisable() {
        // Remove api
        ClientStats.setApi(null);
        enabled = false;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void migrate() {

        // v2.7.6 -> v2.7.7
        String helpKey = "messages.commands.help.stats";
        String helpMsg = getConfig().getString(helpKey);
        if (helpMsg.equals("&b/{1} stats &f- Statistics since server startup")) {
            getConfig().set(helpKey, "&b/{1} stats &f- Global statistics");
        } else if (helpMsg.contains("Statistics since server startup")) {
            getConfig().set(helpKey, helpMsg.replace("Statistics since server startup", "Global statistics"));
        }

        // v2.7.6 -> v2.7.7
        String statsKey = "messages.commands.stats.title";
        String statsMsg = getConfig().getString(statsKey);
        if (statsMsg.equals("Statistics since server startup:")) {
            getConfig().set(statsKey, "Statistics since {1}:");
        } else if (statsMsg.contains("server startup")) {
            getConfig().set(statsKey, statsMsg.replace("server startup", "{1}"));
        }

    }

    @Override
    public void options() {

        // Copy headers and new values
        getConfig().options().copyDefaults(true).copyHeader(true);

    }

    @Override
    public String getConfigString(String path) {
        return getConfig().getString(path);
    }

    @Override
    public String getConfigString(String path, String def) {
        return getConfig().getString(path, def);
    }

    @Override
    public void setConfigValue(String path, Object value) {
        getConfig().set(path, value);
    }

}
