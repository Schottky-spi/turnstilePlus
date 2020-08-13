package de.schottky.turnstile.event;

import de.schottky.turnstile.activator.PressurePlateActivator;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PressurePlateListener implements Listener {

    @EventHandler
    public void onPressurePlateActivate(@NotNull PlayerInteractEvent event) {
        if (event.getAction() != Action.PHYSICAL || event.getClickedBlock() == null)
            return;
        final Block block = event.getClickedBlock();
        final List<MetadataValue> metadataValues = block.getMetadata(PressurePlateActivator.METADATA_IDENTIFIER);
        for (MetadataValue metadata: metadataValues) {
            final Object value = metadata.value();
            if (value instanceof PressurePlateActivator) {
                ((PressurePlateActivator) value).activateTurnstile(event.getPlayer());
            }
        }
    }
    
}
