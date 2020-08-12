package de.schottky.turnstile.event;

import de.schottky.turnstile.TurnstileManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getTo() == null || event.getFrom().equals(event.getTo())) return;
        TurnstileManager.traverse(event.getFrom(), event.getTo(), event.getPlayer());
    }
}
