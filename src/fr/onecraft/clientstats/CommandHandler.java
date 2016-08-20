package fr.onecraft.clientstats;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {

    private Main plugin;

    public CommandHandler(Main plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if (!(sender instanceof Player)) {
    		plugin.sendMessage(sender, "error.not-a-player");
    	}

    	if (args.length == 1) {
    		if (args[0].equalsIgnoreCase("joined")) {
    			if (!sender.hasPermission("clientstats.cmd.joined") && !sender.hasPermission("clientstats.admin")) {
    				plugin.sendMessage(sender, "error.permission");
    				return true;
    			}

    			int uniquePlayers = plugin.joinedPlayers.size();
    			int totalPlayers = plugin.totalJoined;

    			plugin.sendMessage(sender, "commands.joined.unique", String.valueOf(uniquePlayers));
    			plugin.sendMessage(sender, "commands.joined.total", String.valueOf(totalPlayers));

    			return true;

    		} else if (args[0].equalsIgnoreCase("version")) {
    			if (!sender.hasPermission("clientstats.cmd.version") && !sender.hasPermission("clientstats.admin")) {
    				plugin.sendMessage(sender, "error.permission");
    				return true;
    			}

    			HashMap<String, Integer> versions = new HashMap<String, Integer>();

    			for (String version : plugin.joinedPlayers.values()) {

    				Integer i = versions.get(version);
    				if (i == null) {
        				versions.put(version, 1);
    				} else {
        				versions.put(version, i + 1);
    				}
    			}

    			plugin.sendMessage(sender, "commands.version.title");

    			if (versions.size() == 0) {
    				plugin.sendMessage(sender, "commands.version.empty");
    			}

    			for (Entry<String, Integer> entry: versions.entrySet()) {
    				plugin.sendMessage(sender, "commands.version.list", entry.getValue().toString(), entry.getKey());
    			}

    			return true;

    		} else if (args[0].equalsIgnoreCase("online")) {
    			if (!sender.hasPermission("clientstats.cmd.online") && !sender.hasPermission("clientstats.admin")) {
    				plugin.sendMessage(sender, "error.permission");
    				return true;
    			}

    			HashMap<String, Integer> versions = new HashMap<String, Integer>();

    			for (Player player: Bukkit.getOnlinePlayers()) {

    				String version = plugin.getVersion(player);

    				if (version != null) {

	    				Integer i = versions.get(version);
	    				if (i == null) {
	        				versions.put(version, 1);
	    				} else {
	        				versions.put(version, i + 1);
	    				}

    				}
    			}

    			plugin.sendMessage(sender, "commands.online.title");

    			if (versions.size() == 0) {
    				plugin.sendMessage(sender, "commands.online.empty");
    			}

    			for (Entry<String, Integer> entry: versions.entrySet()) {
    				plugin.sendMessage(sender, "commands.online.list", entry.getValue().toString(), entry.getKey());
    			}

    			return true;

    		} else if (args[0].equalsIgnoreCase("player")) {
    			if (!sender.hasPermission("clientstats.cmd.player") && !sender.hasPermission("clientstats.admin")) {
    				plugin.sendMessage(sender, "error.permission");
    				return true;
    			}

    			String version = plugin.getVersion((Player) sender);
    			if (version == null) {
        			plugin.sendMessage(sender, "error.general");
    			} else {
        			plugin.sendMessage(sender, "commands.player.self", version);
    			}

    			return true;

    		} else if (args[0].equalsIgnoreCase("reload")) {
    			if (!sender.hasPermission("clientstats.cmd.reload") && !sender.hasPermission("clientstats.admin")) {
    				plugin.sendMessage(sender, "error.permission");
    				return true;
    			}

    			plugin.saveDefaultConfig();
    			plugin.getConfig().options().copyDefaults(true);
    			plugin.reloadConfig();
    			plugin.sendMessage(sender, "commands.reload");

    			return true;

    		}
    	} else if (args.length == 2) {
    		if (args[0].equalsIgnoreCase("player")) {
    			if (!sender.hasPermission("clientstats.cmd.player") && !sender.hasPermission("clientstats.admin")) {
    				plugin.sendMessage(sender, "error.permission");
    				return true;
    			}

    			Player player = Bukkit.getPlayer(args[1]);
    			if (player == null) {
        			plugin.sendMessage(sender, "commands.player.not-found", args[1]);
    			} else {
	    			String version = plugin.getVersion(player);
	    			if (version == null) {
	        			plugin.sendMessage(sender, "error.general");
	    			} else {
	        			plugin.sendMessage(sender, "commands.player.other", player.getName(), version);
	    			}
    			}

    			return true;

    		}
    	}

    	if (sender.hasPermission("clientstats.help")) {

	    	List<String> messageList = plugin.getConfig().getStringList("messages.commands.help");

	    	String helpLabel = messageList.remove(0);
	    	messageList.add(0, Main.PREFIX + helpLabel);

	    	for (String message : messageList) {
	        	sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	        }

    	} else {

    		plugin.sendMessage(sender, "error.permission");

    	}

       	return true;
    }

}