package fr.onecraft.clientstats.bungee.hooks;

import fr.onecraft.clientstats.common.base.VersionProvider;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.ViaAPI;

import java.util.UUID;

public class ViaVersionProvider implements VersionProvider {

    private final ViaAPI viaVersion = Via.getAPI();

    @Override
    public String getProviderName() {
        return "ViaVersion";
    }

    @Override
    public int getProtocol(UUID player) {
        return viaVersion.getPlayerVersion(player);
    }

}
