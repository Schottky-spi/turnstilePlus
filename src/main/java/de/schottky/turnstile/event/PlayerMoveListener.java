package de.schottky.turnstile.event;

import de.schottky.turnstile.Turnstile;
import de.schottky.turnstile.TurnstilePart;
import de.schottky.turnstile.math.VectorToBlockFace;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerMoveListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getTo() == null || event.getFrom().equals(event.getTo())) return;
        if (openTurnstiles.isEmpty() && playersInTurnstile.isEmpty()) return;
        traverse(event.getFrom(), event.getTo(), event.getPlayer());
    }

    private static final Set<UUID> playersInTurnstile = new HashSet<>();

    public void onTurnstileChange(Turnstile turnstile) {
        if (turnstile.currentStatus() == Turnstile.Status.OPEN) {
            this.openTurnstiles.add(turnstile);
        } else {
            this.openTurnstiles.remove(turnstile);
        }
    }

    private final Set<Turnstile> openTurnstiles = new HashSet<>();

    /**
     * called when a player traverses from a location to another.
     * Neither may be null nor be the same.
     * @param from The location that the player came from
     * @param to The location that the player went to
     * @param player The player that traversed
     */

    public void traverse(Location from, Location to, Player player) {

        final BlockFace direction = VectorToBlockFace.convert(from.subtract(to).toVector());

        for (Turnstile turnstile : openTurnstiles) {
            for (TurnstilePart part : turnstile.allParts()) {
                if (part.containsLocation(to) && !playersInTurnstile.contains(player.getUniqueId())) {
                    turnstile.onPlayerEnter(player, direction);
                    playersInTurnstile.add(player.getUniqueId());
                } else if (!part.containsLocation(to) && playersInTurnstile.remove(player.getUniqueId())) {
                    turnstile.onPlayerLeave(player, direction.getOppositeFace());
                }
            }
        }
    }
}
