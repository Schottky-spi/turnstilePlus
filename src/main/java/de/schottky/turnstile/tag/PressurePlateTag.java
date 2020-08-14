package de.schottky.turnstile.tag;

import de.schottky.turnstile.TurnstilePlugin;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class PressurePlateTag implements Tag<Material> {

    private final Set<Material> pressurePlates = EnumSet.of(
            Material.ACACIA_PRESSURE_PLATE,
            Material.BIRCH_PRESSURE_PLATE,
            Material.JUNGLE_PRESSURE_PLATE,
            Material.OAK_PRESSURE_PLATE,
            Material.DARK_OAK_PRESSURE_PLATE,
            Material.SPRUCE_PRESSURE_PLATE,
            Material.STONE_PRESSURE_PLATE,
            Material.HEAVY_WEIGHTED_PRESSURE_PLATE,
            Material.LIGHT_WEIGHTED_PRESSURE_PLATE);

    @Override
    public boolean isTagged(@NotNull Material material) {
        return pressurePlates.contains(material);
    }

    @Override
    public @NotNull Set<Material> getValues() {
        return Collections.unmodifiableSet(pressurePlates);
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return new NamespacedKey(TurnstilePlugin.instance(), "pressure_plates");
    }
}