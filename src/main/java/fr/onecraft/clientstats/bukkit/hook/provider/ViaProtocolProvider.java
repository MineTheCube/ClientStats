package fr.onecraft.clientstats.bukkit.hook.provider;

import fr.onecraft.clientstats.bukkit.hook.base.AbstractProvider;
import fr.onecraft.core.helpers.Players;
import org.bukkit.entity.Player;
import protocolsupport.api.ProtocolSupportAPI;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.ViaAPI;

import java.util.UUID;

public class ViaProtocolProvider extends AbstractProvider {

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
                return viaVersion.getPlayerVersion(player);
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
