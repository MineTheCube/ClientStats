package fr.onecraft.clientstats.bukkit.hooks;

import fr.onecraft.clientstats.common.base.VersionProvider;
import org.bukkit.Bukkit;

public class ProtocolLibDetector {

    public static boolean isUsable() {
        return Bukkit.getPluginManager().isPluginEnabled("ProtocolLib");
    }

    public static VersionProvider getProvider() {
        return new ProtocolLibProvider();
    }

}
