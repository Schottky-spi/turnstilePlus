package de.schottky.turnstile.chrono;

import de.schottky.turnstile.TurnstilePlugin;
import org.bukkit.Bukkit;

public final class Runners {

    private Runners() {}

    public static void delay(Runnable runnable, long delay) {
        Bukkit.getScheduler().runTaskLater(TurnstilePlugin.instance(), runnable, delay);
    }

    public static void delay(Runnable runnable) {
        delay(runnable, 1);
    }
}
