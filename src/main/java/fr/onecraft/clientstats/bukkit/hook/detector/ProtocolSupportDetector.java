package fr.onecraft.clientstats.bukkit.hook.detector;

import org.bukkit.Bukkit;

import fr.onecraft.clientstats.bukkit.hook.provider.ProtocolSupportProvider;
import fr.onecraft.clientstats.common.base.VersionProvider;

public class ProtocolSupportDetector {

    public static boolean isUsable() {
	return Bukkit.getPluginManager().isPluginEnabled("ProtocolSupport");
    }

    public static VersionProvider getProvider() {
	return new ProtocolSupportProvider();
    }

}
