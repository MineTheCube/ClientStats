package fr.onecraft.clientstats.bukkit.hooks;

import org.bukkit.Bukkit;

public class ViaVersionDetector {

	public static boolean isUsable() {
		return Bukkit.getPluginManager().isPluginEnabled("ViaVersion");
	}

	public static AbstractProvider getProvider() {
		return new ViaVersionProvider();
	}

}
