package de.schottky.turnstile.economy;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface Price {

    static Price emptyPrice() {
        return player -> true;
    }

    boolean withdrawFromPlayer(Player player);

}
