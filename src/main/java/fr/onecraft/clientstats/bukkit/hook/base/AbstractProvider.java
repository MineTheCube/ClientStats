package fr.onecraft.clientstats.bukkit.hook.base;

import fr.onecraft.clientstats.common.base.VersionProvider;
import fr.onecraft.core.helper.Players;
import fr.onecraft.core.plugin.Core;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class AbstractProvider implements VersionProvider {

    public AbstractProvider() {
        if (getProviderName() != null) {
            Core.info("Hooked into " + getProviderName() + "!");
        }
    }

    @Override
    public int getProtocol(UUID player) {
        Player p = Players.get(player);
        return p != null ? getProtocol(p) : 0;
    }

    abstract public int getProtocol(Player p);

}
