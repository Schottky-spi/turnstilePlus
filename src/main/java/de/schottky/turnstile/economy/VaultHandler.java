package de.schottky.turnstile.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Handler for an economy.
 */
public class VaultHandler {

    /**The economy-object that can be used to withdraw or give money
     * <em>It should always be checked if economy is enabled ({@link #isEnabled()})
     * before using this</em>
     *
     * */
    public static Economy economy = null;

    /**
     * initializes economy. Should only be called once in {@link JavaPlugin#onEnable()}
     */
    public static void init() {
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            final RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
            if (economyProvider != null)
                economy = economyProvider.getProvider();
        }
    }

    /**
     * returns {@code true}, if economy is present and working, {@code false} otherwise
     * @return whether or not Vault is enabled and {@link #economy} can be used
     */
    public static boolean isEnabled() {
        return economy != null;
    }
}
