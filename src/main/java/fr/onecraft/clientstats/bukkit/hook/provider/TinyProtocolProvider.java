package fr.onecraft.clientstats.bukkit.hook.provider;

import com.comphenix.tinyprotocol.Reflection;
import com.comphenix.tinyprotocol.TinyProtocol;
import fr.onecraft.clientstats.bukkit.hook.base.AbstractPacketHandler;
import fr.onecraft.core.plugin.Core;
import io.netty.channel.Channel;
import org.bukkit.entity.Player;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class TinyProtocolProvider extends AbstractPacketHandler {

    // Handshake packets
    private final Class<?> HANDSHAKE = Reflection.getClass("{nms}.PacketHandshakingInSetProtocol");
    private final Class<Object> ENUM_PROTOCOL = Reflection.getUntypedClass("{nms}.EnumProtocol");
    private final Reflection.FieldAccessor<Integer> PROTOCOL_VERSION = Reflection.getField(HANDSHAKE, int.class, 0);
    private final Reflection.FieldAccessor<Object> NEXT_STATE = Reflection.getField(HANDSHAKE, ENUM_PROTOCOL, 0);
    private final String HANDSHAKE_LOGIN = "LOGIN";

    // Disconnect while login
    private final Class<?> DISCONNECT = Reflection.getClass("{nms}.PacketLoginOutDisconnect");

    @Override
    public String getProviderName() {
        return "TinyProtocol";
    }

    @Override
    public void registerPacketListener() {
        new TinyProtocol(Core.plugin()) {

            @Override
            public Object onPacketInAsync(Player sender, Channel channel, Object packet) {

                if (HANDSHAKE.isInstance(packet)) {
                    if (PROTOCOL_VERSION.hasField(packet) && NEXT_STATE.hasField(packet)) {
                        Object nextState = NEXT_STATE.get(packet);
                        if (nextState != null && HANDSHAKE_LOGIN.equalsIgnoreCase(nextState.toString())) {
                            Integer protocolVersion = PROTOCOL_VERSION.get(packet);
                            if (protocolVersion != null) {
                                SocketAddress address = channel.remoteAddress();
                                if (address instanceof InetSocketAddress) {
                                    add((InetSocketAddress) address, protocolVersion);
                                }
                            }
                        }
                    }
                }

                return super.onPacketInAsync(sender, channel, packet);
            }

            @Override
            public Object onPacketOutAsync(Player receiver, Channel channel, Object packet) {

                if (DISCONNECT.isInstance(packet)) {
                    SocketAddress address = channel.remoteAddress();
                    if (address instanceof InetSocketAddress) {
                        remove((InetSocketAddress) address);
                    }
                }

                return super.onPacketOutAsync(receiver, channel, packet);
            }

        };
    }

}
