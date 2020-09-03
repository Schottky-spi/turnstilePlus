package de.schottky.turnstile.chrono;

import de.schottky.turnstile.TurnstilePlugin;
import org.bukkit.Bukkit;

/**
 * Utility-class that is mainly focuses on delaying tasks
 */
public final class Runners {

    private Runners() {}

    /**
     * delays a certain task (the runnable) a fixed amount of ticks
     * @param runnable The task to delay
     * @param delay The amount to delay
     */
    public static void delay(Runnable runnable, long delay) {
        Bukkit.getScheduler().runTaskLater(TurnstilePlugin.instance(), runnable, delay);
    }

    /**
     * delays a certain task (the runnable) one tick
     * @param runnable The task to delay
     */
    public static void delay(Runnable runnable) {
        delay(runnable, 1);
    }
}
