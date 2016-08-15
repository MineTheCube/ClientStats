package fr.onecraft.clientstats.bungee;

import fr.onecraft.clientstats.ClientStats;
import fr.onecraft.clientstats.ClientStatsAPI;
import fr.onecraft.core.tuple.Pair;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BungeeClientStats extends BungeePlugin implements ClientStatsAPI {

    // Prefix
    protected String PREFIX = "§9[ClientStats] §f";
    protected String SUBLINE = "§f";

    // Map of UUID -> Protocol version
    private final Map<UUID, Integer> joined = new HashMap<>();

    // Statistics
    private int totalJoined = 0;
    private int maxOnlinePlayers = 0;
    private long maxOnlineDate = 0;

    // Playtime
    private double averagePlaytime = 0;
    private int playtimeRatio = 0;

    // Plugin data
    private boolean enabled = false;

    @Override
    public void onEnable() {

        // Reload config
        reload();

        // Register Event
        getProxy().getPluginManager().registerListener(this, new EventListener(this));

        // Handle command
        getProxy().getPluginManager().registerCommand(this, new CommandHandler(this));

        // Initial value
        maxOnlinePlayers = getProxy().getOnlineCount();
        maxOnlineDate = System.currentTimeMillis();

        // Api is ready
        enabled = true;
        ClientStats.setApi(this);

    }

    @Override
    public void onDisable() {
        // Remove api
        ClientStats.setApi(null);
        enabled = false;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isVersionDetectionEnabled() {
        return isEnabled();
    }

    @Override
    public void reload() {

        // Save config if it doesn't exist
        saveDefaultConfig();

        // Reload to get latest values
        reloadConfig();

        // Copy headers and new values
        // Actually there isn't any easy way to achieve this with Bungeecord API...

        // And save it
        saveConfig();

        // Get prefixes
        PREFIX = colorize(getConfig().getString("messages.prefix", PREFIX));
        SUBLINE = colorize(getConfig().getString("messages.subline", SUBLINE));

    }

    @Override
    public void resetStats() {
        joined.clear();
        totalJoined = 0;
        maxOnlinePlayers = getProxy().getOnlineCount();
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
        return 0;
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
        String versionName = getConfig().getString("versions." + version);

        if (versionName == null) {
            getLogger().severe("Missing version: versions." + version);
            versionName = getConfig().getString("versions.0");

            if (versionName == null) {
                getLogger().severe("Missing message: versions.0");
                return "Unknown";
            }
        }

        return versionName;
    }

    @Override
    public int getProtocol(UUID player) {
        if (isVersionDetectionEnabled()) {
            ProxiedPlayer p = getProxy().getPlayer(player);
            if (p != null) {
                return p.getPendingConnection().getVersion();
            }
        }
        return 0;
    }

    @Override
    public Pair<Integer, String> getVersion(UUID player) {
        int version = getProtocol(player);
        if (version == 0) return null;
        String versionName = getVersionName(version);
        return Pair.of(version, versionName);
    }

    public void updatePlayerCount() {
        int online = getProxy().getOnlineCount();
        if (online > maxOnlinePlayers) {
            maxOnlinePlayers = online;
            maxOnlineDate = System.currentTimeMillis();
        }
    }

    public void registerJoin(ProxiedPlayer p) {
        if (isVersionDetectionEnabled()) joined.put(p.getUniqueId(), getProtocol(p.getUniqueId()));
        totalJoined++;
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

        if (receiver instanceof CommandSender) sender = (CommandSender) receiver;
        else return;

        String message = getConfig().getString("messages." + messageCode);

        if (message == null) {
            getLogger().warning("Missing message: " + messageCode);
            message = getConfig().getString("messages.error.general");

            if (message == null) {
                message = "&cAn internal error occurred..";
            }
        }

        if (message.isEmpty()) return;

        for (int i = 0; i < args.length; i++) {
            message = message.replace("{" + (i + 1) + "}", args[i].toString());
        }

        sender.sendMessage(TextComponent.fromLegacyText(prefix + colorize(message)));
    }

    private String colorize(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

}
