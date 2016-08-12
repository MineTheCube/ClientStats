package fr.onecraft.clientstats.bukkit;

import fr.onecraft.clientstats.ClientStats;
import fr.onecraft.clientstats.ClientStatsAPI;
import fr.onecraft.clientstats.bukkit.hooks.*;
import fr.onecraft.core.collection.PlayerMap;
import fr.onecraft.core.command.CommandUser;
import fr.onecraft.core.helper.Players;
import fr.onecraft.core.plugin.Core;
import fr.onecraft.core.tuple.Pair;
import fr.onecraft.core.utils.ChatUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BukkitClientStats extends Core implements ClientStatsAPI {

    // Prefix
    protected String PREFIX = "§9[ClientStats] §f";
    protected String SUBLINE = "§f";

    // Map of UUID -> Protocol version
    private final PlayerMap<Integer> joined = new PlayerMap<>();

    // Statistics
    private int totalJoined = 0;
    private int totalNewPlayers = 0;
    private int maxOnlinePlayers = 0;
    private long maxOnlineDate = 0;

    // Playtime
    private double averagePlaytime = 0;
    private int playtimeRatio = 0;

    // Version detection
    private Provider provider;

    @Override
    public void enable() {

        // Reload config
        reload();

        // Version detection
        if (ViaVersionDetector.isUsable()) provider = ViaVersionDetector.getProvider();
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

        // Register Event
        new EventListener().register();

        // Handle command
        new CommandHandler().register("clientstats");

        // Initial value
        maxOnlinePlayers = Players.online().size();
        maxOnlineDate = System.currentTimeMillis();

        // Api is ready
        ClientStats.setApi(this);

    }

    @Override
    public boolean isVersionDetectionEnabled() {
        return provider != null && isEnabled();
    }

    @Override
    public void reload() {

        // Save config if it doesn't exist
        saveDefaultConfig();

        // v2.7.3 -> v2.7.4
        Object help = getConfig().get("messages.commands.help");
        if (help instanceof List) {
            getConfig().set("messages.commands.help", null);
            ConfigurationSection cs = getConfig().getConfigurationSection("messages.commands.joined");
            if (cs != null) {
                getConfig().set("messages.commands.USELESS_YOU_CAN_DELETE_THAT_PART", cs);
                getConfig().set("messages.commands.joined", null);
            }
            saveConfig();
        }

        // Reload to get latest values
        reloadConfig();

        // Copy headers and new values
        getConfig().options().copyDefaults(true).copyHeader(true);

        // And save it
        saveConfig();

        // Get prefixes
        PREFIX = ChatUtils.colorize(getConfig().getString("messages.prefix"));
        SUBLINE = ChatUtils.colorize(getConfig().getString("messages.subline"));

    }

    @Override
    public void start() {}

    @Override
    public void disable() {
        // Remove api
        ClientStats.setApi(null);
    }

    @Override
    public void resetStats() {
        joined.clear();
        totalJoined = 0;
        totalNewPlayers = 0;
        maxOnlinePlayers = Players.online().size();
        maxOnlineDate = System.currentTimeMillis();
        averagePlaytime = 0;
        playtimeRatio = 0;
    }

    @Override
    public int getTotalJoined() {
        return totalJoined;
    }

    @Override
    public int getTotalNewPlayers() {
        return totalNewPlayers;
    }

    @Override
    public int getUniqueJoined() {
        return joined.size();
    }

    @Override
    public int getMaxOnlinePlayers() {
        return maxOnlinePlayers;
    }

    @Override
    public long getMaxOnlineDate() {
        return maxOnlineDate;
    }

    @Override
    public double getAveragePlaytime() {
        return averagePlaytime;
    }

    @Override
    public Map<UUID, Integer> getProtocolJoined() {
        return Collections.unmodifiableMap(joined);
    }

    @Override
    public String getVersionName(int version) {
        String versionName = config().getString("versions." + version);

        if (versionName == null) {
            getLogger().severe("Missing version: versions." + version);
            versionName = config().getString("versions.0");

            if (versionName == null) {
                getLogger().severe("Missing message: versions.0");
                return "Unknown";
            }
        }

        return versionName;
    }

    @Override
    public int getProtocol(UUID player) {
        Player p = Players.get(player);
        return p != null && isVersionDetectionEnabled() ? provider.getProtocol(p) : 0;
    }

    @Override
    public Pair<Integer, String> getVersion(UUID player) {
        int version = getProtocol(player);
        if (version == 0) return null;
        String versionName = getVersionName(version);
        return Pair.of(version, versionName);
    }

    public void updatePlayerCount() {
        int online = Players.online().size();
        if (online > maxOnlinePlayers) {
            maxOnlinePlayers = online;
            maxOnlineDate = System.currentTimeMillis();
        }
    }

    public void registerJoin(Player p, boolean isNew) {
        if (isVersionDetectionEnabled()) joined.put(p, getProtocol(p.getUniqueId()));
        totalJoined++;
        if (isNew) totalNewPlayers++;
    }

    public void registerPlaytime(long playtimeMillis) {
        long playtimeSeconds = playtimeMillis / 1000;
        playtimeRatio++;
        averagePlaytime += (playtimeSeconds - averagePlaytime) / playtimeRatio;
    }

    @Override
    public void sendMessage(Object receiver, String messageCode, Object... args) {
        processMessage(receiver, messageCode, PREFIX, args);
    }

    @Override
    public void subMessage(Object receiver, String messageCode, Object... args) {
        processMessage(receiver, messageCode, SUBLINE, args);
    }

    private void processMessage(Object receiver, String messageCode, String prefix, Object... args) {

        CommandSender sender;

        if (receiver instanceof CommandUser) sender = ((CommandUser) receiver).getCommandSender();
        else if (receiver instanceof CommandSender) sender = (CommandSender) receiver;
        else return;

        String message = getConfig().getString("messages." + messageCode);

        if (message == null) {
            warning("Missing message: " + messageCode);
            message = config().getString("messages.error.general");

            if (message == null) {
                message = "&cAn internal error occurred..";
            }
        }

        if (message.isEmpty()) return;

        for (int i = 0; i < args.length; i++) {
            message = message.replace("{" + (i + 1) + "}", args[i].toString());
        }

        sender.sendMessage(prefix + ChatUtils.colorize(message));
    }

}
