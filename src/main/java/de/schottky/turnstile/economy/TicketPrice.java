package de.schottky.turnstile.economy;


import de.schottky.turnstile.persistence.RequiredConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class TicketPrice implements Price {

    protected ItemStack stack;

    public TicketPrice(ItemStack stack) {
        this.stack = stack;
    }

    @RequiredConstructor
    public TicketPrice() {

    }

    @Override
    public @NotNull Type type() {
        return Type.TICKET;
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

    @Override
    public String toString() {
        return "Ticket(" + stack.getType().name().replace('_', ' ').toLowerCase(Locale.ENGLISH)
                + " x " + stack.getAmount() + ")";
    }
}
