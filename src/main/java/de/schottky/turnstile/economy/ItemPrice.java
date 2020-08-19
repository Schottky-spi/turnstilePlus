package de.schottky.turnstile.economy;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import de.schottky.turnstile.persistence.RequiredConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Locale;
import java.util.UUID;

/**
 * withdraws a certain amount of items from the player
 */
public class ItemPrice extends TicketPrice {

    private static final Multimap<UUID,ItemStack> pendingStacks = HashMultimap.create();

    public static void loadAll(Multimap<UUID,ItemStack> pending) {
        pendingStacks.putAll(pending);
    }

    public static Multimap<UUID,ItemStack> save() {
        return ImmutableMultimap.copyOf(pendingStacks);
    }

    public static void collectPendingItems(Player player) {
        final Collection<ItemStack> stacks = pendingStacks.removeAll(player.getUniqueId());
        if (stacks != null && !stacks.isEmpty()) {
            player.getInventory().addItem(stacks.toArray(new ItemStack[0]));
        }
    }

    @Override
    public @NotNull Type type() {
        return Type.ITEM;
    }

    public ItemPrice(ItemStack item) {
        super(item);
    }

    @RequiredConstructor
    protected ItemPrice() {
        super();
    }

    @Override
    public boolean withdrawFromPlayer(Player player, OfflinePlayer owner) {
        if (super.withdrawFromPlayer(player, owner)) {
            addToPending(owner, stack.clone());
            return true;
        } else {
            return false;
        }
    }

    private void addToPending(OfflinePlayer player, ItemStack stack) {
        if (!pendingStacks.containsEntry(player.getUniqueId(), stack)) {
            pendingStacks.put(player.getUniqueId(), stack);
        } else {
            final Collection<ItemStack> stacks = pendingStacks.get(player.getUniqueId());
            for (ItemStack stackInMap: stacks) {
                if (stack.isSimilar(stackInMap)) {
                    stackInMap.setAmount(stackInMap.getAmount() + stack.getAmount());
                    return;
                }
            }
        }
    }
}
