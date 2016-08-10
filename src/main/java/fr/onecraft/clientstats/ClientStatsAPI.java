package fr.onecraft.clientstats;

import java.util.Map;
import java.util.UUID;

public interface ClientStatsAPI {

    /**
     * Whether API is enabled or not
     *
     * @return true if you can use API
     */
    boolean isEnabled();

    /**
     * Get how many total players joined
     *
     * If a player joins the server twice, it will return 2
     *
     * @return total players joined
     */
    int getTotalJoined();

    /**
     * Get how many new players joined
     *
     * @return total new players
     */
    int getTotalNewPlayers();

    /**
     * Get how many different players joined
     *
     * If a player joins the server twice, it will return 1
     *
     * @return different players joined
     */
    int getUniqueJoined();

    /**
     * Get map of UUID -> protocol version used on last join
     *
     * The map is view only, you can't modify it
     *
     * @return Map of UUID -> protocol version
     */
    Map<UUID, Integer> getProtocolJoined();

    /**
     * Get average playtime in seconds
     *
     * @return Average playtime
     */
    double getAveragePlaytime();

    /**
     * Get the version name from protocol number
     *
     * @param protocol The protocol number
     * @return Version name
     */
    String getVersionName(int protocol);

    /**
     * Get protocol version of an online player
     *
     * @param player Player to get protocol version
     * @return Protocol version of player, or 0 if player is not found
     */
    int getProtocol(UUID player);

    /**
     * Get protocol version and version name of an online player
     *
     * @param player Player to get version
     * @return Version of player, or null if player is not found
     */
    Map.Entry<Integer, String> getVersion(UUID player);

    /**
     * Reset current stats
     */
    void resetStats();

    /**
     * Reload plugin's configuration
     */
    void reload();

}
