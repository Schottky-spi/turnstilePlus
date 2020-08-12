package de.schottky.turnstile;

import org.bukkit.Location;

public interface TurnstilePart {

    Turnstile getTurnstile();

    boolean isContainedIn(Location location);

    void setBlocking(boolean active);
}
