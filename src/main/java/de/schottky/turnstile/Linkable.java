package de.schottky.turnstile;

public interface Linkable {

    void link(Turnstile toTurnstile);

    Turnstile unlink();
}
