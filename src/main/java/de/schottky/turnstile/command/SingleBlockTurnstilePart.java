package de.schottky.turnstile.command;

import de.schottky.turnstile.Turnstile;
import de.schottky.turnstile.TurnstilePart;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class SingleBlockTurnstilePart implements TurnstilePart {

    private final Turnstile parent;
    private final Location blockLocation;
    private final BlockData data;

    public SingleBlockTurnstilePart(Turnstile parent, Block block, BlockData blockData) {
        this.parent = parent;
        this.blockLocation = block.getLocation();
        this.data = blockData;
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

    @Override
    public void setBlocking(boolean blocking) {
        blockLocation.getBlock().setBlockData(blocking ?
                data :
                Bukkit.createBlockData(Material.AIR));
    }
}
