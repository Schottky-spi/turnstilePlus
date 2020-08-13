package de.schottky.turnstile;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractTurnstile implements Turnstile {

    private transient final Set<UUID> acceptedPlayers = new HashSet<>();

    private UUID owner;

    @Override
    public @NotNull UUID ownerUUID() {
        return owner;
    }

    private final String name;

    @Override
    public @NotNull String name() {
        return name;
    }

    public AbstractTurnstile(String name) {
        this.name = name;
    }

    public void setOwner(Player player) {
        this.owner = player.getUniqueId();
    }

    public @NotNull OfflinePlayer owner() {
        return Bukkit.getOfflinePlayer(owner);
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
}
