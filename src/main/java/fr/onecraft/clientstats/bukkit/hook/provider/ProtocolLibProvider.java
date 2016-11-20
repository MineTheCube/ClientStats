package fr.onecraft.clientstats.bukkit.hook.provider;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import fr.onecraft.clientstats.bukkit.hook.base.AbstractPacketHandler;
import fr.onecraft.core.plugin.Core;

public class ProtocolLibProvider extends AbstractPacketHandler {

    @Override
    public String getProviderName() {
        return "ProtocolLib";
    }

    @Override
    public void registerPacketListener() {

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Core.plugin(),
                ListenerPriority.NORMAL,
                PacketType.Handshake.Client.SET_PROTOCOL, PacketType.Login.Server.DISCONNECT) {

            @Override
            public void onPacketReceiving(PacketEvent event) {

                if (event.getPacketType() == PacketType.Handshake.Client.SET_PROTOCOL) {
                    // Receiving handshake packet

                    if (event.getPacket().getProtocols().read(0) == PacketType.Protocol.LOGIN) {
                        // Intent to login
                        add(event.getPlayer().getAddress(), event.getPacket().getIntegers().read(0));
                    }

                }
            }

            @Override
            public void onPacketSending(PacketEvent event) {
                if (event.getPacketType() == PacketType.Login.Server.DISCONNECT) {
                    // Server kick player or deny login
                    remove(event.getPlayer().getAddress());
                }
            }
        });

    }

}
