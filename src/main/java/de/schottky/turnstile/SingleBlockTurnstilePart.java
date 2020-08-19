package de.schottky.turnstile;

import de.schottky.turnstile.persistence.RequiredConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class SingleBlockTurnstilePart implements TurnstilePart {

    private Location blockLocation;
    private BlockData data;

    public SingleBlockTurnstilePart(Block block, BlockData blockData) {
        this.blockLocation = block.getLocation();
        this.data = blockData;
    }

    public SingleBlockTurnstilePart(Block block) {
        this(block, block.getBlockData());
    }

    @RequiredConstructor
    private SingleBlockTurnstilePart() {}

    @Override
    public boolean containsLocation(Location location) {
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

    @Override
    public void initAfterLoad() { }

    @Override
    public void destroy() { }
}
