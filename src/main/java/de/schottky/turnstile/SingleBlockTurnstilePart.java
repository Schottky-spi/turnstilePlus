package de.schottky.turnstile;

import de.schottky.turnstile.persistence.RequiredConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.BoundingBox;

public class SingleBlockTurnstilePart implements TurnstilePart {

    private Location blockLocation;
    private BlockData data;

    public SingleBlockTurnstilePart(Block block, BlockData blockData) {
        this.blockLocation = block.getLocation();
        this.data = blockData;
        initAfterLoad();
    }

    public SingleBlockTurnstilePart(Block block) {
        this(block, block.getBlockData());
    }

    @RequiredConstructor
    private SingleBlockTurnstilePart() {}

    private transient BoundingBox blockBB;

    @Override
    public boolean containsLocation(Location location) {
        return blockBB.contains(location.toVector());
    }

    @Override
    public void setBlocking(boolean blocking) {
        blockLocation.getBlock().setBlockData(blocking ?
                data :
                Bukkit.createBlockData(Material.AIR));
    }

    @Override
    public void initAfterLoad() {
        this.blockBB = blockLocation.getBlock().getBoundingBox();
    }

    @Override
    public void destroy() { }

    @Override
    public Location location() {
        return blockLocation.clone();
    }
}
