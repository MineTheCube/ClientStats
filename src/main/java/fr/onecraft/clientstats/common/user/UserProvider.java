package fr.onecraft.clientstats.common.user;

import java.util.Collection;
import java.util.UUID;

public interface UserProvider {

    Collection<UUID> getOnlineUsers();

    int getOnlineCount();

    MixedUser getUser(String name);

}
