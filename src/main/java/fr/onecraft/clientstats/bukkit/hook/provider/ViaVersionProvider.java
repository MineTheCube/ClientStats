package fr.onecraft.clientstats.bukkit.hook.provider;

import java.util.UUID;

import org.bukkit.entity.Player;

import fr.onecraft.clientstats.bukkit.hook.base.AbstractProvider;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.ViaAPI;

public class ViaVersionProvider extends AbstractProvider {

    private final ViaAPI viaVersion = Via.getAPI();

    @Override
    public String getProviderName() {
	return "ViaVersion";
    }

    @Override
    public int getProtocol(UUID player) {
	return this.viaVersion.getPlayerVersion(player);
    }

    @Override
    public int getProtocol(Player p) {
	throw new AbstractMethodError("Impossible call to getProtocol(Player)");
    }

}
