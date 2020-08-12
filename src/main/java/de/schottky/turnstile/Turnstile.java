package de.schottky.turnstile;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Represents a structure in the world that acts as a
 * turnstile
 */

public interface Turnstile {

    void onPlayerTraverse(Player player, Location from, Location to);

    boolean requestActivation(Player player);

    Status currentStatus();

    Collection<TurnstilePart> allParts();

    enum Status {
        OPEN,
        CLOSED
    }
}
