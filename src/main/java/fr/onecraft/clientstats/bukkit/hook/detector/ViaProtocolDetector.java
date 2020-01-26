package fr.onecraft.clientstats.bukkit.hook.detector;

import org.bukkit.Bukkit;

import fr.onecraft.clientstats.bukkit.hook.provider.ViaProtocolProvider;
import fr.onecraft.clientstats.common.base.VersionProvider;
import fr.onecraft.core.plugin.Core;

public class ViaProtocolDetector {

    public static boolean isUsable() {
	if (Bukkit.getPluginManager().isPluginEnabled("ViaVersion")
		&& Bukkit.getPluginManager().isPluginEnabled("ProtocolSupport")) {

	    try {
		// ViaVersion 1.0.0 and up
		Class.forName("us.myles.ViaVersion.api.Via");
		return true;
	    } catch (ClassNotFoundException ignored) {
		Core.warning("Please update ViaVersion for better version detection!");
	    }
	}
	return false;
    }

    public static VersionProvider getProvider() {
	return new ViaProtocolProvider();
    }

}
