package fr.onecraft.clientstats.bukkit.dispatcher;

import fr.onecraft.clientstats.ClientStatsAPI;
import fr.onecraft.clientstats.bukkit.user.BukkitUser;
import fr.onecraft.clientstats.common.core.CommandHandler;
import fr.onecraft.core.command.CommandUser;
import fr.onecraft.core.command.TabCommandRegister;
import org.bukkit.command.Command;

import java.util.List;

public class CommandDispatcher extends TabCommandRegister {

    private final CommandHandler handler;

    public CommandDispatcher(ClientStatsAPI plugin) {
        this.handler = new CommandHandler(plugin);
    }

    @Override
    protected void execute(CommandUser user, List<String> args, Command command, String alias) {
        handler.execute(BukkitUser.of(user.getCommandSender()), args, alias);
    }

    @Override
    public List<String> complete(CommandUser user, List<String> args, String token, Command cmd, String label) {
        return handler.complete(BukkitUser.of(user.getCommandSender()), args, token);
    }

}