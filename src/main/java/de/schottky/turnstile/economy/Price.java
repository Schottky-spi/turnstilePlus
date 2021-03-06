package de.schottky.turnstile.economy;

import com.github.schottky.zener.localization.Localizable;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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

    class Empty implements Price, Localizable {

        @Override
        public boolean withdrawFromPlayer(Player player, OfflinePlayer owner) {
            return true;
        }

        @Override
        public @NotNull Type type() {
            return Type.EMPTY;
        }

        @Override
        public String toString() {
            return "nothing";
        }

        @Override
        public String identifier() {
            return "ident.empty_price";
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

    @NotNull Type type();

    enum Type {
        ITEM,
        TICKET,
        MONEY,
        EMPTY
    }

}
