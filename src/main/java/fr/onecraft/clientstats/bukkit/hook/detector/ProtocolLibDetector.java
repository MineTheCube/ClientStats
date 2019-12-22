package fr.onecraft.clientstats.bukkit.hook.detector;

import org.bukkit.Bukkit;

import fr.onecraft.clientstats.bukkit.hook.provider.ProtocolLibProvider;
import fr.onecraft.clientstats.common.base.VersionProvider;

public class ProtocolLibDetector {

    public static boolean isUsable() {
	return Bukkit.getPluginManager().isPluginEnabled("ProtocolLib");
    }

    public static VersionProvider getProvider() {
	return new ProtocolLibProvider();
    }

}
