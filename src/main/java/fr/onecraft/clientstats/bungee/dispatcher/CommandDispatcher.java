package fr.onecraft.clientstats.bungee.dispatcher;

import fr.onecraft.clientstats.ClientStatsAPI;
import fr.onecraft.clientstats.bungee.user.BungeeUser;
import fr.onecraft.clientstats.common.core.CommandHandler;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.util.Arrays;

public class CommandDispatcher extends Command {

    private final CommandHandler handler;

    public CommandDispatcher(ClientStatsAPI plugin) {
        super("clientstats", null, "cstats", "cs", "bclientstats", "bcstats", "bcs");
        this.handler = new CommandHandler(plugin);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        handler.execute(BungeeUser.of(sender), Arrays.asList(args), "cstats");
    }

}