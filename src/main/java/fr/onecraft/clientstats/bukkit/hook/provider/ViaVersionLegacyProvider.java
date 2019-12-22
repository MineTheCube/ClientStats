package fr.onecraft.clientstats.bukkit.hook.provider;

import org.bukkit.entity.Player;

import fr.onecraft.clientstats.bukkit.hook.base.AbstractProvider;
import us.myles.ViaVersion.api.ViaVersion;
import us.myles.ViaVersion.api.ViaVersionAPI;

public class ViaVersionLegacyProvider extends AbstractProvider {

    @SuppressWarnings("deprecation")
    private final ViaVersionAPI viaVersion = ViaVersion.getInstance();

    @Override
    public String getProviderName() {
	return "ViaVersion Legacy";
    }

    @Override
    public int getProtocol(Player p) {
	return this.viaVersion.getPlayerVersion(p);
    }

}
