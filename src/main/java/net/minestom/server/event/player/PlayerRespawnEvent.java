package net.minestom.server.event.player;

import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.utils.Position;
import org.jetbrains.annotations.NotNull;

/**
 * Called when {@link Player#respawn()} is executed (for custom respawn or as a result of
 * {@link net.minestom.server.network.packet.client.play.ClientStatusPacket}
 */
public class PlayerRespawnEvent implements PlayerEvent {

    private final Player player;
    private Position respawnPosition;

    public PlayerRespawnEvent(@NotNull Player player) {
        this.player = player;
        this.respawnPosition = player.getRespawnPoint();
    }

    /**
     * Gets the respawn position.
     * <p>
     * Is by default {@link Player#getRespawnPoint()}
     *
     * @return the respawn position
     */
    @NotNull
    public Position getRespawnPosition() {
        return respawnPosition;
    }

    /**
     * Changes the respawn position.
     *
     * @param respawnPosition the new respawn position
     */
    public void setRespawnPosition(@NotNull Position respawnPosition) {
        this.respawnPosition = respawnPosition;
    }

    @Override
    public @NotNull Player getPlayer() {
        return player;
    }
}
