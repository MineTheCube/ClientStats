package fr.onecraft.clientstats.bukkit.hook.provider;

import java.util.UUID;

import org.bukkit.entity.Player;

import fr.onecraft.clientstats.bukkit.hook.base.AbstractProvider;
import fr.onecraft.core.helpers.Players;
import protocolsupport.api.ProtocolSupportAPI;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.ViaAPI;

public class ViaProtocolProvider extends AbstractProvider {

    @SuppressWarnings("rawtypes")
    private final ViaAPI viaVersion = Via.getAPI();

    @Override
    public String getProviderName() {
	return "ViaVersion + ProtocolSupport";
    }

    @Override
    public int getProtocol(UUID player) {
	Player p = Players.get(player);

	if (p != null) {
	    int version = ProtocolSupportAPI.getProtocolVersion(p).getId();
	    if (version == -1) {
		return this.viaVersion.getPlayerVersion(player);
	    }
	    return version;
	}

	return 0;
    }

    @Override
    public int getProtocol(Player p) {
	throw new AbstractMethodError("Impossible call to getProtocol(Player)");
    }

}
