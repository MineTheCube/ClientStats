package fr.onecraft.clientstats.bukkit.hooks;

import fr.onecraft.core.helper.Players;
import org.bukkit.entity.Player;
import protocolsupport.api.ProtocolSupportAPI;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.ViaAPI;

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
