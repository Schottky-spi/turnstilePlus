package de.schottky.turnstile;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import de.schottky.turnstile.event.PlayerMoveListener;
import de.schottky.turnstile.persistence.TurnstilePersistence;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility-class that manages multiple turnstiles
 */
public final class TurnstileManager {

    private static TurnstileManager instance;

    public static TurnstileManager instance() {
        return instance;
    }

    public static void createInstance(PlayerMoveListener listener) {
        if (instance != null) throw new RuntimeException("Only one TurnstileManager may exist");
        instance = new TurnstileManager(listener);
    }

    private TurnstileManager(PlayerMoveListener listener) {
        this.listener = listener;
    }

    private final PlayerMoveListener listener;

    /**
     * All the turnstiles that exist on the server
     */
    private final Set<Turnstile> allTurnstiles = new HashSet<>();

    /**
     * returns an unmodifiable collection of all turnstiles that belong to a certain
     * player
     * @param player the player that the turnstiles belong to
     * @return The turnstiles of this player
     */
    public Collection<Turnstile> allTurnstilesForPlayer(Player player) {
        return allTurnstiles.stream()
                .filter(turnstile -> turnstile.ownerUUID().equals(player.getUniqueId()))
                .collect(Collectors.toSet());
    }

    /**
     * register a new turnstile for a player. This will allow it to receive
     * events like {@link Turnstile#onPlayerEnter(Player, BlockFace)}
     * @param player The player that this turnstile belongs to
     * @param turnstile The turnstile
     */

    public void registerTurnstile(Player player, Turnstile turnstile) {
        allTurnstiles.add(turnstile);
        TurnstilePersistence.saveAsync(player.getUniqueId(), allTurnstilesForPlayer(player));
    }

    /**
     * loads turnstile-data. This will override any existing data
     * @param turnstileData The data to load
     */

    public void loadTurnstileData(Multimap<UUID,Turnstile> turnstileData) {
        allTurnstiles.clear();
        allTurnstiles.addAll(turnstileData.values());
        allTurnstiles.forEach(turnstile -> turnstile.setOpen(false));
    }

    /**
     * gets the data to be persistent after server reload / server shutdown
     * @return The persistent data
     */

    public Multimap<UUID,Turnstile> retrieveTurnstileData() {
        final ImmutableMultimap.Builder<UUID,Turnstile> builder = ImmutableMultimap.builder();
        for (Turnstile turnstile: allTurnstiles) {
            builder.put(turnstile.ownerUUID(), turnstile);
        }
        return builder.build();
    }

    /**
     * Activate a certain turnstile
     * @param t The turnstile to activate
     * @param player the player to activate it for
     */

    public void activateTurnstile(Turnstile t, Player player) {
        t.requestActivation(player);
    }

    /**
     * This should be the only method that distributes updates from the turnstile
     * to all listening nodes
     * @param turnstile The turnstile that wants to have its update posted
     */

    public void postTurnstileUpdate(Turnstile turnstile) {
        listener.onTurnstileChange(turnstile);
    }

    public void activateTurnstile(String turnstileName, Player owner) {
        final Optional<Turnstile> turnstile = forName(turnstileName, owner);
        if (turnstile.isPresent()) {
            activateTurnstile(turnstile.get(), owner);
            owner.sendMessage("You have activated turnstile " + turnstileName);
        } else {
            owner.sendMessage("You do not own a turnstile by that name!");
        }
    }

    public Optional<Turnstile> forName(String name, Player owner) {
        return allTurnstiles.stream()
                .filter(t -> t.ownerUUID().equals(owner.getUniqueId()) &&
                        t.name().equalsIgnoreCase(name))
                .findFirst();
    }

    public void removeTurnstile(Turnstile turnstile) {
        this.allTurnstiles.remove(turnstile);
    }

    public void removeTurnstile(String name, Player owner) {
        final Optional<Turnstile> turnstile = forName(name, owner);
        if (turnstile.isPresent()) {
            removeTurnstile(turnstile.get());
            owner.sendMessage("Successfully removed Turnstile " + name);
        } else {
            owner.sendMessage("You do not own a turnstile by that name!");
        }
    }

}
