package fr.onecraft.clientstats.common.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.http.concurrent.FutureCallback;

import fr.onecraft.clientstats.ClientStatsAPI;
import fr.onecraft.clientstats.common.base.ServerType;
import fr.onecraft.clientstats.common.user.MixedUser;
import fr.onecraft.core.tuple.MutablePair;
import fr.onecraft.core.tuple.Pair;

public class CommandHandler {

    private static final List<String> EMPTY_LIST = Collections.emptyList();
    private static final List<String> SUB_COMMANDS = Arrays.asList("stats", "version", "online", "player", "reset",
	    "reload");

    private final ClientStatsAPI api;

    public CommandHandler(ClientStatsAPI api) {
	this.api = api;
    }

    private boolean denied(MixedUser sender, String cmd) {
	return this.denied(sender, cmd, true);
    }

    private boolean denied(MixedUser sender, String cmd, boolean verbose) {
	if (sender.hasPermission(ClientStatsAPI.PERMISSION_ADMIN)
		|| sender.hasPermission(ClientStatsAPI.PERMISSION_COMMAND.replace("{cmd}", cmd))) {
	    return false;
	} else {
	    if (verbose)
		this.api.sendMessage(sender, "error.permission");
	    return true;
	}
    }

    private boolean versionDetectionDisabled(MixedUser sender) {
	if (!this.api.isVersionDetectionEnabled()) {
	    this.api.sendMessage(sender, "warning.version-disabled");
	    return true;
	}
	return false;
    }

    private List<String> filter(Iterable<String> list, String token, MixedUser commandUser) {
	List<String> completions = new ArrayList<>();
	token = (token == null) || token.isEmpty() ? null : token.toLowerCase();
	for (String s : list) {
	    if (((token == null) || s.toLowerCase().startsWith(token))
		    && ((commandUser == null) || !this.denied(commandUser, s, false))) {
		completions.add(s);
	    }
	}
	return completions.size() == 1 ? Collections.singletonList(completions.get(0) + " ") : completions;
    }

    public List<String> complete(MixedUser sender, List<String> args, String token) {
	if (args.size() == 1) {
	    if (args.get(0).equalsIgnoreCase("player") && !this.denied(sender, "player")) {
		return this.filter(MixedUser.getOnlineNames(), token, null);
	    }
	} else if (args.size() == 0) {
	    return this.filter(SUB_COMMANDS, token, sender);
	}
	return EMPTY_LIST;
    }

