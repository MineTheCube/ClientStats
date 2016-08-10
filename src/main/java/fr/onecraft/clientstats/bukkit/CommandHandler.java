package fr.onecraft.clientstats.bukkit;

import fr.onecraft.core.command.CommandRegister;
import fr.onecraft.core.command.CommandUser;
import fr.onecraft.core.plugin.Core;
import fr.onecraft.core.tuple.MutablePair;
import fr.onecraft.core.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class CommandHandler extends CommandRegister {

    private final BukkitClientStats plugin = Core.instance();

    private boolean denied(CommandUser sender, String cmd) {
        if (sender.hasPermission("clientstats.admin") || sender.hasPermission("clientstats.cmd." + cmd)) {
            return false;
        } else {
            plugin.sendMessage(sender, "error.permission");
            return true;
        }
    }

    @Override
    protected void execute(CommandUser sender, List<String> args, Command cmd, String label) {
        if (args.size() == 1) {

            if (args.get(0).equalsIgnoreCase("stats")) {
                if (denied(sender, "stats")) return;

                plugin.sendMessage(sender, "commands.stats.title");
                plugin.subMessage(sender, "commands.stats.unique", plugin.getUniqueJoined());
                plugin.subMessage(sender, "commands.stats.new", plugin.getTotalNewPlayers());
                plugin.subMessage(sender, "commands.stats.total", plugin.getTotalJoined());
                long averagePlaytime = Math.round(plugin.getAveragePlaytime());
                long min = averagePlaytime / 60;
                long sec = averagePlaytime % 60;
                plugin.subMessage(sender, "commands.stats.playtime", min, sec);
                return;

            } else if (args.get(0).equalsIgnoreCase("version")) {
                if (denied(sender, "version")) return;

                Map<Integer, Pair<String, Integer>> versions = new TreeMap<>();
                int total = 0;

                for (Integer version : plugin.getProtocolJoined().values()) {
                    total++;
                    Pair<String, Integer> entry = versions.get(version);
                    if (entry == null) {
                        String versionName = plugin.getVersionName(version);
                        if (versionName != null) {
                            versions.put(version, MutablePair.of(versionName, 1));
                        }
                    } else {
                        entry.setValue(entry.getValue() + 1);
                        versions.put(version, entry);
                    }
                }

                // For testing purpose
                // TODO: Use unit testing
//    			versions = new TreeMap<>();
//    			versions.put(47, Pair.of(plugin.getVersionName(47), 210)); // 1.8
//    			versions.put(107, Pair.of(plugin.getVersionName(107), 79)); // 1.9
//    			versions.put(108, Pair.of(plugin.getVersionName(108), 14)); // 1.9.1
//    			versions.put(109, Pair.of(plugin.getVersionName(109), 56)); // 1.9.2
//    			versions.put(110, Pair.of(plugin.getVersionName(110), 478)); // 1.9.4
//    			int total = 210 + 79 + 14 + 56 + 478;

                plugin.sendMessage(sender, "commands.version.title");

                if (total == 0) {
                    plugin.subMessage(sender, "commands.version.empty");
                } else {
                    for (Pair<String, Integer> entry : versions.values()) {
                        plugin.subMessage(sender, "commands.version.list", entry.getValue(), entry.getKey(), Math.round(entry.getValue() * 100F / total));
                    }
                }

                return;

            } else if (args.get(0).equalsIgnoreCase("online")) {
                if (denied(sender, "online")) return;

                Map<Integer, Pair<String, Integer>> versions = new TreeMap<>();
                int total = 0;

                for (Player online : Bukkit.getOnlinePlayers()) {
                    total++;
                    int version = plugin.getProtocol(online.getUniqueId());
                    Pair<String, Integer> entry = versions.get(version);
                    if (entry == null) {
                        String versionName = plugin.getVersionName(version);
                        if (versionName != null) {
                            versions.put(version, MutablePair.of(versionName, 1));
                        }
                    } else {
                        entry.setValue(entry.getValue() + 1);
                        versions.put(version, entry);
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
                if (denied(sender, "player")) return;

                if (!sender.isPlayer()) {
                    plugin.sendMessage(sender, "error.not-a-player");
                    return;
                }

                Pair<Integer, String> version = plugin.getVersion(sender.getPlayer().getUniqueId());
                if (version == null) {
                    plugin.sendMessage(sender, "error.general");
                } else {
                    plugin.sendMessage(sender, "commands.player.self", version.getRight());
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
                if (denied(sender, "player")) return;

                Player player = Bukkit.getPlayer(args.get(1));
                if (player == null) {
                    plugin.sendMessage(sender, "commands.player.not-found", args.get(1));
                } else {
                    Pair<Integer, String> version = plugin.getVersion(player.getUniqueId());
                    if (version == null) {
                        plugin.sendMessage(sender, "error.general");
                    } else {
                        plugin.sendMessage(sender, "commands.player.other", player.getName(), version.getRight());
                    }
                }

                return;

            }
        }

        if (!denied(sender, "help")) {

            String[] subCommands = {"stats", "version", "online", "player", "reset", "reload"};
            boolean isAdmin = sender.hasPermission("clientstats.admin");
            List<String> messages = new ArrayList<>();

            for (String subCommand : subCommands) {
                if (isAdmin || sender.hasPermission("clientstats.cmd." + subCommand)) {
                    messages.add("commands.help." + subCommand);
                }
            }

            if (messages.isEmpty()) {
                plugin.sendMessage(sender, "error.permission");
            } else {
                plugin.sendMessage(sender, "commands.help.title");
                for (String message : messages) {
                    plugin.subMessage(sender, message);
                }
            }
        }

    }

}