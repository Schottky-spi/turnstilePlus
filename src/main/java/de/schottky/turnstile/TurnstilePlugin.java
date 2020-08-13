package de.schottky.turnstile;

import com.github.schottky.zener.api.Zener;
import com.github.schottky.zener.command.Commands;
import de.schottky.turnstile.command.TurnstileCommand;
import de.schottky.turnstile.event.PlayerMoveListener;
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

        TurnstileManager.createInstance(listener);
        TurnstilePersistence.loadAll();
    }

    @Override
    public void onDisable() {
        Zener.end();
        TurnstilePersistence.saveAll();
    }
}
