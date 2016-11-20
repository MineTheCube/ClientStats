package fr.onecraft.clientstats.bukkit.hook.provider;

import fr.onecraft.clientstats.bukkit.hook.base.AbstractProvider;
import org.bukkit.entity.Player;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.ViaAPI;

import java.util.UUID;

public class ViaVersionProvider extends AbstractProvider {

    private final ViaAPI viaVersion = Via.getAPI();

    @Override
    public String getProviderName() {
        return "ViaVersion";
    }

    @Override
    public int getProtocol(UUID player) {
        return viaVersion.getPlayerVersion(player);
    }

    @Override
    public int getProtocol(Player p) {
        throw new AbstractMethodError("Impossible call to getProtocol(Player)");
    }

}
