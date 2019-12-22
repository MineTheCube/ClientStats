package fr.onecraft.clientstats.bungee.user;

import java.util.UUID;

import fr.onecraft.clientstats.common.user.MixedUser;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeUser extends MixedUser {

    public static MixedUser of(CommandSender sender) {
	return sender == null ? null : new BungeeUser(sender);
    }

    private BungeeUser(CommandSender sender) {
	this.sender = sender;
    }

    private final CommandSender sender;

    @Override
    public void sendMessage(String msg) {
	this.sender.sendMessage(TextComponent.fromLegacyText(msg));
    }

    @Override
    public boolean hasPermission(String permission) {
	return this.sender.hasPermission(permission);
    }

    @Override
    public boolean isPlayer() {
	return this.sender instanceof ProxiedPlayer;
    }

    @Override
    public UUID getUniqueId() {
	return this.sender instanceof ProxiedPlayer ? ((ProxiedPlayer) this.sender).getUniqueId() : null;
    }

    @Override
    public String getName() {
	return this.sender instanceof ProxiedPlayer ? this.sender.getName() : null;
    }

}
