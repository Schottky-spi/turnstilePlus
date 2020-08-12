package de.schottky.turnstile;

import com.github.schottky.zener.api.Zener;
import com.github.schottky.zener.command.Commands;
import de.schottky.turnstile.command.TurnstileCommand;
import de.schottky.turnstile.event.PlayerMoveListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class TurnstilePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        Zener.start(this);
        final PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerMoveListener(), this);
        Commands.registerAll(new TurnstileCommand());
    }

    @Override
    public void onDisable() {
        Zener.end();
    }
}
