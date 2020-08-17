package de.schottky.turnstile.economy;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * withdraws a certain amount of items from the player
 */
public class ItemPrice implements Price {

    ItemStack stack;

    public ItemPrice(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public boolean withdrawFromPlayer(Player player, OfflinePlayer owner) {
        final PlayerInventory inv = player.getInventory();
        if (inv.containsAtLeast(stack, stack.getAmount())) {
            inv.remove(stack);
            return true;
        } else {
            return false;
        }
    }
}
