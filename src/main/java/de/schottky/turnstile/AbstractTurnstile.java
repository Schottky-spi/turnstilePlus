package de.schottky.turnstile;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractTurnstile implements Turnstile {

    private final Set<UUID> acceptedPlayers = new HashSet<>();

    @Override
    public void onPlayerTraverse(Player player, Location from, Location to) {
        if (!acceptedPlayers.remove(player.getUniqueId())) {
            final Vector target = from.subtract(to).toVector().normalize().setY(0);
            System.out.println(target);
            player.setVelocity(target.multiply(2));
        }
        this.setOpen(currentStatus().isOpen);
    }

    @Override
    public boolean requestActivation(Player player) {
        if (acceptedPlayers.contains(player.getUniqueId())) {
            // TODO: send message
            player.sendMessage(ChatColor.RED + "You must pay first!");
            return false;
        }
        if (withdrawToll(player)) {
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
        for (TurnstilePart part: allParts()) {
            part.setBlocking(!open);
        }
        if (!open)
            acceptedPlayers.clear();
    }
}
