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

    void onPlayerTraverse(Player player, Location from, Location to);

    boolean requestActivation(Player player);

    Status currentStatus();

    Collection<TurnstilePart> allParts();

    void setOpen(boolean open);

    @NotNull String name();

    @NotNull UUID ownerUUID();

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
