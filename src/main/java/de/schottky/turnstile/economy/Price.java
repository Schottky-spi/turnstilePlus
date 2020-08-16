package de.schottky.turnstile.economy;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface Price {

    static Price emptyPrice() {
        return new Empty();
    }

    class Empty implements Price {

        @Override
        public boolean withdrawFromPlayer(Player player) {
            return false;
        }
    }

    boolean withdrawFromPlayer(Player player);

}
