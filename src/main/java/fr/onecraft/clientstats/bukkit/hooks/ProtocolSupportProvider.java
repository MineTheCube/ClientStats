package fr.onecraft.clientstats.bukkit.hooks;

import org.bukkit.entity.Player;
import protocolsupport.api.ProtocolSupportAPI;

public class ProtocolSupportProvider extends AbstractProvider {

	@Override
	public String getProviderName() {
		return "ProtocolSupport";
	}

	@Override
	public int getProtocol(Player p) {
		return ProtocolSupportAPI.getProtocolVersion(p).getId();
	}

}
