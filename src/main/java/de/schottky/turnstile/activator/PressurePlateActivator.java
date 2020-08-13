package de.schottky.turnstile.activator;

import de.schottky.turnstile.Turnstile;
import de.schottky.turnstile.TurnstilePlugin;
import de.schottky.turnstile.persistence.RequiredConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;

public class PressurePlateActivator extends AbstractActivator {

    private Location pressurePlateLocation;

    public static final String METADATA_IDENTIFIER = TurnstilePlugin.instance().getName() + ":plate_activator";

    public PressurePlateActivator(Block block) {
        if (!isPressurePlate(block.getType())) {
            throw new RuntimeException("block not a pressure plate");
        }
        this.pressurePlateLocation = block.getLocation();
    }

    @RequiredConstructor
    public PressurePlateActivator() {}

    @Override
    public void linkTurnstile(Turnstile turnstile) {
        final Block block = pressurePlateLocation.getBlock();
        if (isPressurePlate(block.getType())) {
            super.linkTurnstile(turnstile);
            block.setMetadata(METADATA_IDENTIFIER,
                    new FixedMetadataValue(TurnstilePlugin.instance(), this));
        }
    }

    private boolean isPressurePlate(Material type) {
        return Tag.WOODEN_PRESSURE_PLATES.isTagged(type) ||
                type == Material.HEAVY_WEIGHTED_PRESSURE_PLATE ||
                type == Material.LIGHT_WEIGHTED_PRESSURE_PLATE ||
                type == Material.STONE_PRESSURE_PLATE;
    }
}
