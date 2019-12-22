package fr.onecraft.clientstats.bukkit.hook.provider;

import org.bukkit.entity.Player;

import fr.onecraft.clientstats.bukkit.hook.base.AbstractProvider;
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
