package de.schottky.turnstile;

import com.github.schottky.zener.api.Zener;
import com.github.schottky.zener.command.Commands;
import com.github.schottky.zener.localization.Language;
import com.github.schottky.zener.localization.LanguageFile;
import de.schottky.turnstile.command.BaseCommand;
import de.schottky.turnstile.command.CustomArguments;
import de.schottky.turnstile.economy.VaultHandler;
import de.schottky.turnstile.event.ButtonClickListener;
import de.schottky.turnstile.event.DoorInteractListener;
import de.schottky.turnstile.event.PlayerMoveListener;
import de.schottky.turnstile.event.PressurePlateListener;
import de.schottky.turnstile.persistence.TurnstilePersistence;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Locale;

public class TurnstilePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        Zener.start(this);
        Language.setCurrent(Language.forPlugin(this, Locale.US, LanguageFile.StorageProvider.JSON));

        CustomArguments.registerAll();
        Commands.registerAll(new BaseCommand());

        final PluginManager pluginManager = Bukkit.getPluginManager();
        final PlayerMoveListener listener = new PlayerMoveListener();
        pluginManager.registerEvents(listener, this);
        pluginManager.registerEvents(new ButtonClickListener(), this);
        pluginManager.registerEvents(new PressurePlateListener(), this);
        pluginManager.registerEvents(doorInteractListener, this);

        TurnstileManager.createInstance(listener);
        TurnstilePersistence.loadAll();
        VaultHandler.init();
    }

    @Override
    public void onDisable() {
        Zener.end();
        TurnstilePersistence.saveAll();
    }

    private final DoorInteractListener doorInteractListener = new DoorInteractListener();

    public DoorInteractListener doorInteractListener() {
        return doorInteractListener;
    }

    public static TurnstilePlugin instance() {
        return JavaPlugin.getPlugin(TurnstilePlugin.class);
    }
}
