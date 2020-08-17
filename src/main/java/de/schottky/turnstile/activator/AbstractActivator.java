package de.schottky.turnstile.activator;

import de.schottky.turnstile.Turnstile;
import de.schottky.turnstile.persistence.RequiredConstructor;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;
import java.util.Objects;

/**
 * Abstract superclass for an activator. Contains basic implementations
 * for {@link #link(Turnstile)} and {@link #unlink()} using a weak-reference
 * to a turnstile. Also provides a basic implementation to activate
 * the turnstile. Sub-classes may override all of these method, but should
 * call the super-method.
 */
public abstract class AbstractActivator implements TurnstileActivator {

    protected transient WeakReference<Turnstile> turnstile = new WeakReference<>(null);

    @RequiredConstructor
    protected AbstractActivator() {}

    public void link(Turnstile turnstile) {
        this.turnstile = new WeakReference<>(turnstile);
    }

    @Override
    public Turnstile unlink() {
        final Turnstile theTurnstile = turnstile.get();
        if (theTurnstile == null)
            return null;
        else {
            theTurnstile.unlink(this);
            return theTurnstile;
        }
    }

    @Override
    public void activateTurnstile(Player player) {
        final Turnstile theTurnstile = turnstile.get();
        if (theTurnstile != null)
            theTurnstile.requestActivation(player);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractActivator that = (AbstractActivator) o;
        System.out.println(Objects.equals(turnstile.get(), that.turnstile.get()));
        return Objects.equals(turnstile, that.turnstile);
    }
}
