package de.schottky.turnstile.activator;

import com.github.schottky.zener.localization.Localizable;
import de.schottky.turnstile.Turnstile;
import de.schottky.turnstile.TurnstilePlugin;
import de.schottky.turnstile.metadata.MetadataKeys;
import de.schottky.turnstile.persistence.RequiredConstructor;
import de.schottky.turnstile.tag.CustomTags;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Objects;

/**
 * An activator that is based upon a pressure-plate
 */
public class PressurePlateActivator extends AbstractActivator implements Localizable {

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
    public boolean link(Turnstile turnstile) {
        final Block block = pressurePlateLocation.getBlock();
        if (CustomTags.PRESSURE_PLATES.isTagged(block.getType())) {
            block.setMetadata(METADATA_IDENTIFIER,
                    new FixedMetadataValue(TurnstilePlugin.instance(), this));
            return super.link(turnstile);
        }
        return false;
    }

    @Override
    public void destroy() {
        pressurePlateLocation.getBlock().removeMetadata(METADATA_IDENTIFIER, TurnstilePlugin.instance());
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
    public String toString() {
        return "Pressure plate-activator";
    }

    @Override
    public int hashCode() {
        return Objects.hash(pressurePlateLocation);
    }

    @Override
    public String identifier() {
        return "ident.activator.pressure_plate";
    }
}
