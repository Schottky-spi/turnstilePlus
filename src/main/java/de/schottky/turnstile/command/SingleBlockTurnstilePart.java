package de.schottky.turnstile.command;

import de.schottky.turnstile.Turnstile;
import de.schottky.turnstile.TurnstilePart;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class SingleBlockTurnstilePart implements TurnstilePart {

    private final Turnstile parent;
    private final Location blockLocation;

    public SingleBlockTurnstilePart(Turnstile parent, Block block) {
        this.parent = parent;
        this.blockLocation = block.getLocation();
    }

    @Override
    public Turnstile getTurnstile() {
        return parent;
    }

    @Override
    public boolean isContainedIn(Location location) {
        return (location.getX() >= blockLocation.getX() && location.getX() <= blockLocation.getX() + 1) &&
                (location.getY() >= blockLocation.getY() && location.getY() <= blockLocation.getY() + 1) &&
                (location.getZ() >= blockLocation.getZ() && location.getZ() <= blockLocation.getZ() + 1);
    }
}
