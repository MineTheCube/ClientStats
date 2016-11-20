package fr.onecraft.clientstats.bukkit;

import fr.onecraft.clientstats.ClientStats;
import fr.onecraft.clientstats.bukkit.dispatcher.CommandDispatcher;
import fr.onecraft.clientstats.bukkit.dispatcher.EventDispatcher;
import fr.onecraft.clientstats.bukkit.hook.detector.*;
import fr.onecraft.clientstats.bukkit.user.BukkitUserProvider;
import fr.onecraft.clientstats.common.base.Configurable;
import fr.onecraft.clientstats.common.base.VersionProvider;
import fr.onecraft.clientstats.common.core.AbstractAPI;
import fr.onecraft.clientstats.common.user.MixedUser;
import fr.onecraft.core.plugin.Core;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class BukkitPlugin extends Core implements Configurable {

    @Override
    public void enable() {

        // User provider
        MixedUser.setProvider(new BukkitUserProvider(getServer()));

        // Version detection
        VersionProvider provider = null;
        if (ViaProtocolDetector.isUsable()) provider = ViaProtocolDetector.getProvider();
        else if (ViaVersionDetector.isUsable()) provider = ViaVersionDetector.getProvider();
        else if (ViaVersionLegacyDetector.isUsable()) provider = ViaVersionLegacyDetector.getProvider();
        else if (ProtocolSupportDetector.isUsable()) provider = ProtocolSupportDetector.getProvider();
        else if (ServerDetector.isUsable()) provider = ServerDetector.getProvider();
        else if (getConfig().getBoolean("settings.use-packets", false)) {

            if (ProtocolLibDetector.isUsable()) provider = ProtocolLibDetector.getProvider();
            else if (TinyProtocolDetector.isUsable()) provider = TinyProtocolDetector.getProvider();
            else {
                severe("---------------------");
                severe("\"use-packets\" is enabled, but we can't find a way to use them.");
                severe("Please install ProtocolLib to enable version detection.");
                severe("---------------------");
            }

        } else {
            warning("---------------------");
            warning("Your server doesn't seem to support multiple Minecraft versions.");
            warning("If it does, please enable \"use-packets\" in the config and restart the server.");
            warning("---------------------");
        }

        // Bukkit API
        AbstractAPI api = new BukkitAPI(provider, this);

        // Reload config
        api.reload();

        // Register Event
        new EventDispatcher(api).register();

        // Handle command
        new CommandDispatcher(api).register("clientstats");

        // Api is ready
        ClientStats.setApi(api);

    }

    @Override
    public void start() {}

    @Override
    public void disable() {
        // Remove api
        ClientStats.setApi(null);
    }

    @Override
    public void migrate() {

        // v2.7.3 -> v2.7.4
        Object help = getConfig().get("messages.commands.help");
        if (help instanceof List) {
            getConfig().set("messages.commands.help", null);
            ConfigurationSection cs = getConfig().getConfigurationSection("messages.commands.joined");
            if (cs != null) {
                getConfig().set("messages.commands.joined", null);
            }
            saveConfig();
        }

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
