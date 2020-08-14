package de.schottky.turnstile.display;

import de.schottky.turnstile.Turnstile;

public interface TurnstileInformationDisplay {

    void displayInformationAbout(Turnstile turnstile);

    boolean hasBeenRemoved();

}
