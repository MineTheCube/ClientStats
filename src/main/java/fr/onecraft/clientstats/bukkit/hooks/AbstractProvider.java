package fr.onecraft.clientstats.bukkit.hooks;

import org.bukkit.entity.Player;

import fr.onecraft.core.plugin.Core;

public abstract class AbstractProvider {

	public AbstractProvider() {
		if (getProviderName() != null) {
            Core.info("Hooked into " + getProviderName() + " !");
        }
	}

	public abstract String getProviderName();
	
	public abstract int getProtocol(Player p);
	
}
