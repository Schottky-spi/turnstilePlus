package de.schottky.turnstile;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class TurnstileManager {

    private static final Set<Turnstile> allTurnstiles = new HashSet<>();
    private static final Set<Turnstile> activeTurnstiles = new HashSet<>();

    public static void registerTurnstile(Turnstile turnstile) {
        allTurnstiles.add(turnstile);
    }

    public static void traverse(Location from, Location to, Player player) {
        for (Turnstile turnstile: activeTurnstiles) {
            for (TurnstilePart part: turnstile.allParts()) {
                if (part.isContainedIn(to)) {
                    turnstile.onPlayerTraverse(player, from, to);
                    if (turnstile.currentStatus() == Turnstile.Status.CLOSED)
                        activeTurnstiles.remove(turnstile);
                }
            }
        }
    }

    public static void activateTurnstile(Turnstile t) {
        activeTurnstiles.add(t);
    }

    public static void activateAllTurnstiles() {
        activeTurnstiles.addAll(allTurnstiles);
    }

}
