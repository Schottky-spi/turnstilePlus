package de.schottky.turnstile.command;

import de.schottky.turnstile.AbstractTurnstile;
import de.schottky.turnstile.TurnstilePart;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class TestTurnstile extends AbstractTurnstile {

    private TurnstilePart onlyPart;

    public TestTurnstile(TurnstilePart onlyPart) {
        this.onlyPart = onlyPart;
    }

    public TestTurnstile() {
        this.onlyPart = null;
    }

    public void addPart(TurnstilePart onlyPart) {
        this.onlyPart = onlyPart;
    }

    @Override
    public Collection<TurnstilePart> allParts() {
        return Collections.singleton(onlyPart);
    }
}
