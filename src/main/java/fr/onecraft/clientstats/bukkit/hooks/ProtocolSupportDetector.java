package fr.onecraft.clientstats.bukkit.hooks;

import org.bukkit.Bukkit;

public class ProtocolSupportDetector {

	public static boolean isUsable() {
		return Bukkit.getPluginManager().isPluginEnabled("ProtocolSupport");
	}

	public static AbstractProvider getProvider() {
		return new ProtocolSupportProvider();
	}
	
}
