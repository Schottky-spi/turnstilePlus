package de.schottky.turnstile.command;

import de.schottky.turnstile.AbstractTurnstile;
import de.schottky.turnstile.TurnstilePart;
import de.schottky.turnstile.persistence.RequiredConstructor;

import java.util.Collection;
import java.util.Collections;

public class TestTurnstile extends AbstractTurnstile {

    private TurnstilePart onlyPart;

    public TestTurnstile(String name, TurnstilePart onlyPart) {
        super(name);
        this.onlyPart = onlyPart;
    }

    @RequiredConstructor
    private TestTurnstile() {
        super(null);
    }

    @Override
    public Collection<TurnstilePart> allParts() {
        return Collections.singleton(onlyPart);
    }
}
