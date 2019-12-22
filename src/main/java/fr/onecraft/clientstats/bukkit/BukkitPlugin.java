package fr.onecraft.clientstats.bukkit;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import fr.onecraft.clientstats.ClientStats;
import fr.onecraft.clientstats.bukkit.dispatcher.CommandDispatcher;
import fr.onecraft.clientstats.bukkit.dispatcher.EventDispatcher;
import fr.onecraft.clientstats.bukkit.hook.detector.ProtocolLibDetector;
import fr.onecraft.clientstats.bukkit.hook.detector.ProtocolSupportDetector;
import fr.onecraft.clientstats.bukkit.hook.detector.ServerDetector;
import fr.onecraft.clientstats.bukkit.hook.detector.TinyProtocolDetector;
import fr.onecraft.clientstats.bukkit.hook.detector.ViaProtocolDetector;
import fr.onecraft.clientstats.bukkit.hook.detector.ViaVersionDetector;
import fr.onecraft.clientstats.bukkit.hook.detector.ViaVersionLegacyDetector;
import fr.onecraft.clientstats.bukkit.user.BukkitUserProvider;
import fr.onecraft.clientstats.common.base.Configurable;
import fr.onecraft.clientstats.common.base.VersionProvider;
import fr.onecraft.clientstats.common.core.AbstractAPI;
import fr.onecraft.clientstats.common.core.VersionNameProvider;
import fr.onecraft.clientstats.common.user.MixedUser;
import fr.onecraft.core.plugin.Core;

public class BukkitPlugin extends Core implements Configurable {

    @SuppressWarnings("deprecation")
    @Override
    public void enable() {

	// User provider
	MixedUser.setProvider(new BukkitUserProvider(this.getServer()));

	// Version detection
	VersionProvider provider = null;
	if (ViaProtocolDetector.isUsable())
	    provider = ViaProtocolDetector.getProvider();
	else if (ViaVersionDetector.isUsable())
	    provider = ViaVersionDetector.getProvider();
	else if (ViaVersionLegacyDetector.isUsable())
	    provider = ViaVersionLegacyDetector.getProvider();
	else if (ProtocolSupportDetector.isUsable())
	    provider = ProtocolSupportDetector.getProvider();
	else if (ServerDetector.isUsable())
	    provider = ServerDetector.getProvider();
	else if (this.getConfig().getBoolean("settings.use-packets", false)) {

	    if (ProtocolLibDetector.isUsable())
		provider = ProtocolLibDetector.getProvider();
	    else if (TinyProtocolDetector.isUsable())
		provider = TinyProtocolDetector.getProvider();
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

	// Version Provider
	try {
	    VersionNameProvider.reload(false, this.getLogger());
	} catch (IOException e) {
	    this.getLogger().log(Level.SEVERE, "Unable to load version list", e);
	}
	Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, () -> {
	    try {
		VersionNameProvider.reload(true, this.getLogger());
	    } catch (IOException e) {
		this.getLogger().log(Level.SEVERE, "Unable to reload version list", e);
	    }
	}, 86400, 86400);

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
    public void start() {
    }

    @Override
    public void disable() {
	// Remove api
	ClientStats.setApi(null);
    }

    @Override
    public void migrate() {

	// v2.7.3 -> v2.7.4
	Object help = this.getConfig().get("messages.commands.help");
	if (help instanceof List) {
	    this.getConfig().set("messages.commands.help", null);
	    ConfigurationSection cs = this.getConfig().getConfigurationSection("messages.commands.joined");
	    if (cs != null) {
		this.getConfig().set("messages.commands.joined", null);
	    }
	    this.saveConfig();
	}

	// v2.7.6 -> v2.7.7
	String helpKey = "messages.commands.help.stats";
	String helpMsg = this.getConfig().getString(helpKey);
	if (helpMsg.equals("&b/{1} stats &f- Statistics since server startup")) {
	    this.getConfig().set(helpKey, "&b/{1} stats &f- Global statistics");
	} else if (helpMsg.contains("Statistics since server startup")) {
	    this.getConfig().set(helpKey, helpMsg.replace("Statistics since server startup", "Global statistics"));
	}

	// v2.7.6 -> v2.7.7
	String statsKey = "messages.commands.stats.title";
	String statsMsg = this.getConfig().getString(statsKey);
	if (statsMsg.equals("Statistics since server startup:")) {
	    this.getConfig().set(statsKey, "Statistics since {1}:");
	} else if (statsMsg.contains("server startup")) {
	    this.getConfig().set(statsKey, statsMsg.replace("server startup", "{1}"));
	}

    }

    @Override
    public void options() {

	// Copy headers and new values
	this.getConfig().options().copyDefaults(true).copyHeader(true);

    }

    @Override
    public String getConfigString(String path) {
	return this.getConfig().getString(path);
    }

    @Override
    public String getConfigString(String path, String def) {
	return this.getConfig().getString(path, def);
    }

    @Override
    public void setConfigValue(String path, Object value) {
	this.getConfig().set(path, value);
    }

}
