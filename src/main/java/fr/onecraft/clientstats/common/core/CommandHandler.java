package fr.onecraft.clientstats.common.core;

import fr.onecraft.clientstats.ClientStatsAPI;
import fr.onecraft.clientstats.common.base.ServerType;
import fr.onecraft.clientstats.common.user.MixedUser;
import fr.onecraft.core.tuple.MutablePair;
import fr.onecraft.core.tuple.Pair;

import java.text.DateFormat;
import java.util.*;


public class CommandHandler {

    private static final List<String> EMPTY_LIST = Collections.emptyList();
    private static final List<String> SUB_COMMANDS = Arrays.asList("stats", "version", "online", "player", "reset", "reload");

    private final ClientStatsAPI api;

    public CommandHandler(ClientStatsAPI api) {
        this.api = api;
    }

    private boolean denied(MixedUser sender, String cmd) {
        return denied(sender, cmd, true);
    }

    private boolean denied(MixedUser sender, String cmd, boolean verbose) {
        if (sender.hasPermission(ClientStatsAPI.PERMISSION_ADMIN)
                || sender.hasPermission(ClientStatsAPI.PERMISSION_COMMAND.replace("{cmd}", cmd))) {
            return false;
        } else {
            if (verbose) api.sendMessage(sender, "error.permission");
            return true;
        }
    }

    private boolean versionDetectionDisabled(MixedUser sender) {
        if (!api.isVersionDetectionEnabled()) {
            api.sendMessage(sender, "warning.version-disabled");
            return true;
        }
        return false;
    }

    private List<String> filter(Iterable<String> list, String token) {
        List<String> completions = new ArrayList<>();
        for (String completion : list) {
            if (token == null || completion.startsWith(token)) {
                completions.add(completion);
            }
        }
        return completions.size() == 1 ? Collections.singletonList(completions.get(0) + " ") : completions;
    }

    public List<String> complete(MixedUser sender, List<String> args, String token) {
        if (args.size() == 1) {
            if (args.get(0).equalsIgnoreCase("player")) {
                return filter(MixedUser.getOnlineNames(), token);
            }
        } else if (args.size() == 0) {
            return filter(SUB_COMMANDS, token);
        }
        return EMPTY_LIST;
    }

