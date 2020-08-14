package de.schottky.turnstile.economy;

import org.bukkit.entity.Player;

public class EmptyPrice implements Price {

    @Override
    public boolean withdrawFromPlayer(Player player) {
        return true;
    }
}
