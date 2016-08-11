package fr.onecraft.clientstats.bukkit.hooks;

import com.comphenix.tinyprotocol.Reflection;

public class TinyProtocolDetector {

    public static boolean isUsable() {

        try {
            // Netty update
            Class.forName("io.netty.channel.ChannelInitializer");
        } catch (ClassNotFoundException e) {
            return false;
        }

        try {

            // Handshake packets
            Class<?> handshake = Reflection.getClass("{nms}.PacketHandshakingInSetProtocol");
            Class<Object> ENUM_PROTOCOL = Reflection.getUntypedClass("{nms}.EnumProtocol");
            Reflection.getField(handshake, int.class, 0);
            Reflection.getField(handshake, ENUM_PROTOCOL, 0);

            // Disconnect while login
            Reflection.getClass("{nms}.PacketLoginOutDisconnect");

            // We need to have LOGIN
            for (Object value : ENUM_PROTOCOL.getEnumConstants()) {
                if ("LOGIN".equalsIgnoreCase(value.toString())) {
                    return true;
                }
            }

        } catch (Exception ignored) {}

        return false;
    }

    public static TinyProtocolProvider getProvider() {
        return new TinyProtocolProvider();
    }

}
