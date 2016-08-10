package fr.onecraft.clientstats.bukkit.hooks;

import fr.onecraft.core.plugin.Core;

public abstract class AbstractProvider implements Provider {

    public AbstractProvider() {
        if (getProviderName() != null) {
            Core.info("Hooked into " + getProviderName() + " !");
        }
    }

}
