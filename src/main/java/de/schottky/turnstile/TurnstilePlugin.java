package de.schottky.turnstile;

import com.github.schottky.zener.api.Zener;
import com.github.schottky.zener.command.Commands;
import de.schottky.turnstile.command.TurnstileCommand;
import de.schottky.turnstile.event.ButtonClickListener;
import de.schottky.turnstile.event.PlayerMoveListener;
import de.schottky.turnstile.event.PressurePlateListener;
import de.schottky.turnstile.persistence.TurnstilePersistence;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class TurnstilePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        Zener.start(this);
        Commands.registerAll(new TurnstileCommand());

        final PluginManager pluginManager = Bukkit.getPluginManager();
        final PlayerMoveListener listener = new PlayerMoveListener();
        pluginManager.registerEvents(listener, this);
        pluginManager.registerEvents(new ButtonClickListener(), this);
        pluginManager.registerEvents(new PressurePlateListener(), this);

        TurnstileManager.createInstance(listener);
        TurnstilePersistence.loadAll();
    }

    @Override
    public void onDisable() {
        Zener.end();
        TurnstilePersistence.saveAll();
    }

    public static TurnstilePlugin instance() {
        return JavaPlugin.getPlugin(TurnstilePlugin.class);
    }
}
