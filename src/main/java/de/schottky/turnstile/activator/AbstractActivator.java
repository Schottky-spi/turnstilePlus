package de.schottky.turnstile.activator;

import de.schottky.turnstile.Turnstile;
import de.schottky.turnstile.persistence.RequiredConstructor;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;
import java.util.Objects;

/**
 * Abstract superclass for an activator. Contains basic implementations
 * for {@link #link(Turnstile)} and {@link #linkedTurnstile()} ()} using a weak-reference
 * to a turnstile. Also provides a basic implementation to activate
 * the turnstile. Sub-classes may override all of these method, but should
 * call the super-method.
 */
public abstract class AbstractActivator implements TurnstileActivator {

    @RequiredConstructor
    protected AbstractActivator() {}

    protected transient WeakReference<Turnstile> turnstile = new WeakReference<>(null);

    public boolean link(Turnstile turnstile) {
        this.turnstile = new WeakReference<>(turnstile);
        return true;
    }

    @Override
    public Turnstile linkedTurnstile() {
        return turnstile.get();
    }

    @Override
    public void destroy() {

    }

    @Override
    public void activateTurnstile(Player player) {
        if (linkedTurnstile() != null)
            linkedTurnstile().requestActivation(player);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractActivator that = (AbstractActivator) o;
        return Objects.equals(turnstile, that.turnstile);
    }
}
