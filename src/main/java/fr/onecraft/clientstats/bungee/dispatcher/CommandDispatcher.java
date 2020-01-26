package fr.onecraft.clientstats.bungee.dispatcher;

import java.util.Arrays;
import java.util.List;

import fr.onecraft.clientstats.ClientStatsAPI;
import fr.onecraft.clientstats.bungee.user.BungeeUser;
import fr.onecraft.clientstats.common.core.CommandHandler;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class CommandDispatcher extends Command implements TabExecutor {

    private final CommandHandler handler;

    public CommandDispatcher(ClientStatsAPI plugin) {
	super("clientstats", null, "cstats", "cs", "bclientstats", "bcstats", "bcs");
	this.handler = new CommandHandler(plugin);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
	this.handler.execute(BungeeUser.of(sender), Arrays.asList(args), "cstats");
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
	String token;
	List<String> list;
	if (args.length == 0) {
	    list = Arrays.asList(args);
	    token = null;
	} else {
	    list = Arrays.asList(Arrays.copyOf(args, args.length - 1));
	    token = args[args.length - 1];
	}
	return this.handler.complete(BungeeUser.of(sender), list, token);
    }
}