    public void execute(MixedUser sender, List<String> args, String label) {
        if (args.size() == 1) {

            if (args.get(0).equalsIgnoreCase("stats")) {
                if (denied(sender, "stats")) return;

                api.sendMessage(sender, "commands.stats.title");
                api.subMessage(sender, "commands.stats.unique", api.getUniqueJoined());

                if (api.getServerType() != ServerType.BUNGEE) {
                    api.subMessage(sender, "commands.stats.new", api.getTotalNewPlayers());
                }

                api.subMessage(sender, "commands.stats.total", api.getTotalJoined());

                String date = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault())
                        .format(new Date(api.getMaxOnlineDate()));
                api.subMessage(sender, "commands.stats.max", api.getMaxOnlinePlayers(), date);

                long averagePlaytime = Math.round(api.getAveragePlaytime());
                long min = averagePlaytime / 60;
                long sec = averagePlaytime % 60;
                api.subMessage(sender, "commands.stats.playtime", min, sec);

                // Warn user if stats are low and he has exempt permission
                if (api.getUniqueJoined() < 10 && sender.isPlayer() && sender.hasPermission(ClientStatsAPI.EXEMPT_PERMISSION)) {
                    api.subMessage(sender, "warning.exempted");
                }

                return;

            } else if (args.get(0).equalsIgnoreCase("version")) {
                if (denied(sender, "version") || versionDetectionDisabled(sender)) return;

                Map<Integer, Pair<String, Integer>> versions = new TreeMap<>();
                int total = 0;

                for (Integer version : api.getProtocolJoined().values()) {
                    total++;
                    Pair<String, Integer> pair = versions.get(version);
                    if (pair == null) {
                        String versionName = api.getVersionName(version);
                        if (versionName != null) {
                            versions.put(version, MutablePair.of(versionName, 1));
                        }
                    } else {
                        pair.setRight(pair.getRight() + 1);
                        versions.put(version, pair);
                    }
                }

                api.sendMessage(sender, "commands.version.title");

                if (total == 0) {
                    api.subMessage(sender, "commands.version.empty");
                } else {
                    for (Pair<String, Integer> entry : versions.values()) {
                        api.subMessage(sender, "commands.version.list", entry.getValue(), entry.getKey(), Math.round(entry.getValue() * 100F / total));
                    }
                }

                // Warn user if stats are low and he has exempt permission
                if (total < 10 && sender.isPlayer() && sender.hasPermission(ClientStatsAPI.EXEMPT_PERMISSION)) {
                    api.subMessage(sender, "warning.exempted");
                }

                return;

            } else if (args.get(0).equalsIgnoreCase("online")) {
                if (denied(sender, "online") || versionDetectionDisabled(sender)) return;

                Map<Integer, Pair<String, Integer>> versions = new TreeMap<>();
                int total = 0;

                for (UUID online : MixedUser.getOnlineIds()) {
                    total++;
                    int version = api.getProtocol(online);
                    Pair<String, Integer> pair = versions.get(version);
                    if (pair == null) {
                        String versionName = api.getVersionName(version);
                        if (versionName != null) {
                            versions.put(version, MutablePair.of(versionName, 1));
                        }
                    } else {
                        pair.setValue(pair.getValue() + 1);
                        versions.put(version, pair);
                    }
                }

                api.sendMessage(sender, "commands.online.title");

                if (total == 0) {
                    api.subMessage(sender, "commands.online.empty");
                } else {
                    for (Pair<String, Integer> entry : versions.values()) {
                        api.subMessage(sender, "commands.online.list", entry.getValue(), entry.getKey(), Math.round(entry.getValue() * 100F / total));
                    }
                }

                return;

            } else if (args.get(0).equalsIgnoreCase("player")) {
                if (denied(sender, "player") || versionDetectionDisabled(sender)) return;

                if (!sender.isPlayer()) {
                    api.sendMessage(sender, "error.not-a-player");
                    return;
                }

                Map.Entry<Integer, String> version = api.getVersion(sender.getUniqueId());
                if (version == null) {
                    api.sendMessage(sender, "error.general");
                } else {
                    api.sendMessage(sender, "commands.player.self", version.getValue());
                }

                return;

            } else if (args.get(0).equalsIgnoreCase("reset")) {
                if (denied(sender, "reset")) return;

                api.resetStats();
                api.sendMessage(sender, "commands.reset");

                return;

            } else if (args.get(0).equalsIgnoreCase("reload")) {
                if (denied(sender, "reload")) return;

                api.reload();
                api.sendMessage(sender, "commands.reload");

                return;
            }

        } else if (args.size() == 2) {

            if (args.get(0).equalsIgnoreCase("player")) {
                if (denied(sender, "player") || versionDetectionDisabled(sender)) return;

                MixedUser player = MixedUser.getUser(args.get(1));
                if (player == null) {
                    api.sendMessage(sender, "commands.player.not-found", args.get(1));
                } else {
                    Map.Entry<Integer, String> version = api.getVersion(player.getUniqueId());
                    if (version == null) {
                        api.sendMessage(sender, "error.general");
                    } else {
                        api.sendMessage(sender, "commands.player.other", player.getName(), version.getValue());
                    }
                }

                return;

            }
        }

        if (!denied(sender, "help")) {

            boolean isAdmin = sender.hasPermission(ClientStatsAPI.PERMISSION_ADMIN);
            List<String> messages = new ArrayList<>();

            for (String subCommand : SUB_COMMANDS) {
                if (isAdmin || sender.hasPermission(ClientStatsAPI.PERMISSION_COMMAND.replace("{cmd}", subCommand))) {
                    messages.add("commands.help." + subCommand);
                }
            }

            if (messages.isEmpty()) {
                api.sendMessage(sender, "error.permission");
            } else {
                api.sendMessage(sender, "commands.help.title");
                for (String message : messages) {
                    api.subMessage(sender, message, label);
                }
            }
        }

    }

}