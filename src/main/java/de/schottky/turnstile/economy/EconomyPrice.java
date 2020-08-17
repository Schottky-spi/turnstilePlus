package de.schottky.turnstile.economy;

import com.google.common.base.Preconditions;
import de.schottky.turnstile.persistence.RequiredConstructor;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;

/**
 * withdraws a specific price from a player using
 * vault's economy. Fails, if economy is not present/enabled
 */
public class EconomyPrice implements Price {

    private double amount;

    public EconomyPrice(double amount) {
        this.amount = amount;
    }

    @RequiredConstructor
    EconomyPrice() {}

    @Override
    public boolean withdrawFromPlayer(Player player) {
        Preconditions.checkArgument(VaultHandler.isEnabled(), "Vault not enabled");
        final EconomyResponse response = VaultHandler.economy.withdrawPlayer(player, amount);
        return response.transactionSuccess();
    }
}
