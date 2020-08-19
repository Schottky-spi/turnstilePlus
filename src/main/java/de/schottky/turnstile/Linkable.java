package de.schottky.turnstile;

public interface Linkable {

    boolean link(Turnstile toTurnstile);

    void destroy();

    Turnstile linkedTurnstile();
}
