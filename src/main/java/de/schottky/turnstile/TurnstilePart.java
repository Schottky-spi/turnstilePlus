package de.schottky.turnstile;

import org.bukkit.Location;

/**
 * represents a part of a turnstile
 */
public interface TurnstilePart {

    /**
     * true, if this part contains a certain location
     * @param location The location that this part may contain
     * @return true, if this part contains a certain location, false otherwise
     */
    boolean containsLocation(Location location);

    /**
     * true, if this part should block (be closed), false
     * otherwise
     * @param block true, if this part should block, false otherwise
     */

    void setBlocking(boolean block);

    default void initAfterLoad() {}
}
