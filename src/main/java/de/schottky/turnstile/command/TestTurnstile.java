package de.schottky.turnstile.command;

import de.schottky.turnstile.AbstractTurnstile;
import de.schottky.turnstile.TurnstilePart;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class TestTurnstile extends AbstractTurnstile {

    private final Collection<TurnstilePart> parts;

    public TestTurnstile(Collection<TurnstilePart> parts) {
        this.parts = new ArrayList<>(parts);
    }

    public TestTurnstile() {
        this.parts = new ArrayList<>();
    }

    public void addParts(Collection<TurnstilePart> parts) {
        this.parts.addAll(parts);
    }

    @Override
    public Collection<TurnstilePart> allParts() {
        return Collections.unmodifiableCollection(parts);
    }
}
