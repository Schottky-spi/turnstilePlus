package de.schottky.turnstile;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;

/**
 * Represents a structure in the world that acts as a
 * turnstile
 */

public interface Turnstile {

    /**
     * called when this turnstile is open and a player enters the turnstile
     * @param player The player that enters the turnstile
     * @param from The location that the player came from
     * @param to The location that the player went
     */

    void onPlayerTraverse(Player player, Location from, Location to);

    /**
     * called when a player requests the activation of a turnstile
     * @param player The player that requests the activation
     * @return true, if the turnstile should open, false otherwise
     */

    boolean requestActivation(Player player);

    /**
     * the current status of this turnstile, either open or closed
     * @return open, if the turnstile is open, closed otherwise
     */

    Status currentStatus();

    /**
     * all parts of this turnstile that a player could move through
     * @return all parts that belong to this turnstile
     */

    Collection<TurnstilePart> allParts();

    /**
     * opens or closes this turnstile
     * @param open true, if the turnstile should open, false otherwise
     */

    void setOpen(boolean open);

    /**
     * The name of this turnstile. Must be unique for every player
     * @return The name
     */

    @NotNull String name();

    /**
     * The UUID of the owning player
     * @return The UUID
     */

    @NotNull UUID ownerUUID();

    /**
     * The offlinePlayer that this turnstile belongs to
     * @return The player
     */

    default @NotNull OfflinePlayer owningPlayer() {
        return Bukkit.getOfflinePlayer(ownerUUID());
    }

    enum Status {
        OPEN(true),
        CLOSED(false);

        public final boolean isOpen;

        Status(boolean isOpen) {
            this.isOpen = isOpen;
        }
    }
}
