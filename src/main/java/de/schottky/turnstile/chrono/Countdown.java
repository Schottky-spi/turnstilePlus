package de.schottky.turnstile.chrono;

import de.schottky.turnstile.TurnstilePlugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Utility-class that counts down from a fixed value
 * but can be reset intermediately
 */

public class Countdown {

    private BukkitTask task;
    private final long ticks;
    private final long granularity;
    private final Runnable whenDone;

    public Countdown(long amount, TimeUnit unit, Runnable whenDone) {
        this.whenDone = whenDone;
        switch (unit) {
            case NANOSECONDS:
            case MICROSECONDS:
            case MILLISECONDS:
            default:
                this.granularity = 1;
                this.ticks = unit.toMillis(amount) / 50L;
                break;
            case SECONDS:
                this.granularity = 20;
                this.ticks = amount;
                break;
            case MINUTES:
                this.granularity = 20 * 60;
                this.ticks = amount;
                break;
            case HOURS:
                this.granularity = 20 * 60 * 60;
                this.ticks = amount;
                break;
            case DAYS:
                this.granularity = 20 * 60 * 60 * 24;
                this.ticks = amount;
                break;
        }
    }

    private State stateIn = State.READY;

    enum State { READY, EXPIRED, STARTED }

    /**
     * resets this Countdown.
     * <br>If this countdown hasn't started and has not expired,
     * this will simply start the countdown
     * <br>If this countdown has started, but not expired, reset
     * the current tick. It will then take the amount that was set initially until
     * this countdown will reset
     * <br>If this countdown has started and expired, this will re-start the
     * countdown and reset it.
     */

    public void reset() {
        currentTick.set(0);
        if (stateIn == State.READY) {
            start();
        } else if (stateIn == State.EXPIRED) {
            stateIn = State.READY;
            start();
        }
    }

    /**
     * cancels the current task. After a call to this method,
     * this countdown will be expired
     */

    public void cancel() {
        if (task != null) {
            task.cancel();
            stateIn = State.EXPIRED;
        }
    }

    private final AtomicLong currentTick = new AtomicLong();

    /**
     * starts counting. When the countdown has expired,
     * the action passed in the constructor will run.
     * <br>If this countdown has already started, or is
     * expired, this method cannot be called and will throw an
     * {@code IllegalStateException}. Use {@link #reset()} to reset
     * the countdown and start a new one.
     */

    public void start() {
        if (stateIn != State.READY)
            throw new IllegalStateException("Cannot start countdown since it's already running or has expired");

        this.task = new BukkitRunnable() {

            @Override
            public void run() {
                final long tick = currentTick.get();
                if (tick == ticks) {
                    task.cancel();
                    whenDone.run();
                    stateIn = State.EXPIRED;
                } else if (tick < ticks) {
                    currentTick.getAndIncrement();
                }
            }

        }.runTaskTimer(JavaPlugin.getPlugin(TurnstilePlugin.class), 0, granularity);
        stateIn = State.STARTED;
    }
}
