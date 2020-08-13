package de.schottky.turnstile.activator;

import de.schottky.turnstile.Turnstile;
import de.schottky.turnstile.TurnstilePlugin;
import de.schottky.turnstile.persistence.RequiredConstructor;
import de.schottky.turnstile.tag.CustomTags;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;

public class PressurePlateActivator extends AbstractActivator {

    private Location pressurePlateLocation;

    public static final String METADATA_IDENTIFIER = TurnstilePlugin.instance().getName() + ":plate_activator";

    public PressurePlateActivator(Block block) {
        if (!CustomTags.PRESSURE_PLATES.isTagged(block.getType())) {
            throw new RuntimeException("block not a pressure plate");
        }
        this.pressurePlateLocation = block.getLocation();
    }

    @RequiredConstructor
    public PressurePlateActivator() {}

    @Override
    public void linkTurnstile(Turnstile turnstile) {
        final Block block = pressurePlateLocation.getBlock();
        if (CustomTags.PRESSURE_PLATES.isTagged(block.getType())) {
            super.linkTurnstile(turnstile);
            block.setMetadata(METADATA_IDENTIFIER,
                    new FixedMetadataValue(TurnstilePlugin.instance(), this));
        }
    }

    @Override
    public boolean hasBeenRemoved() {
        return CustomTags.PRESSURE_PLATES.isTagged(pressurePlateLocation.getBlock().getType());
    }

}
