package de.schottky.turnstile.event;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Door;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashSet;
import java.util.Set;

public class DoorInteractListener implements Listener {

    private final Set<Location> notIntractable = new HashSet<>();

    public void addObservedLocation(Location location) {
        this.notIntractable.add(location);
    }

    public void removeObservedLocation(Location location) {
        this.notIntractable.remove(location);
    }

    @EventHandler
    public void onDoorInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if (shouldCancel(event.getClickedBlock()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockRedstone(BlockRedstoneEvent event) {
        if (shouldCancel(event.getBlock())) {
            event.setNewCurrent(event.getOldCurrent());
        }
    }

    private boolean shouldCancel(Block block) {
        if (block != null && block.getBlockData() instanceof Door) {
            final Door door = (Door) block.getBlockData();
            Location loc = door.getHalf() == Bisected.Half.TOP ?
                    block.getRelative(BlockFace.DOWN).getLocation() :
                    block.getLocation();
            return notIntractable.contains(loc);
        }
        return false;
    }

}
