package de.schottky.turnstile;

import de.schottky.turnstile.chrono.Runners;
import de.schottky.turnstile.persistence.RequiredConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Door;
import org.bukkit.util.BoundingBox;

public class DoorBlockTurnstilePart implements TurnstilePart {

    private Location lowerBlock;

    public DoorBlockTurnstilePart(Block block) {
        if (!Tag.DOORS.isTagged(block.getType()))
            throw new IllegalArgumentException("block not a door");
        final Door door = (Door) block.getBlockData();
        if (door.getHalf() == Bisected.Half.TOP)
            lowerBlock = block.getRelative(BlockFace.DOWN).getLocation();
        else
            lowerBlock = block.getLocation();
        initAfterLoad();
    }

    @RequiredConstructor
    protected DoorBlockTurnstilePart() { }

    private transient BoundingBox door;

    @Override
    public boolean containsLocation(Location location) {
        return door.contains(location.toVector());
    }

    @Override
    public void setBlocking(boolean shouldBlock) {
        final Block block = lowerBlock.getBlock();
        Door door = (Door) (Tag.DOORS.isTagged(block.getType()) ?
                block.getBlockData() :
                Material.SPRUCE_DOOR.createBlockData());

        door.setOpen(!shouldBlock);

        // delay: strange interaction with BlockRedstoneEvent
        Runners.delay(() -> {
            door.setHalf(Bisected.Half.BOTTOM);
            block.setBlockData(door, false);
            door.setHalf(Bisected.Half.TOP);
            block.getRelative(BlockFace.UP).setBlockData(door, false);
        });
    }

    @Override
    public void initAfterLoad() {
        TurnstilePlugin.instance().doorInteractListener().addObservedLocation(lowerBlock);
         door = BoundingBox.of(lowerBlock.getBlock(), lowerBlock.getBlock().getRelative(BlockFace.UP));
    }

    @Override
    public void destroy() {
        TurnstilePlugin.instance().doorInteractListener().removeObservedLocation(lowerBlock);
    }

    @Override
    public Location location() {
        return lowerBlock.clone().add(0, 1, 0);
    }
}
