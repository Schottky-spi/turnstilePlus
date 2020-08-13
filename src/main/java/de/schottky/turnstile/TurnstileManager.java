package de.schottky.turnstile;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import de.schottky.turnstile.persistence.TurnstilePersistence;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class TurnstileManager {

    private static final Multimap<UUID,Turnstile> allTurnstiles = HashMultimap.create();

    public static Collection<Turnstile> allTurnstilesForPlayer(Player player) {
        return Collections.unmodifiableCollection(allTurnstiles.get(player.getUniqueId()));
    }

    private static final Set<Turnstile> openTurnstiles = new HashSet<>();

    public static void registerTurnstile(Player player, Turnstile turnstile) {
        allTurnstiles.put(player.getUniqueId(), turnstile);
        TurnstilePersistence.saveAsync(player.getUniqueId(), allTurnstiles.get(player.getUniqueId()));
    }

    public static void loadTurnstileData(Multimap<UUID,Turnstile> turnstileData) {
        allTurnstiles.clear();
        allTurnstiles.putAll(turnstileData);
    }

    public static Multimap<UUID,Turnstile> retrieveTurnstileData() {
        return ImmutableMultimap.copyOf(allTurnstiles);
    }

    public static void traverse(Location from, Location to, Player player) {
        final Iterator<Turnstile> iter = openTurnstiles.iterator();
        while (iter.hasNext()) {
            final Turnstile turnstile = iter.next();
            for (TurnstilePart part: turnstile.allParts()) {
                if (part.containsLocation(to)) {
                    turnstile.onPlayerTraverse(player, from, to);
                    if (turnstile.currentStatus() == Turnstile.Status.CLOSED)
                        iter.remove();
                }
            }
        }
    }

    public static void activateTurnstile(Turnstile t, Player player) {
        if (t.requestActivation(player)) openTurnstiles.add(t);
    }

    public static void activateAllTurnstiles(Player player) {
        for (Turnstile turnstile: allTurnstiles.values()) activateTurnstile(turnstile, player);
    }

}
