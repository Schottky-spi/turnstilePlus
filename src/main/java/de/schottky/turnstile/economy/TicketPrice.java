package de.schottky.turnstile.economy;


import de.schottky.turnstile.persistence.RequiredConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TicketPrice implements Price {

    protected ItemStack stack;

    public TicketPrice(ItemStack stack) {
        this.stack = stack;
    }

    @RequiredConstructor
    public TicketPrice() {

    }

    @Override
    public boolean withdrawFromPlayer(Player player, OfflinePlayer owner) {
        if (player.getInventory().containsAtLeast(stack, stack.getAmount())) {
            int toRemove = stack.getAmount();
            for (ItemStack stack: player.getInventory().getContents()) {
                if (this.stack.isSimilar(stack)) {
                    final int amount = stack.getAmount() - toRemove;
                    if (amount >= 0) {
                        stack.setAmount(amount);
                        return true;
                    } else {
                        stack.setAmount(0);
                        toRemove -= amount;
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
