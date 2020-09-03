package de.schottky.turnstile;

import de.schottky.turnstile.economy.Price;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

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
     * @param direction The direction that the player entered the turnstile
     */

    void onPlayerEnter(@NotNull Player player, @NotNull BlockFace direction);

    /**
     * called when this turnstile is open and a player leaves the turnstile
     * @param player The player that left the turnstile
     * @param direction The direction that the player left the turnstile
     */

    void onPlayerLeave(@NotNull Player player, @NotNull BlockFace direction);

    /**
     * called when a player requests the activation of a turnstile
     * @param player The player that requests the activation
     */

    void requestActivation(@NotNull Player player);

    /**
     * the current status of this turnstile, either open or closed
     * @return open, if the turnstile is open, closed otherwise
     */

    @NotNull Status currentStatus();

    enum Status {
        OPEN(true),
        CLOSED(false);

        public final boolean isOpen;

        Status(boolean isOpen) {
            this.isOpen = isOpen;
        }
    }

    /**
     * all parts of this turnstile that a player could move through
     * @return all parts that belong to this turnstile
     */

    @NotNull @Unmodifiable Collection<TurnstilePart> allParts();

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

    default @NotNull OfflinePlayer owningPlayer() {
        return Bukkit.getOfflinePlayer(ownerUUID());
    }

    void setOwner(@NotNull OfflinePlayer player);

    /**
     * adds a linkable item to this this turnstile.
     * A linkable item can be an activator or an information-display
     * @param linkable The item to add
     */

    void link(@NotNull Linkable linkable);

    /**
     * removes a linkable item from this turnstile
     * @param linkable The linkable item to remove
     */

    void unlink(@NotNull Linkable linkable);

    /**
     * called after the initial load has been performed to do additional
     * tasks with the data
     */
    void initAfterLoad();

    /**
     * destroys this turnstile
     */

    void destroy();

    /**
     * set the price that is needed to pass through this turnstile
     * @param price The price to set. A null price indicates that no price should be set
     */

    void setPrice(@Nullable Price price);

    /**
     * returns the price that it costs to surpass this
     * turnstile
     * @return The price
     */
    @NotNull Price price();

    /**
     * returns an <em>estimate</em> of this turnstile's location.
     * Since a turnstile can be defined using multiple parts, this location
     * might or might not correlate to one of the parts. Whether or not
     * this is the case, is dependent on the concrete implementation.
     * @return An approximation of this turnstile's location
     */
    @NotNull Location location();
}
