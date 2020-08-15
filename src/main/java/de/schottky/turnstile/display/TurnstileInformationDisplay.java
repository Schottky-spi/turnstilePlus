package de.schottky.turnstile.display;

import de.schottky.turnstile.Linkable;
import de.schottky.turnstile.Turnstile;

public interface TurnstileInformationDisplay extends Linkable {

    void displayInformationAbout(Turnstile turnstile);

}
