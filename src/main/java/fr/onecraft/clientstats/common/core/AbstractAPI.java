package fr.onecraft.clientstats.common.core;

import fr.onecraft.clientstats.ClientStatsAPI;
import fr.onecraft.clientstats.common.base.Configurable;
import fr.onecraft.clientstats.common.base.VersionProvider;
import fr.onecraft.clientstats.common.user.MixedUser;
import fr.onecraft.core.tuple.Pair;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import static fr.onecraft.clientstats.common.user.MixedUser.getOnlineCount;

public abstract class AbstractAPI implements ClientStatsAPI {

    // Prefix
    protected String PREFIX = "§9[ClientStats] §f";
    protected String SUBLINE = "§f";

    // Map of UUID -> Protocol version
    private final Map<UUID, Integer> joined = new HashMap<>();

    // Version provider
    private final VersionProvider provider;
    private final Configurable config;

    // Statistics
    private int totalJoined = 0;
    private int totalNewPlayers;
    private int maxOnlinePlayers = getOnlineCount();
    private long maxOnlineDate = System.currentTimeMillis();

    // Playtime
    private double averagePlaytime = 0;
    private int playtimeRatio = 0;

    public AbstractAPI(VersionProvider provider, Configurable config) {
        this.provider = provider;
        this.config = config;
    }

    public abstract Logger getLogger();

    public abstract String colorize(String msg);

    @Override
    public void reload() {

        // Save config if it doesn't exist
        config.saveDefaultConfig();

        // Reload to get latest values
        config.reloadConfig();

        // Migrate over versions
        config.migrate();

        // Copy headers and new values
        config.options();

        // And save it
        config.saveConfig();

        // Get prefixes
        PREFIX = colorize(config.getConfigString("messages.prefix", PREFIX));
        SUBLINE = colorize(config.getConfigString("messages.subline", SUBLINE));

    }

    @Override
    public void resetStats() {
        joined.clear();
        totalJoined = 0;
        maxOnlinePlayers = MixedUser.getOnlineCount();
        maxOnlineDate = System.currentTimeMillis();
        averagePlaytime = 0;
        playtimeRatio = 0;
    }

    @Override
    public boolean isVersionDetectionEnabled() {
        return isEnabled() && provider != null;
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
        String versionName = config.getConfigString("versions." + version);

        if (versionName == null) {
            getLogger().severe("Missing version: versions." + version);
            versionName = config.getConfigString("versions.0");

            if (versionName == null) {
                getLogger().severe("Missing message: versions.0");
                return "Unknown";
            }
        }

        return versionName;
    }

    @Override
    public int getProtocol(UUID player) {
        return isVersionDetectionEnabled() ? provider.getProtocol(player) : 0;
    }

    @Override
    public Pair<Integer, String> getVersion(UUID player) {
        int version = getProtocol(player);
        if (version == 0) return null;
        String versionName = getVersionName(version);
        return Pair.of(version, versionName);
    }

    public void updatePlayerCount() {
        int online = MixedUser.getOnlineCount();
        if (online > maxOnlinePlayers) {
            maxOnlinePlayers = online;
            maxOnlineDate = System.currentTimeMillis();
        }
    }

    public void registerJoin(MixedUser p, boolean isNew) {
        totalJoined++;
        if (isNew) totalNewPlayers++;
        if (isVersionDetectionEnabled()) joined.put(p.getUniqueId(), getProtocol(p.getUniqueId()));
    }

    public void registerPlaytime(long playtimeMillis) {
        long playtimeSeconds = playtimeMillis / 1000;
        playtimeRatio++;
        averagePlaytime += (playtimeSeconds - averagePlaytime) / playtimeRatio;
    }

    @Override
    public void sendMessage(MixedUser receiver, String messageCode, Object... args) {
        processMessage(receiver, messageCode, PREFIX, args);
    }

    @Override
    public void subMessage(MixedUser receiver, String messageCode, Object... args) {
        processMessage(receiver, messageCode, SUBLINE, args);
    }

    private void processMessage(MixedUser receiver, String messageCode, String prefix, Object... args) {

        String message = config.getConfigString("messages." + messageCode);

        if (message == null) {
            getLogger().warning("Missing message: " + messageCode);
            message = config.getConfigString("messages.error.general");

            if (message == null) {
                message = "&cAn internal error occurred..";
            }
        }

        if (message.isEmpty()) return;

        for (int i = 0; i < args.length; i++) {
            message = message.replace("{" + (i + 1) + "}", args[i].toString());
        }

        receiver.sendMessage(prefix + colorize(message));
    }

}
