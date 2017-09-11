package fr.onecraft.clientstats;

import fr.onecraft.clientstats.common.base.ServerType;
import fr.onecraft.clientstats.common.user.MixedUser;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.DateFormat;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public interface ClientStatsAPI {

    /**
     * Permission to use every commands
     */
    String PERMISSION_ADMIN = "clientstats.admin";

    /**
     * Permission to use a specific command
     */
    String PERMISSION_COMMAND = "clientstats.cmd.{cmd}";

    /**
     * Permission to be excluded from statistics
     */
    String EXEMPT_PERMISSION = "clientstats.exempt";

    /**
     * Whether API is enabled or not
     *
     * @return True if you can use API
     */
    boolean isEnabled();

    /**
     * Whether version detection is enabled or not
     * <p>
     * Version detection is needed for these method to work:
     * <ul>
     *     <li>{@link #getProtocolJoined}</li>
     *     <li>{@link #getProtocol(UUID)}</li>
     *     <li>{@link #getVersion(UUID)}</li>
     * </ul>
     *
     * @return True if you can use version methods
     */
    boolean isVersionDetectionEnabled();

    /**
     * Get how many total players joined
     * <p>
     * If a player joins the server twice, it will return 2
     *
     * @return Total players joined
     */
    int getTotalJoined();

    /**
     * Get how many new players joined
     *
     * @return Total new players
     */
    int getTotalNewPlayers();

    /**
     * Get how many different players joined
     * <p>
     * If a player joins the server twice, it will return 1
     *
     * @return Different players joined
     */
    int getUniqueJoined();

    /**
     * Get the maximum of online players
     *
     * @return Maximum player count
     */
    int getMaxOnlinePlayers();

    /**
     * Get date when maximum of online players was reached
     * <p>
     * Number returned is the amount of milliseconds since midnight, January 1, 1970 UTC
     *
     * @return Timestamp of maximum player count
     */
    long getMaxOnlineDate();

    /**
     * Get map of UUID -> protocol version used on last join
     * <p>
     * The map is view only, you can't modify it
     *
     * @return Map of UUID -> protocol version
     */
    @Nonnull
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
    @Nonnull
    String getVersionName(int protocol);

    /**
     * Get protocol version of an online player
     *
     * @param player Player to get protocol version
     * @return Protocol version of player, or 0 if player is not found
     */
    int getProtocol(@Nonnull UUID player);

    /**
     * Get protocol version and version name of an online player
     *
     * @param player Player to get version
     * @return Version of player, or null if player is not found
     */
    @Nullable
    Entry<Integer, String> getVersion(@Nonnull UUID player);

    /**
     * Reset current stats
     */
    void resetStats();

    /**
     * Reload plugin's configuration
     */
    void reload();

    /**
     * Get start of statistics recording
     * <p>
     * Number returned is the amount of milliseconds since midnight, January 1, 1970 UTC
     *
     * @return Timestamp when the statistics started to be recorded
     */
    long getStartOfRecording();

    /**
     * Get current server type
     *
     * Mainly used for internal purposes
     *
     * @return Server type
     */
    @Nonnull
    ServerType getServerType();

    /**
     * Get datetime format specified in config
     *
     * @return Datetime format
     */
    @Nonnull
    DateFormat getDateTimeFormat();

    /**
     * Send a message to receiver with main prefix
     *
     * @param receiver Receiver of the message
     * @param messageCode Config path of message
     * @param args Arguments that replace {1}, {2}, etc
     */
    void sendMessage(MixedUser receiver, String messageCode, Object... args);

    /**
     * Send a message to receiver with short prefix
     *
     * @param receiver Receiver of the message
     * @param messageCode Config path of message
     * @param args Arguments that replace {1}, {2}, etc
     */
    void subMessage(MixedUser receiver, String messageCode, Object... args);

}
