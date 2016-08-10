package fr.onecraft.clientstats.bukkit.hooks;

import org.bukkit.entity.Player;

public interface Provider {

    String getProviderName();

    int getProtocol(Player p);

}
