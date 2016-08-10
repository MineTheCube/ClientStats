package fr.onecraft.clientstats.bukkit.hooks;

import org.bukkit.Bukkit;

public class ProtocolLibDetector {

    public static boolean isUsable() {
        return Bukkit.getPluginManager().isPluginEnabled("ProtocolLib");
    }

    public static ProtocolLibProvider getProvider() {
        return new ProtocolLibProvider();
    }

}
