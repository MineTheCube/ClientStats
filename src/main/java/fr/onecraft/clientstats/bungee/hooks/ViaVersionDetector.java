package fr.onecraft.clientstats.bungee.hooks;

import fr.onecraft.clientstats.common.base.VersionProvider;
import net.md_5.bungee.api.ProxyServer;

public class ViaVersionDetector {

    public static boolean isUsable() {
	return ProxyServer.getInstance().getPluginManager().getPlugin("ViaVersion") != null;
    }

    public static VersionProvider getProvider() {
	return new ViaVersionProvider();
    }

}
