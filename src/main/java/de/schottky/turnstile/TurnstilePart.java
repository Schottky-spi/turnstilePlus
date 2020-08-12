package de.schottky.turnstile;

import org.bukkit.Chunk;
import org.bukkit.Location;

public interface TurnstilePart {

    Turnstile getTurnstile();

    boolean isContainedIn(Location location);
}
