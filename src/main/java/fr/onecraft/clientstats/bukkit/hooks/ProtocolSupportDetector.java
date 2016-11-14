package fr.onecraft.clientstats.bukkit.hooks;

import fr.onecraft.clientstats.common.base.VersionProvider;
import org.bukkit.Bukkit;

public class ProtocolSupportDetector {

    public static boolean isUsable() {
        return Bukkit.getPluginManager().isPluginEnabled("ProtocolSupport");
    }

    public static VersionProvider getProvider() {
        return new ProtocolSupportProvider();
    }

}
