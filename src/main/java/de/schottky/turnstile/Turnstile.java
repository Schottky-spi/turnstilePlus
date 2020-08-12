package de.schottky.turnstile;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Optional;

/**
 * Represents a structure in the world that acts as a
 * turnstile
 */

public interface Turnstile {

    void onPlayerTraverse(Player player, Location from, Location to);

    boolean requestActivation(Player player);

    Status currentStatus();

    Collection<TurnstilePart> allParts();

    void setOpen(boolean open);

    enum Status {
        OPEN(true),
        CLOSED(false);

        public final boolean isOpen;

        Status(boolean isOpen) {
            this.isOpen = isOpen;
        }
    }

    static Optional<Turnstile> byName(String name) {
        // TODO
        return Optional.empty();
    }
}
