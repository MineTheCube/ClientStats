package fr.onecraft.clientstats.bungee;

import fr.onecraft.clientstats.ClientStats;
import fr.onecraft.clientstats.bungee.dispatcher.CommandDispatcher;
import fr.onecraft.clientstats.bungee.dispatcher.EventDispatcher;
import fr.onecraft.clientstats.bungee.user.BungeeUserProvider;
import fr.onecraft.clientstats.common.base.Configurable;
import fr.onecraft.clientstats.common.core.AbstractAPI;
import fr.onecraft.clientstats.common.user.MixedUser;
import fr.onecraft.config.plugin.PluginConfigurable;

public class BungeePlugin extends PluginConfigurable implements Configurable {

    // Plugin state
    private boolean enabled = false;

    @Override
    public void onEnable() {

        // User provider
        MixedUser.setProvider(new BungeeUserProvider(getProxy()));

        // Bungeecord API
        AbstractAPI api = new BungeeAPI(this);

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
    public void migrate() {}

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

}
