package fr.onecraft.clientstats.bukkit.hook.detector;

import fr.onecraft.clientstats.bukkit.hook.provider.ViaVersionProvider;
import fr.onecraft.clientstats.common.base.VersionProvider;
import org.bukkit.Bukkit;

public class ViaVersionDetector {

    public static boolean isUsable() {
        if (Bukkit.getPluginManager().isPluginEnabled("ViaVersion")) {
            try {
                // ViaVersion 1.0.0 and up
                Class.forName("us.myles.ViaVersion.api.Via");
                return true;
            } catch (ClassNotFoundException ignored) {}
        }
        return false;
    }

    public static VersionProvider getProvider() {
        return new ViaVersionProvider();
    }

}
