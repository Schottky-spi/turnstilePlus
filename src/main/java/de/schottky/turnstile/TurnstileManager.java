package de.schottky.turnstile;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import de.schottky.turnstile.persistence.TurnstilePersistence;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility-class that manages multiple turnstiles
 */
public final class TurnstileManager {

    private TurnstileManager() {}

    /**
     * All the turnstiles that exist on the server
     */
    private static final Set<Turnstile> allTurnstiles = new HashSet<>();

    /**
     * returns an unmodifiable collection of all turnstiles that belong to a certain
     * player
     * @param player the player that the turnstiles belong to
     * @return The turnstiles of this player
     */
    public static Collection<Turnstile> allTurnstilesForPlayer(Player player) {
        return allTurnstiles.stream()
                .filter(turnstile -> turnstile.ownerUUID().equals(player.getUniqueId()))
                .collect(Collectors.toSet());
    }

    /**
     * a set containing all open turnstiles
     */

    private static final Set<Turnstile> openTurnstiles = new HashSet<>();

    /**
     * register a new turnstile for a player. This will allow it to receive
     * events like {@link Turnstile#onPlayerTraverse(Player, Location, Location)}
     * @param player The player that this turnstile belongs to
     * @param turnstile The turnstile
     */

    public static void registerTurnstile(Player player, Turnstile turnstile) {
        allTurnstiles.add(turnstile);
        TurnstilePersistence.saveAsync(player.getUniqueId(), allTurnstilesForPlayer(player));
    }

    /**
     * loads turnstile-data. This will override any existing data
     * @param turnstileData The data to load
     */

    public static void loadTurnstileData(Multimap<UUID,Turnstile> turnstileData) {
        allTurnstiles.clear();
        allTurnstiles.addAll(turnstileData.values());
    }

    /**
     * gets the data to be persistent after server reload / server shutdown
     * @return The persistent data
     */

    public static Multimap<UUID,Turnstile> retrieveTurnstileData() {
        final ImmutableMultimap.Builder<UUID,Turnstile> builder = ImmutableMultimap.builder();
        for (Turnstile turnstile: allTurnstiles) {
            builder.put(turnstile.ownerUUID(), turnstile);
        }
        return builder.build();
    }

    private final Map<UUID,Turnstile> playersInTurnstile = new HashMap<>();

    /**
     * called when a player traverses from a location to another.
     * Neither may be null nor be the same.
     * @param from The location that the player came from
     * @param to The location that the player went to
     * @param player The player that traversed
     */

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

    /**
     * Activate a certain turnstile
     * @param t The turnstile to activate
     * @param player the player to activate it for
     */

    public static void activateTurnstile(Turnstile t, Player player) {
        if (t.requestActivation(player)) openTurnstiles.add(t);
    }

    public static void activateTurnstile(String turnstileName, Player owner) {
        final Optional<Turnstile> turnstile = allTurnstiles.stream()
                .filter(t -> t.ownerUUID().equals(owner.getUniqueId()) &&
                        t.name().equalsIgnoreCase(turnstileName))
                .findFirst();
        if (turnstile.isPresent()) {
            activateTurnstile(turnstile.get(), owner);
        } else {
            owner.sendMessage("You do not own a turnstile by that name!");
        }
    }

}
