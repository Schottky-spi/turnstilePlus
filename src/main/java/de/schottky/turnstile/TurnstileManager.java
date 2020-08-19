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
        return allTurnstilesForPlayer(player.getUniqueId());
    }

    public Collection<Turnstile> allTurnstilesForPlayer(UUID playerUUID) {
        return allTurnstiles.stream()
                .filter(turnstile -> turnstile.ownerUUID().equals(playerUUID))
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
        allTurnstiles.forEach(Turnstile::initAfterLoad);
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
     * This should be the only method that distributes updates from the turnstile
     * to all listening nodes
     * @param turnstile The turnstile that wants to have its update posted
     */

    public void postTurnstileUpdate(Turnstile turnstile) {
        listener.onTurnstileChange(turnstile);
    }

    public Optional<Turnstile> forName(String name, Player owner) {
        return forIdentification(name, owner.getUniqueId());
    }

    public Optional<Turnstile> forIdentification(String name, UUID uuid) {
        return allTurnstiles.stream()
                .filter(t -> t.ownerUUID().equals(uuid) &&
                        t.name().equalsIgnoreCase(name))
                .findFirst();
    }

    public void removeTurnstile(Turnstile turnstile) {
        turnstile.destroy();
        this.allTurnstiles.remove(turnstile);
        TurnstilePersistence.saveAllAsyncFor(turnstile.ownerUUID());
    }

}
