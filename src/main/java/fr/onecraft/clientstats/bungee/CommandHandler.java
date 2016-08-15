package fr.onecraft.clientstats.bungee;

import fr.onecraft.clientstats.ClientStatsAPI;
import fr.onecraft.core.tuple.MutablePair;
import fr.onecraft.core.tuple.Pair;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.text.DateFormat;
import java.util.*;


public class CommandHandler extends Command {

    private final ClientStatsAPI plugin;

    public CommandHandler(ClientStatsAPI plugin) {
        super("clientstats", null, "cstats", "cs", "bclientstats", "bcstats", "bcs");
        this.plugin = plugin;
    }

    private boolean denied(CommandSender sender, String cmd) {
        if (sender.hasPermission(ClientStatsAPI.PERMISSION_ADMIN)
                || sender.hasPermission(ClientStatsAPI.PERMISSION_COMMAND.replace("{cmd}", cmd))) {
            return false;
        } else {
            plugin.sendMessage(sender, "error.permission");
            return true;
        }
    }

    private boolean versionDetectionDisabled(CommandSender sender) {
        if (!plugin.isVersionDetectionEnabled()) {
            plugin.sendMessage(sender, "warning.version-disabled");
            return true;
        }
        return false;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        execute(sender, Arrays.asList(args), "cstats");
    }

    private void execute(CommandSender sender, List<String> args, String label) {
        if (args.size() == 1) {

            if (args.get(0).equalsIgnoreCase("stats")) {
                if (denied(sender, "stats")) return;

                plugin.sendMessage(sender, "commands.stats.title");
                plugin.subMessage(sender, "commands.stats.unique", plugin.getUniqueJoined());
                // No new players on Bungeecord
                // plugin.subMessage(sender, "commands.stats.new", plugin.getTotalNewPlayers());
                plugin.subMessage(sender, "commands.stats.total", plugin.getTotalJoined());

                String date = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault())
                        .format(new Date(plugin.getMaxOnlineDate()));
                plugin.subMessage(sender, "commands.stats.max", plugin.getMaxOnlinePlayers(), date);

                long averagePlaytime = Math.round(plugin.getAveragePlaytime());
                long min = averagePlaytime / 60;
                long sec = averagePlaytime % 60;
                plugin.subMessage(sender, "commands.stats.playtime", min, sec);

                // Warn user if stats are low and he has exempt permission
                if (plugin.getUniqueJoined() < 10 && sender instanceof ProxiedPlayer && sender.hasPermission(ClientStatsAPI.EXEMPT_PERMISSION)) {
                    plugin.subMessage(sender, "warning.exempted");
                }

                return;

            } else if (args.get(0).equalsIgnoreCase("version")) {
                if (denied(sender, "version") || versionDetectionDisabled(sender)) return;

                Map<Integer, Pair<String, Integer>> versions = new TreeMap<>();
                int total = 0;

                for (Integer version : plugin.getProtocolJoined().values()) {
                    total++;
                    Pair<String, Integer> pair = versions.get(version);
                    if (pair == null) {
                        String versionName = plugin.getVersionName(version);
                        if (versionName != null) {
                            versions.put(version, MutablePair.of(versionName, 1));
                        }
                    } else {
                        pair.setRight(pair.getRight() + 1);
                        versions.put(version, pair);
                    }
                }

                plugin.sendMessage(sender, "commands.version.title");

                if (total == 0) {
                    plugin.subMessage(sender, "commands.version.empty");
                } else {
                    for (Pair<String, Integer> entry : versions.values()) {
                        plugin.subMessage(sender, "commands.version.list", entry.getValue(), entry.getKey(), Math.round(entry.getValue() * 100F / total));
                    }
                }

                // Warn user if stats are low and he has exempt permission
                if (total < 10 && sender instanceof ProxiedPlayer && sender.hasPermission(ClientStatsAPI.EXEMPT_PERMISSION)) {
                    plugin.subMessage(sender, "warning.exempted");
                }

                return;

            } else if (args.get(0).equalsIgnoreCase("online")) {
                if (denied(sender, "online") || versionDetectionDisabled(sender)) return;

                Map<Integer, Pair<String, Integer>> versions = new TreeMap<>();
                int total = 0;

                for (ProxiedPlayer online : ProxyServer.getInstance().getPlayers()) {
                    total++;
                    int version = plugin.getProtocol(online.getUniqueId());
                    Pair<String, Integer> pair = versions.get(version);
                    if (pair == null) {
                        String versionName = plugin.getVersionName(version);
                        if (versionName != null) {
                            versions.put(version, MutablePair.of(versionName, 1));
                        }
                    } else {
                        pair.setValue(pair.getValue() + 1);
                        versions.put(version, pair);
                    }
                }

                plugin.sendMessage(sender, "commands.online.title");

                if (total == 0) {
                    plugin.subMessage(sender, "commands.online.empty");
                } else {
                    for (Pair<String, Integer> entry : versions.values()) {
                        plugin.subMessage(sender, "commands.online.list", entry.getValue(), entry.getKey(), Math.round(entry.getValue() * 100F / total));
                    }
                }

                return;

            } else if (args.get(0).equalsIgnoreCase("player")) {
                if (denied(sender, "player") || versionDetectionDisabled(sender)) return;

                if (!(sender instanceof ProxiedPlayer)) {
                    plugin.sendMessage(sender, "error.not-a-player");
                    return;
                }

                Map.Entry<Integer, String> version = plugin.getVersion(((ProxiedPlayer) sender).getUniqueId());
                if (version == null) {
                    plugin.sendMessage(sender, "error.general");
                } else {
                    plugin.sendMessage(sender, "commands.player.self", version.getValue());
                }

                return;

            } else if (args.get(0).equalsIgnoreCase("reset")) {
                if (denied(sender, "reset")) return;

                plugin.resetStats();
                plugin.sendMessage(sender, "commands.reset");

                return;

            } else if (args.get(0).equalsIgnoreCase("reload")) {
                if (denied(sender, "reload")) return;

                plugin.reload();
                plugin.sendMessage(sender, "commands.reload");

                return;
            }

        } else if (args.size() == 2) {

            if (args.get(0).equalsIgnoreCase("player")) {
                if (denied(sender, "player") || versionDetectionDisabled(sender)) return;

                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args.get(1));
                if (player == null) {
                    plugin.sendMessage(sender, "commands.player.not-found", args.get(1));
                } else {
                    Map.Entry<Integer, String> version = plugin.getVersion(player.getUniqueId());
                    if (version == null) {
                        plugin.sendMessage(sender, "error.general");
                    } else {
                        plugin.sendMessage(sender, "commands.player.other", player.getName(), version.getValue());
                    }
                }

                return;

            }
        }

        if (!denied(sender, "help")) {

            String[] subCommands = {"stats", "version", "online", "player", "reset", "reload"};
            boolean isAdmin = sender.hasPermission(ClientStatsAPI.PERMISSION_ADMIN);
            List<String> messages = new ArrayList<>();

            for (String subCommand : subCommands) {
                if (isAdmin || sender.hasPermission(ClientStatsAPI.PERMISSION_COMMAND.replace("{cmd}", subCommand))) {
                    messages.add("commands.help." + subCommand);
                }
            }

            if (messages.isEmpty()) {
                plugin.sendMessage(sender, "error.permission");
            } else {
                plugin.sendMessage(sender, "commands.help.title");
                for (String message : messages) {
                    plugin.subMessage(sender, message, label);
                }
            }
        }

    }

}