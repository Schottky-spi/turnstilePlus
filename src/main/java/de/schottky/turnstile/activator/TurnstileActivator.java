package de.schottky.turnstile.activator;

import de.schottky.turnstile.Linkable;
import org.bukkit.entity.Player;

public interface TurnstileActivator extends Linkable {

    void activateTurnstile(Player player);

}
