package de.schottky.turnstile.activator;

import de.schottky.turnstile.Turnstile;
import org.bukkit.entity.Player;

public interface TurnstileActivator {

    void activateTurnstile(Player player);

    void linkTurnstile(Turnstile turnstile);

    boolean hasBeenRemoved();

}
