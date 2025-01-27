package net.minestom.server.network;

import net.minestom.server.entity.Player;
import net.minestom.server.listener.manager.PacketListenerManager;
import net.minestom.server.network.packet.client.ClientPacket;
import net.minestom.server.network.packet.client.ClientPacketsHandler;
import net.minestom.server.network.packet.client.handshake.ClientHandshakePacket;
import net.minestom.server.network.packet.client.play.ClientPlayerPositionAndRotationPacket;
import net.minestom.server.network.packet.client.play.ClientPlayerPositionPacket;
import net.minestom.server.network.packet.client.play.ClientPlayerRotationPacket;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

/**
 * Responsible for processing client packets.
 * <p>
 * You can retrieve the different packet handlers per state (status/login/play)
 * from the {@link ClientPacketsHandler} classes.
 */
public class PacketProcessor {
    private final ClientPacketsHandler statusHandler;
    private final ClientPacketsHandler loginHandler;
    private final ClientPacketsHandler configurationHandler;
    private final ClientPacketsHandler playHandler;

    private final PacketListenerManager packetListenerManager;

    public PacketProcessor(@NotNull PacketListenerManager packetListenerManager) {
        statusHandler = new ClientPacketsHandler.Status();
        loginHandler = new ClientPacketsHandler.Login();
        configurationHandler = new ClientPacketsHandler.Configuration();
        playHandler = new ClientPacketsHandler.Play();

        this.packetListenerManager = packetListenerManager;
    }

    public @NotNull ClientPacket create(@NotNull ConnectionState connectionState, int packetId, ByteBuffer body) {
        NetworkBuffer buffer = new NetworkBuffer(body);
        final ClientPacket clientPacket = switch (connectionState) {
            case HANDSHAKE -> {
                assert packetId == 0;
                yield new ClientHandshakePacket(buffer);
            }
            case STATUS -> statusHandler.create(packetId, buffer);
            case LOGIN -> loginHandler.create(packetId, buffer);
            case CONFIGURATION -> configurationHandler.create(packetId, buffer);
            case PLAY -> playHandler.create(packetId, buffer);
        };
        body.position(buffer.readIndex());
        return clientPacket;
    }

    public ClientPacket process(@NotNull PlayerConnection connection, int packetId, ByteBuffer body) {
        final ClientPacket packet = create(connection.getClientState(), packetId, body);
        var packetClass = packet.getClass();
        if (packetClass != ClientPlayerPositionPacket.class && packetClass != ClientPlayerPositionAndRotationPacket.class && packetClass != ClientPlayerRotationPacket.class)
            System.out.println("PROCESS " + connection.getClientState() + " " + packet.getClass().getSimpleName());

        final ConnectionState state = connection.getClientState();
        switch (state) {
            case HANDSHAKE, STATUS, LOGIN, CONFIGURATION -> packetListenerManager.processClientPacket(connection.getClientState(), packet, connection);
            case PLAY -> {
                final Player player = connection.getPlayer();
                assert player != null;
                player.addPacketToQueue(packet);
            }
        }
        return packet;
    }
}
