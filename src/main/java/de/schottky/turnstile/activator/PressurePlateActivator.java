package de.schottky.turnstile.activator;

import de.schottky.turnstile.Turnstile;
import de.schottky.turnstile.TurnstilePlugin;
import de.schottky.turnstile.metadata.MetadataKeys;
import de.schottky.turnstile.persistence.RequiredConstructor;
import de.schottky.turnstile.tag.CustomTags;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Objects;

public class PressurePlateActivator extends AbstractActivator {

    private Location pressurePlateLocation;

    public static final String METADATA_IDENTIFIER = MetadataKeys.create("plate_activator");

    public PressurePlateActivator(Block block) {
        if (!CustomTags.PRESSURE_PLATES.isTagged(block.getType())) {
            throw new RuntimeException("block not a pressure plate");
        }
        this.pressurePlateLocation = block.getLocation();
    }

    @RequiredConstructor
    public PressurePlateActivator() {}

    @Override
    public void link(Turnstile turnstile) {
        super.link(turnstile);
        final Block block = pressurePlateLocation.getBlock();
        if (CustomTags.PRESSURE_PLATES.isTagged(block.getType())) {
            super.link(turnstile);
            block.setMetadata(METADATA_IDENTIFIER,
                    new FixedMetadataValue(TurnstilePlugin.instance(), this));
        } else {
            this.unlink();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PressurePlateActivator that = (PressurePlateActivator) o;
        return Objects.equals(pressurePlateLocation, that.pressurePlateLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pressurePlateLocation);
    }
}
