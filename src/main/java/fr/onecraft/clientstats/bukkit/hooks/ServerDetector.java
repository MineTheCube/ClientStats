package fr.onecraft.clientstats.bukkit.hooks;

import fr.onecraft.core.utils.BukkitUtils;

import java.lang.reflect.Method;

public class ServerDetector {

    public static boolean isUsable() {
        try {
            Class<?> networkManager = Class.forName(BukkitUtils.getNmsPrefixOf("NetworkManager"));
            Method method = networkManager.getMethod("getVersion");
            return method.getReturnType().isAssignableFrom(int.class) || method.getReturnType().isAssignableFrom(Integer.class);
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
            return false;
        }
    }

    public static AbstractProvider getProvider() {
        return new ServerProvider();
    }
}
