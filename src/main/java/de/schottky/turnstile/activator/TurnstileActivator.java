package de.schottky.turnstile.activator;

import de.schottky.turnstile.Linkable;
import org.bukkit.entity.Player;

/**
 * Represents anything that can activate a turnstile (open it)
 */
public interface TurnstileActivator extends Linkable {

    /**
     * activates the turnstile for a given player.
     * This method sends a request to the turnstile to
     * open it. If successful, the turnstile will open,
     * else it will stay closed.
     * If already open, nothing will change
     * @param player The player to open this turnstile for
     */

    void activateTurnstile(Player player);

}
