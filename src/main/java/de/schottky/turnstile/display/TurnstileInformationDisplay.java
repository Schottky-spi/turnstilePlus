package de.schottky.turnstile.display;

import de.schottky.turnstile.Linkable;

/**
 * Anything that wants to display information about a turnstile
 * and receive updates whenever something major changes
 * (the user of the turnstile, the name,...)
 */
public interface TurnstileInformationDisplay extends Linkable {

    /**
     * called when a major change in the turnstile happens and should
     * be used to update any visual dependencies like a sign
     * @param turnstile The turnstile that had it's state changed
     */
    void onTurnstileStateUpdate();

}
