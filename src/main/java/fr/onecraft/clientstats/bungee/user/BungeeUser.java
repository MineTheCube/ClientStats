package fr.onecraft.clientstats.bungee.user;

import fr.onecraft.clientstats.common.user.MixedUser;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

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
        sender.sendMessage(TextComponent.fromLegacyText(msg));
    }

    @Override
    public boolean hasPermission(String permission) {
        return sender.hasPermission(permission);
    }

    @Override
    public boolean isPlayer() {
        return sender instanceof ProxiedPlayer;
    }

    @Override
    public UUID getUniqueId() {
        return sender instanceof ProxiedPlayer ? ((ProxiedPlayer) sender).getUniqueId() : null;
    }

    @Override
    public String getName() {
        return sender instanceof ProxiedPlayer ? sender.getName() : null;
    }

}
