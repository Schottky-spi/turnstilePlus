package de.schottky.turnstile.activator;

import de.schottky.turnstile.Turnstile;
import de.schottky.turnstile.persistence.RequiredConstructor;
import org.bukkit.entity.Player;

import java.util.Objects;

public abstract class AbstractActivator implements TurnstileActivator {

    protected transient Turnstile turnstile;

    @RequiredConstructor
    protected AbstractActivator() {}

    @Override
    public void linkTurnstile(Turnstile turnstile) {
        this.turnstile = turnstile;
    }

    @Override
    public void activateTurnstile(Player player) {
        turnstile.requestActivation(player);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractActivator that = (AbstractActivator) o;
        return Objects.equals(turnstile, that.turnstile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turnstile);
    }
}
