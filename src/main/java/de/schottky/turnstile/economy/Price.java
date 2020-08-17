package de.schottky.turnstile.economy;

import org.bukkit.OfflinePlayer;
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
        public boolean withdrawFromPlayer(Player player, OfflinePlayer owner) {
            return true;
        }
    }

    /**
     * withdraws the amount that it costs to pass through the
     * turnstile from the player
     * @param player The player to withdraw from
     * @param owner The player that owns the turnstile and to whom the amount should go
     * @return true, if successful, false otherwise
     */

    boolean withdrawFromPlayer(Player player, OfflinePlayer owner);

}
