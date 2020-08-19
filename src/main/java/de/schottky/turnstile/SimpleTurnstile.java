package de.schottky.turnstile;

import de.schottky.turnstile.persistence.RequiredConstructor;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;

public class SimpleTurnstile extends AbstractTurnstile {

    private TurnstilePart turnstilePart;

    public SimpleTurnstile(String name, Player owner, TurnstilePart turnstilePart) {
        super(name, owner);
        this.turnstilePart = turnstilePart;
    }

    @RequiredConstructor
    private SimpleTurnstile() {
        super();
    }

    @Override
    public Collection<TurnstilePart> allParts() {
        if (turnstilePart == null)
            return Collections.emptyList();
        else
            return Collections.singleton(turnstilePart);
    }
}
