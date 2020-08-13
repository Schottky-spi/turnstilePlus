package de.schottky.turnstile;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Abstract Superclass for every regular turnstile.
 * Implements all methods from {@link Turnstile} except
 * for {@link Turnstile#allParts()}
 */

public abstract class AbstractTurnstile implements Turnstile {

    /**
     * players that may traverse the turnstile
     */

    private transient final Set<UUID> acceptedPlayers = new HashSet<>();

    /**
     * The owner of the turnstile
     */
    private UUID owner;

    @Override
    public @NotNull UUID ownerUUID() {
        return owner;
    }

    /**
     * The name of this turnstile, must be unique for every turnstile
     * of a player (can be the same if two
     */

    private final String name;

    @Override
    public @NotNull String name() {
        return name;
    }

    /**
     * instantiates a turnstile with a given name
     * @param name The name
     */

    public AbstractTurnstile(String name) {
        this.name = name;
    }

    public void setOwner(Player player) {
        this.owner = player.getUniqueId();
    }

    @Override
    public void onPlayerTraverse(Player player, Location from, Location to) {
        if (!acceptedPlayers.remove(player.getUniqueId())) {
            final Vector target = from.subtract(to).toVector().normalize().setY(0);
            player.setVelocity(target.multiply(1.5));
        }
        this.setOpen(currentStatus().isOpen);
    }

    @Override
    public boolean requestActivation(Player player) {
        if (player.getUniqueId().equals(ownerUUID()))
            return true;
        // TODO: send messages
        if (acceptedPlayers.contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You have already payed. Go through!");
            return false;
        } else if (withdrawToll(player)) {
            acceptedPlayers.add(player.getUniqueId());
            setOpen(true);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Status currentStatus() {
        return acceptedPlayers.isEmpty() ? Status.CLOSED : Status.OPEN;
    }

    protected boolean withdrawToll(Player player) {
        // TODO: Toll, Vault e.t.c
        return true;
    }

    @Override
    public void setOpen(boolean open) {
        allParts().forEach(part -> part.setBlocking(!open));
        if (!open)
            acceptedPlayers.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractTurnstile that = (AbstractTurnstile) o;
        return owner.equals(that.owner) &&
                name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, name);
    }
}
