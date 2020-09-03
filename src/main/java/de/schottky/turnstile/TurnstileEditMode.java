package de.schottky.turnstile;

import com.github.schottky.zener.localization.I18n;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class TurnstileEditMode {

    private TurnstileEditMode() {}

    private static final Map<UUID,Turnstile> editingPlayers = new HashMap<>();

    public static void enterEditMode(Player player, Turnstile turnstile) {
        editingPlayers.put(player.getUniqueId(), turnstile);
        player.sendMessage(I18n.of("edit_mode.entered", "name", turnstile.name()));
    }

    public static void exitEditMode(Player player) {
        final Turnstile turnstile = editingPlayers.remove(player.getUniqueId());
        if (turnstile != null)
            player.sendMessage(I18n.of("edit_mode.exited", "name", turnstile.name()));
    }

    public static Optional<Turnstile> forPlayer(Player player) {
        return Optional.ofNullable(editingPlayers.get(player.getUniqueId()));
    }

    public static boolean isEditing(Player player) {
        return editingPlayers.containsKey(player.getUniqueId());
    }
}
