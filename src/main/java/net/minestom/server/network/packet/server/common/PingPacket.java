package net.minestom.server.network.packet.server.common;

import net.minestom.server.network.ConnectionState;
import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.network.packet.server.ServerPacket;
import net.minestom.server.network.packet.server.ServerPacketIdentifier;
import org.jetbrains.annotations.NotNull;

import static net.minestom.server.network.NetworkBuffer.INT;

public record PingPacket(int id) implements ServerPacket {
    public PingPacket(@NotNull NetworkBuffer reader) {
        this(reader.read(INT));
    }

    @Override
    public void write(@NotNull NetworkBuffer writer) {
        writer.write(INT, id);
    }

    @Override
    public int getId(@NotNull ConnectionState state) {
        return state == ConnectionState.PLAY ? ServerPacketIdentifier.PING : ServerPacketIdentifier.CONFIGURATION_PING;
    }
}
