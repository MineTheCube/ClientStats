package fr.onecraft.clientstats.common.user;

import java.util.Collection;
import java.util.UUID;

public abstract class MixedUser {

    private static UserProvider provider;

    public static void setProvider(UserProvider provider) {
        MixedUser.provider = provider;
    }

    public static Collection<UUID> getOnlineUsers() {
        return provider.getOnlineUsers();
    }

    public static int getOnlineCount() {
        return provider.getOnlineCount();
    }

    public static MixedUser getUser(String name) {
        return provider.getUser(name);
    }

    public abstract void sendMessage(String msg);

    public abstract boolean hasPermission(String permission);

    public abstract boolean isPlayer();

    public abstract UUID getUniqueId();

    public abstract String getName();

}
