package fr.onecraft.clientstats.bukkit.hook.detector;

import fr.onecraft.clientstats.bukkit.hook.provider.ViaVersionLegacyProvider;
import fr.onecraft.clientstats.common.base.VersionProvider;
import org.bukkit.Bukkit;

public class ViaVersionLegacyDetector {

    public static boolean isUsable() {
        if (Bukkit.getPluginManager().isPluginEnabled("ViaVersion")) {
            try {
                // ViaVersion 0.9.9 and below
                Class.forName("us.myles.ViaVersion.api.ViaVersion");
                return true;
            } catch (ClassNotFoundException ignored) {}
        }
        return false;
    }

    public static VersionProvider getProvider() {
        return new ViaVersionLegacyProvider();
    }

}
