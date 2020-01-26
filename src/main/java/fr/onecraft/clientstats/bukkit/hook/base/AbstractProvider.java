package fr.onecraft.clientstats.bukkit.hook.base;

import java.util.UUID;

import org.bukkit.entity.Player;

import fr.onecraft.clientstats.common.base.VersionProvider;
import fr.onecraft.core.helpers.Players;
import fr.onecraft.core.plugin.Core;

public abstract class AbstractProvider implements VersionProvider {

    public AbstractProvider() {
	if (this.getProviderName() != null) {
	    Core.info("Hooked into " + this.getProviderName() + "!");
	}
    }

    @Override
    public int getProtocol(UUID player) {
	Player p = Players.get(player);
	return p != null ? this.getProtocol(p) : 0;
    }

    abstract public int getProtocol(Player p);

}
