package fr.onecraft.clientstats.bungee.hooks;

import java.util.UUID;

import fr.onecraft.clientstats.common.base.VersionProvider;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.ViaAPI;

public class ViaVersionProvider implements VersionProvider {

    private final ViaAPI viaVersion = Via.getAPI();

    @Override
    public String getProviderName() {
	return "ViaVersion";
    }

    @Override
    public int getProtocol(UUID player) {
	return this.viaVersion.getPlayerVersion(player);
    }

}
