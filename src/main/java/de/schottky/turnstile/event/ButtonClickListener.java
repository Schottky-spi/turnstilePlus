package de.schottky.turnstile.event;

import de.schottky.turnstile.activator.ButtonActivator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

public class ButtonClickListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onButtonClick(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
            final List<MetadataValue> metadataValues = event.getClickedBlock()
                    .getMetadata(ButtonActivator.METADATA_IDENTIFIER);
            for (MetadataValue metadata: metadataValues) {
                final Object value = metadata.value();
                if (value instanceof ButtonActivator) {
                    ((ButtonActivator) value).activateTurnstile(event.getPlayer());
                }
            }
        }
    }
}
