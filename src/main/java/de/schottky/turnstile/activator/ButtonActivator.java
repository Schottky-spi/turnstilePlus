package de.schottky.turnstile.activator;

import de.schottky.turnstile.Turnstile;
import de.schottky.turnstile.TurnstilePlugin;
import de.schottky.turnstile.persistence.RequiredConstructor;
import org.bukkit.Location;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Objects;

public class ButtonActivator extends AbstractActivator {

    private Location buttonLocation;

    public static final String METADATA_IDENTIFIER =
            TurnstilePlugin.instance().getName() + ":button_activator";

    public ButtonActivator(Block button) {
        if (!Tag.BUTTONS.isTagged(button.getType())) {
            throw new RuntimeException("Block not a button");
        }
        this.buttonLocation = button.getLocation();
    }

    @RequiredConstructor
    protected ButtonActivator() { }

    @Override
    public void activateTurnstile(Player player) {
        turnstile.requestActivation(player);
    }

    @Override
    public void linkTurnstile(Turnstile turnstile) {
        super.linkTurnstile(turnstile);
        buttonLocation.getBlock().setMetadata(METADATA_IDENTIFIER,
                new FixedMetadataValue(TurnstilePlugin.instance(), this));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ButtonActivator that = (ButtonActivator) o;
        return Objects.equals(buttonLocation, that.buttonLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), buttonLocation);
    }
}
