package fr.onecraft.clientstats.bukkit.hooks;

import org.bukkit.entity.Player;
import us.myles.ViaVersion.api.ViaVersion;
import us.myles.ViaVersion.api.ViaVersionAPI;

public class ViaVersionLegacyProvider extends AbstractProvider {

    private final ViaVersionAPI viaVersion = ViaVersion.getInstance();

    @Override
    public String getProviderName() {
        return "ViaVersion Legacy";
    }

    @Override
    public int getProtocol(Player p) {
        return viaVersion.getPlayerVersion(p);
    }

}