    public void execute(MixedUser sender, List<String> args, String label) {
	if (args.size() == 1) {

	    if (args.get(0).equalsIgnoreCase("stats")) {
		if (this.denied(sender, "stats"))
		    return;

		this.api.sendMessage(sender, "commands.stats.title",
			this.api.getDateTimeFormat().format(new Date(this.api.getStartOfRecording())));
		this.api.subMessage(sender, "commands.stats.unique", this.api.getUniqueJoined());

		if (this.api.getServerType() != ServerType.BUNGEE) {
		    this.api.subMessage(sender, "commands.stats.new", this.api.getTotalNewPlayers());
		}

		this.api.subMessage(sender, "commands.stats.total", this.api.getTotalJoined());

		String date = this.api.getDateTimeFormat().format(new Date(this.api.getMaxOnlineDate()));
		this.api.subMessage(sender, "commands.stats.max", this.api.getMaxOnlinePlayers(), date);

		long averagePlaytime = Math.round(this.api.getAveragePlaytime());
		long min = averagePlaytime / 60;
		long sec = averagePlaytime % 60;
		this.api.subMessage(sender, "commands.stats.playtime", min, sec);

		// Warn user if stats are low and he has exempt permission
		if ((this.api.getUniqueJoined() < 10) && sender.isPlayer()
			&& sender.hasPermission(ClientStatsAPI.EXEMPT_PERMISSION)) {
		    this.api.subMessage(sender, "warning.exempted");
		}

		return;

	    } else if (args.get(0).equalsIgnoreCase("version")) {
		if (this.denied(sender, "version") || this.versionDetectionDisabled(sender))
		    return;

		Map<Integer, Pair<String, Integer>> versions = new TreeMap<>();
		int total = 0;

		for (Integer version : this.api.getProtocolJoined().values()) {
		    total++;
		    Pair<String, Integer> pair = versions.get(version);
		    if (pair == null) {
			String versionName = this.api.getVersionName(version);
			versions.put(version, MutablePair.of(versionName, 1));
		    } else {
			pair.setRight(pair.getRight() + 1);
			versions.put(version, pair);
		    }
		}

		this.api.sendMessage(sender, "commands.version.title");

		if (total == 0) {
		    this.api.subMessage(sender, "commands.version.empty");
		} else {
		    for (Pair<String, Integer> pair : versions.values()) {
			this.api.subMessage(sender, "commands.version.list", pair.getRight(), pair.getLeft(),
				Math.round((pair.getRight() * 100F) / total));
		    }
		}

		// Warn user if stats are low and he has exempt permission
		if ((total < 10) && sender.isPlayer() && sender.hasPermission(ClientStatsAPI.EXEMPT_PERMISSION)) {
		    this.api.subMessage(sender, "warning.exempted");
		}

		return;

	    } else if (args.get(0).equalsIgnoreCase("online")) {
		if (this.denied(sender, "online") || this.versionDetectionDisabled(sender))
		    return;

		Map<Integer, Pair<String, Integer>> versions = new TreeMap<>();
		int total = 0;

		for (UUID online : MixedUser.getOnlineIds()) {
		    total++;
		    int version = this.api.getProtocol(online);
		    Pair<String, Integer> pair = versions.get(version);
		    if (pair == null) {
			String versionName = this.api.getVersionName(version);
			versions.put(version, MutablePair.of(versionName, 1));
		    } else {
			pair.setValue(pair.getRight() + 1);
			versions.put(version, pair);
		    }
		}

		this.api.sendMessage(sender, "commands.online.title");

		if (total == 0) {
		    this.api.subMessage(sender, "commands.online.empty");
		} else {
		    for (Pair<String, Integer> pair : versions.values()) {
			this.api.subMessage(sender, "commands.online.list", pair.getRight(), pair.getLeft(),
				Math.round((pair.getRight() * 100F) / total));
		    }
		}

		return;

	    } else if (args.get(0).equalsIgnoreCase("player")) {
		if (this.denied(sender, "player") || this.versionDetectionDisabled(sender))
		    return;

		if (!sender.isPlayer()) {
		    this.api.sendMessage(sender, "error.not-a-player");
		    return;
		}

		Map.Entry<Integer, String> version = this.api.getVersion(sender.getUniqueId());
		if (version == null) {
		    this.api.sendMessage(sender, "error.general");
		} else {
		    this.api.sendMessage(sender, "commands.player.self", version.getValue());
		}

		return;

	    } else if (args.get(0).equalsIgnoreCase("reset")) {
		if (this.denied(sender, "reset"))
		    return;

		this.api.resetStats();
		this.api.sendMessage(sender, "commands.reset");

		return;

	    } else if (args.get(0).equalsIgnoreCase("reload")) {
		if (this.denied(sender, "reload"))
		    return;

		this.api.reload();
		this.api.sendMessage(sender, "commands.reload.config");

		this.api.sendMessage(sender, "commands.reload.version-start");
		VersionNameProvider.reloadLater(true, ((AbstractAPI) this.api).getLogger(), new FutureCallback<Void>() {

		    @Override
		    public void completed(Void result) {
			CommandHandler.this.api.sendMessage(sender, "commands.reload.version-end");
		    }

		    @Override
		    public void failed(Exception ex) {
			CommandHandler.this.api.sendMessage(sender, "commands.reload.version-failed");
			ex.printStackTrace();
		    }

		    @Override
		    public void cancelled() {
			CommandHandler.this.api.sendMessage(sender, "commands.reload.version-failed");
		    }

		});

		return;
	    }

	} else if (args.size() == 2) {

	    if (args.get(0).equalsIgnoreCase("player")) {
		if (this.denied(sender, "player") || this.versionDetectionDisabled(sender))
		    return;

		MixedUser player = MixedUser.getUser(args.get(1));
		if (player == null) {
		    this.api.sendMessage(sender, "commands.player.not-found", args.get(1));
		} else {
		    Map.Entry<Integer, String> version = this.api.getVersion(player.getUniqueId());
		    if (version == null) {
			this.api.sendMessage(sender, "error.general");
		    } else {
			this.api.sendMessage(sender, "commands.player.other", player.getName(), version.getValue());
		    }
		}

		return;

	    }
	}

	boolean isAdmin = sender.hasPermission(ClientStatsAPI.PERMISSION_ADMIN);
	List<String> messages = new ArrayList<>();

	for (String subCommand : SUB_COMMANDS) {
	    if (isAdmin || sender.hasPermission(ClientStatsAPI.PERMISSION_COMMAND.replace("{cmd}", subCommand))) {
		messages.add("commands.help." + subCommand);
	    }
	}

	if (messages.isEmpty()) {
	    this.api.sendMessage(sender, "error.permission");
	} else {
	    this.api.sendMessage(sender, "commands.help.title");
	    for (String message : messages) {
		this.api.subMessage(sender, message, label);
	    }
	}
    }

}