package de.schottky.turnstile;

import org.bukkit.Location;

public interface TurnstilePart {

    boolean containsLocation(Location location);

    void setBlocking(boolean active);
}
