package fr.onecraft.clientstats.bukkit.hook.detector;

import org.bukkit.Bukkit;

import fr.onecraft.clientstats.bukkit.hook.provider.ViaVersionProvider;
import fr.onecraft.clientstats.common.base.VersionProvider;

public class ViaVersionDetector {

    public static boolean isUsable() {
	if (Bukkit.getPluginManager().isPluginEnabled("ViaVersion")) {
	    try {
		// ViaVersion 1.0.0 and up
		Class.forName("us.myles.ViaVersion.api.Via");
		return true;
	    } catch (ClassNotFoundException ignored) {
	    }
	}
	return false;
    }

    public static VersionProvider getProvider() {
	return new ViaVersionProvider();
    }

}
