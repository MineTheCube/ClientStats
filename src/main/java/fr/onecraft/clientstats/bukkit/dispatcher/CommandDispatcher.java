package fr.onecraft.clientstats.bukkit.dispatcher;

import java.util.List;

import org.bukkit.command.Command;

import fr.onecraft.clientstats.ClientStatsAPI;
import fr.onecraft.clientstats.bukkit.user.BukkitUser;
import fr.onecraft.clientstats.common.core.CommandHandler;
import fr.onecraft.core.command.CommandUser;
import fr.onecraft.core.command.TabCommandRegister;

public class CommandDispatcher extends TabCommandRegister {

    private final CommandHandler handler;

    public CommandDispatcher(ClientStatsAPI plugin) {
	this.handler = new CommandHandler(plugin);
    }

    @Override
    protected void execute(CommandUser user, List<String> args, Command command, String alias) {
	this.handler.execute(BukkitUser.of(user.getSender()), args, alias);
    }

    @Override
    public List<String> complete(CommandUser user, List<String> args, String token, Command cmd, String label) {
	return this.handler.complete(BukkitUser.of(user.getSender()), args, token);
    }

}