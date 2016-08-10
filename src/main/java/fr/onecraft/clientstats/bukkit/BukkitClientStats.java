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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BukkitClientStats extends Core implements ClientStatsAPI {

    protected String PREFIX = "§9[ClientStats] §f";
    protected String SUBLINE = "§f";

    private final PlayerMap<Integer> joined = new PlayerMap<>();
    private int totalJoined = 0;
    private int totalNewPlayers = 0;
    private double averagePlaytime = 0;
    private int playtimeRatio = 0;

    private Provider provider;

    @Override
    public void enable() {

        // Version detection
        if (ViaVersionDetector.isUsable()) provider = ViaVersionDetector.getProvider();
        else if (ProtocolSupportDetector.isUsable()) provider = ProtocolSupportDetector.getProvider();
        else if (ServerDetector.isUsable()) provider = ServerDetector.getProvider();
        else if (ProtocolLibDetector.isUsable()) provider = ProtocolLibDetector.getProvider();
        else if (TinyProtocolDetector.isUsable()) provider = TinyProtocolDetector.getProvider();
        else {
            severe("---------------------");
            severe("Your server doesn't seem to support multiple Minecraft versions.");
            severe("---------------------");
            setEnabled(false);
            return;
        }

        // Reload config
        reload();

        // Register Event
        new EventListener().register();

        // Handle command
        new CommandHandler().register("clientstats");

        // Api is ready
        ClientStats.setApi(this);

    }

    @Override
    public void reload() {

        // Save config if it doesn't exist
        saveDefaultConfig();

        // v2.7.4
        Object help = getConfig().get("messages.commands.help");
        if (help instanceof List) {
            getConfig().set("messages.commands.help", null);
            ConfigurationSection cs = getConfig().getConfigurationSection("messages.commands.joined");
            if (cs != null) {
                getConfig().set("messages.commands.stats", cs);
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
        PREFIX = ChatUtils.colorize(config().getString("messages.prefix"));
        SUBLINE = ChatUtils.colorize(config().getString("messages.subline"));

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
        return p != null ? provider.getProtocol(p) : 0;
    }

    @Override
    public Pair<Integer, String> getVersion(UUID player) {
        int version = getProtocol(player);
        if (version == 0) return null;
        String versionName = getVersionName(version);
        return Pair.of(version, versionName);
    }

    public void registerJoin(Player p, boolean isNew) {
        joined.put(p, getProtocol(p.getUniqueId()));
        totalJoined++;
        if (isNew) totalNewPlayers++;
    }

    public void registerPlaytime(long playtimeMillis) {
        long playtimeSeconds = playtimeMillis / 1000;
        playtimeRatio++;
        averagePlaytime += (playtimeSeconds - averagePlaytime) / playtimeRatio;
    }

    public void sendMessage(CommandUser sender, String messageCode, Object... args) {
        processMessage(sender, messageCode, PREFIX, args);
    }

    public void subMessage(CommandUser sender, String messageCode, Object... args) {
        processMessage(sender, messageCode, SUBLINE, args);
    }

    private void processMessage(CommandUser sender, String messageCode, String prefix, Object... args) {

        String message = config().getString("messages." + messageCode);

        if (message == null) {
            warning("Missing message: " + messageCode);
            message = config().getString("messages.error.general");

            if (message == null) {
                message = "&cAn internal error occurred..";
            }
        }

        for (int i = 0; i < args.length; i++) {
            message = message.replace("{" + (i + 1) + "}", args[i].toString());
        }

        sender.sendMessage(prefix + ChatUtils.colorize(message));
    }

}
