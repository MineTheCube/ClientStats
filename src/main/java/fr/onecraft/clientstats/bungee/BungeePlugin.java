package fr.onecraft.clientstats.bungee;

import fr.onecraft.clientstats.ClientStats;
import fr.onecraft.clientstats.bungee.config.PluginConfigurable;
import fr.onecraft.clientstats.bungee.dispatcher.CommandDispatcher;
import fr.onecraft.clientstats.bungee.dispatcher.EventDispatcher;
import fr.onecraft.clientstats.bungee.user.BungeeUserProvider;
import fr.onecraft.clientstats.common.core.AbstractAPI;
import fr.onecraft.clientstats.common.user.MixedUser;

public class BungeePlugin extends PluginConfigurable {

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

}
