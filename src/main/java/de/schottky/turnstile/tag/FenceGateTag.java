package de.schottky.turnstile.tag;

import com.google.common.collect.Sets;
import de.schottky.turnstile.TurnstilePlugin;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class FenceGateTag implements Tag<Material> {

    private final Set<Material> fenceGates = Sets.immutableEnumSet(
            Material.ACACIA_FENCE_GATE,
            Material.BIRCH_FENCE_GATE,
            Material.DARK_OAK_FENCE_GATE,
            Material.JUNGLE_FENCE_GATE,
            Material.OAK_FENCE_GATE,
            Material.SPRUCE_FENCE_GATE
    );

    @Override
    public boolean isTagged(@NotNull Material material) {
        return fenceGates.contains(material);
    }

    @Override
    public @NotNull Set<Material> getValues() {
        return fenceGates;
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return new NamespacedKey(TurnstilePlugin.instance(), "fence_gates");
    }
}
