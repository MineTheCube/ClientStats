package fr.onecraft.clientstats.bukkit.hooks;

import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ServerProvider extends AbstractProvider {

    @Override
    public String getProviderName() {
        return "Protocol Hack Server";
    }

    @Override
    public int getProtocol(Player p) {
        try {
            Method getHandle = p.getClass().getMethod("getHandle");
            Object nmsPlayer = getHandle.invoke(p);

            Field fieldPlayerConnection = nmsPlayer.getClass().getField("playerConnection");
            Object playerConnection = fieldPlayerConnection.get(nmsPlayer);

            Field fieldNetworkManager = playerConnection.getClass().getField("networkManager");
            Object networkManager = fieldNetworkManager.get(playerConnection);

            Method getVersion = networkManager.getClass().getMethod("getVersion");
            Object value = getVersion.invoke(networkManager);

            Integer version = (Integer) value;

            if (version != null) {
                return version;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return 0;
    }

}
