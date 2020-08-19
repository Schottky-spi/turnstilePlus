package de.schottky.turnstile.command;

import de.schottky.turnstile.AbstractTurnstile;
import de.schottky.turnstile.TurnstilePart;
import de.schottky.turnstile.persistence.RequiredConstructor;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;

public class TestTurnstile extends AbstractTurnstile {

    private TurnstilePart turnstilePart;

    public TestTurnstile(String name, Player owner, TurnstilePart turnstilePart) {
        super(name, owner);
        this.turnstilePart = turnstilePart;
    }

    @RequiredConstructor
    private TestTurnstile() {
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
