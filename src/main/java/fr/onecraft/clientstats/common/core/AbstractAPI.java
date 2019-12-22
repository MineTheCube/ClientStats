package fr.onecraft.clientstats.common.core;

import static fr.onecraft.clientstats.common.user.MixedUser.getOnlineCount;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import fr.onecraft.clientstats.ClientStatsAPI;
import fr.onecraft.clientstats.common.base.Configurable;
import fr.onecraft.clientstats.common.base.VersionProvider;
import fr.onecraft.clientstats.common.user.MixedUser;
import fr.onecraft.core.helpers.Locales;
import fr.onecraft.core.tuple.Pair;

public abstract class AbstractAPI implements ClientStatsAPI {

    // Default datetime format
    protected final static DateFormat DEFAULT_DATETIME_FORMAT = DateFormat.getDateTimeInstance(DateFormat.SHORT,
	    DateFormat.SHORT);

    // Config values
    protected String PREFIX = "§9[ClientStats] §f";
    protected String SUBLINE = "§f";
    protected DateFormat dateTimeFormat = DEFAULT_DATETIME_FORMAT;

    // Map of UUID -> Protocol version
    private final Map<UUID, Integer> joined = new HashMap<>();

    // Version provider
    private final VersionProvider provider;
    private final Configurable config;

    // Statistics
    private int totalJoined = 0;
    private int totalNewPlayers = 0;
    private int maxOnlinePlayers = getOnlineCount();
    private long maxOnlineDate = System.currentTimeMillis();
    private long startOfRecording = System.currentTimeMillis();

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
	this.config.saveDefaultConfig();

	// Reload to get latest values
	this.config.reloadConfig();

	// Migrate over versions
	this.config.migrate();

	// Copy headers and new values
	this.config.options();

	// And save it
	this.config.saveConfig();

	// Get prefixes
	this.PREFIX = this.colorize(this.config.getConfigString("messages.prefix", this.PREFIX));
	this.SUBLINE = this.colorize(this.config.getConfigString("messages.subline", this.SUBLINE));

	// Get date format
	String userFormat = this.config.getConfigString("settings.date-format", "default");
	DateFormat userDateFormat = null;

	if (userFormat.equalsIgnoreCase("default")) {
	    // Default format
	    userDateFormat = DEFAULT_DATETIME_FORMAT;
	} else {
	    Locale locale = Locales.valueOf(userFormat);
	    if (locale != null) {
		// It's a locale
		DateFormat dateTimeInstance = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT,
			locale);
		if (dateTimeInstance != null) {
		    userDateFormat = dateTimeInstance;
		}
	    } else if (userFormat.length() > 2) {
		// It may be a date format
		try {
		    userDateFormat = new SimpleDateFormat(userFormat);
		} catch (IllegalArgumentException ignored) {
		}
	    }
	}

	if (userDateFormat == null) {
	    this.getLogger().warning("Invalid date-format value, reverting to default.");
	    this.dateTimeFormat = DEFAULT_DATETIME_FORMAT;
	    this.config.setConfigValue("settings.date-format", "default");
	    this.config.saveConfig();
	} else {
	    this.dateTimeFormat = userDateFormat;
	}
    }

    @Override
    public void resetStats() {
	this.joined.clear();
	this.totalJoined = 0;
	this.maxOnlinePlayers = MixedUser.getOnlineCount();
	this.maxOnlineDate = System.currentTimeMillis();
	this.averagePlaytime = 0;
	this.playtimeRatio = 0;
	this.startOfRecording = System.currentTimeMillis();
    }

    @Override
    public boolean isVersionDetectionEnabled() {
	return this.isEnabled() && (this.provider != null);
    }

    @Override
    public int getTotalJoined() {
	return this.totalJoined;
    }

    @Override
    public int getTotalNewPlayers() {
	return this.totalNewPlayers;
    }

    @Override
    public int getUniqueJoined() {
	return this.joined.size();
    }

    @Override
    public int getMaxOnlinePlayers() {
	return this.maxOnlinePlayers;
    }

    @Override
    public long getMaxOnlineDate() {
	return this.maxOnlineDate;
    }

    @Override
    public double getAveragePlaytime() {
	return this.averagePlaytime;
    }

    @Nonnull
    @Override
    public Map<UUID, Integer> getProtocolJoined() {
	return Collections.unmodifiableMap(this.joined);
    }

    @Nonnull
    @Override
    public String getVersionName(int version) {
	String versionName = VersionNameProvider.get(version);

	if (versionName == null) {
	    this.getLogger().warning("Unknown version: " + version);
	    versionName = "Unknown";
	}

	return versionName;
    }

    @Override
    public int getProtocol(@Nonnull UUID player) {
	return this.isVersionDetectionEnabled() ? this.provider.getProtocol(player) : 0;
    }

    @Nullable
    @Override
    public Pair<Integer, String> getVersion(@Nonnull UUID player) {
	int version = this.getProtocol(player);
	if (version == 0)
	    return null;
	String versionName = this.getVersionName(version);
	return Pair.of(version, versionName);
    }

    public void updatePlayerCount() {
	int online = MixedUser.getOnlineCount();
	if (online > this.maxOnlinePlayers) {
	    this.maxOnlinePlayers = online;
	    this.maxOnlineDate = System.currentTimeMillis();
	}
    }

    public void registerJoin(MixedUser p, boolean isNew) {
	this.totalJoined++;
	if (isNew)
	    this.totalNewPlayers++;
	if (this.isVersionDetectionEnabled())
	    this.joined.put(p.getUniqueId(), this.getProtocol(p.getUniqueId()));
    }

    public void registerPlaytime(long playtimeMillis) {
	long playtimeSeconds = playtimeMillis / 1000;
	this.playtimeRatio++;
	this.averagePlaytime += (playtimeSeconds - this.averagePlaytime) / this.playtimeRatio;
    }

    @Override
    public long getStartOfRecording() {
	return this.startOfRecording;
    }

    @Nonnull
    @Override
    public DateFormat getDateTimeFormat() {
	return this.dateTimeFormat;
    }

    @Override
    public void sendMessage(MixedUser receiver, String messageCode, Object... args) {
	this.processMessage(receiver, messageCode, this.PREFIX, args);
    }

    @Override
    public void subMessage(MixedUser receiver, String messageCode, Object... args) {
	this.processMessage(receiver, messageCode, this.SUBLINE, args);
    }

    private void processMessage(MixedUser receiver, String messageCode, String prefix, Object... args) {

	String message = this.config.getConfigString("messages." + messageCode);

	if ((message == null) || message.isEmpty()) {
	    this.getLogger().warning("Missing message: " + messageCode);
	    message = this.config.getConfigString("messages.error.general");

	    if ((message == null) || message.isEmpty()) {
		message = "&cAn internal error occurred..";
	    }
	}

	if (message.isEmpty())
	    return;

	for (int i = 0; i < args.length; i++) {
	    message = message.replace("{" + (i + 1) + "}", args[i].toString());
	}

	receiver.sendMessage(prefix + this.colorize(message));
    }

}
