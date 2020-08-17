package de.schottky.turnstile.economy;

import org.bukkit.entity.Player;

/**
 * The price that it costs to traverse through a turnstile
 */
public interface Price {

    /**
     * an empty price; costs nothing and will give nothing to
     * the owner
     * @return a new empty price
     */
    static Price emptyPrice() {
        return new Empty();
    }

    class Empty implements Price {

        @Override
        public boolean withdrawFromPlayer(Player player) {
            return true;
        }
    }

    /**
     * withdraws the amount that it costs to pass through the
     * turnstile from the player
     * @param player The player to withdraw from
     * @return true, if successful, false otherwise
     */

    boolean withdrawFromPlayer(Player player);

}
