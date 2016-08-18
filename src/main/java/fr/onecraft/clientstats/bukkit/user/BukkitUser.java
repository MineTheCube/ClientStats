package fr.onecraft.clientstats.bukkit.user;

import fr.onecraft.clientstats.common.user.MixedUser;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BukkitUser extends MixedUser {

    public static MixedUser of(CommandSender sender) {
        return sender == null ? null : new BukkitUser(sender);
    }

    private BukkitUser(CommandSender sender) {
        this.sender = sender;
    }

    private final CommandSender sender;

    @Override
    public void sendMessage(String msg) {
        sender.sendMessage(msg);
    }

    @Override
    public boolean hasPermission(String permission) {
        return sender.hasPermission(permission);
    }

    @Override
    public boolean isPlayer() {
        return sender instanceof Player;
    }

    @Override
    public UUID getUniqueId() {
        return sender instanceof Player ? ((Player) sender).getUniqueId() : null;
    }

    @Override
    public String getName() {
        return sender instanceof Player ? sender.getName() : null;
    }

}
