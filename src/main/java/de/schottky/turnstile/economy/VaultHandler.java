package de.schottky.turnstile.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHandler {

    public static Economy economy = null;

    public static void init() {
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            final RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
            if (economyProvider != null)
                economy = economyProvider.getProvider();
        }
    }

    public static boolean isEnabled() {
        return economy != null;
    }
}
