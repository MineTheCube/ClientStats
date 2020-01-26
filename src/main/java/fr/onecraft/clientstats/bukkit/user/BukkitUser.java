package fr.onecraft.clientstats.bukkit.user;

import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.onecraft.clientstats.common.user.MixedUser;

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
	this.sender.sendMessage(msg);
    }

    @Override
    public boolean hasPermission(String permission) {
	return this.sender.hasPermission(permission);
    }

    @Override
    public boolean isPlayer() {
	return this.sender instanceof Player;
    }

    @Override
    public UUID getUniqueId() {
	return this.sender instanceof Player ? ((Player) this.sender).getUniqueId() : null;
    }

    @Override
    public String getName() {
	return this.sender instanceof Player ? this.sender.getName() : null;
    }

}
