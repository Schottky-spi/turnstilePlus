package de.schottky.turnstile;

import de.schottky.turnstile.activator.TurnstileActivator;
import de.schottky.turnstile.chrono.Countdown;
import de.schottky.turnstile.display.TurnstileInformationDisplay;
import de.schottky.turnstile.economy.Price;
import de.schottky.turnstile.persistence.RequiredConstructor;
import de.schottky.turnstile.persistence.TurnstilePersistence;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Abstract Superclass for every regular turnstile.
 * Implements all methods from {@link Turnstile} except
 * for {@link Turnstile#allParts()}
 */

public abstract class AbstractTurnstile implements Turnstile {

    private final Set<TurnstileInformationDisplay> informationDisplays = new HashSet<>();

    @Override
    public void addInformationDisplay(TurnstileInformationDisplay display) {
        this.informationDisplays.add(display);
        postUpdate();
    }

    @Override
    public void removeInformationDisplay(TurnstileInformationDisplay display) {
        this.informationDisplays.remove(display);
    }

    private void postUpdate() {
        informationDisplays.forEach(display -> display.displayInformationAbout(this));
    }

    private final Set<TurnstileActivator> activators = new HashSet<>();

    @Override
    public void link(TurnstileActivator activator) {
        this.activators.add(activator);
        activator.link(this);
        TurnstilePersistence.saveAllAsyncFor(ownerUUID());
    }

    @Override
    public void unlink(TurnstileActivator activator) {
        this.activators.remove(activator);
        TurnstilePersistence.saveAllAsyncFor(ownerUUID());
    }

    @Override
    public void initAfterLoad() {
        this.setOpen(false);
        // TODO: can cause ConcurrentModificationException when called within
        // TODO: link
        informationDisplays.forEach(informationDisplays -> informationDisplays.link(this));
        activators.forEach(activator -> activator.link(this));
        allParts().forEach(TurnstilePart::initAfterLoad);
        postUpdate();
    }

    /**
     * The owner of the turnstile
     */
    private UUID owner;

    @Override
    public @NotNull UUID ownerUUID() {
        return owner;
    }

    public void setOwner(Player player) {
        this.owner = player.getUniqueId();
        postUpdate();
    }

    /**
     * The name of this turnstile, must be unique for every turnstile
     * of a player (can be the same if two
     */

    private String name;

    @Override
    public @NotNull String name() {
        return name;
    }

    private @NotNull Price price = Price.emptyPrice();

    @Override
    public void setPrice(@Nullable Price price) {
        this.price = price == null ? Price.emptyPrice() : price;
        postUpdate();
    }

    @Override
    public Price price() {
        return price;
    }

    /**
     * instantiates a turnstile with a given name
     * @param name The name
     */

    public AbstractTurnstile(String name, Player owner) {
        this.name = name;
        this.owner = owner.getUniqueId();
    }

    @RequiredConstructor
    protected AbstractTurnstile() { }

    /**
     * players that may traverse the turnstile
     */

    private transient final Set<UUID> acceptedPlayers = new HashSet<>();

    /**
     * players that are in the turnstile mapped to the BlockFace that they came from
     */

    private transient final Map<UUID,BlockFace> playersInTurnstile = new HashMap<>();

    @Override
    public void onPlayerEnter(Player player, BlockFace direction) {
        if (!acceptedPlayers.contains(player.getUniqueId())) {
            player.setVelocity(direction.getDirection().multiply(-2));
        } else {
            this.playersInTurnstile.put(player.getUniqueId(), direction);
        }
    }

    @Override
    public void onPlayerLeave(Player player, BlockFace direction) {
        final BlockFace entered = playersInTurnstile.remove(player.getUniqueId());
        if (entered == null) {
            player.setVelocity(direction.getDirection().multiply(-2));
        } else if (entered != direction) {
            this.acceptedPlayers.remove(player.getUniqueId());
            this.setOpen(currentStatus().isOpen);
            TurnstileManager.instance().postTurnstileUpdate(this);
        }
    }

    @Override
    public void requestActivation(Player player) {
        if (acceptedPlayers.contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You have already payed. Go through!");
        } else if (withdrawToll(player)) {
            acceptedPlayers.add(player.getUniqueId());
            TurnstileManager.instance().postTurnstileUpdate(this);
            setOpen(true);
            player.sendMessage("here you go!");
        }
    }

    @Override
    public Status currentStatus() {
        return acceptedPlayers.isEmpty() ? Status.CLOSED : Status.OPEN;
    }

    protected boolean withdrawToll(Player player) {
        if (price.withdrawFromPlayer(player)) {
            return true;
        } else {
            player.sendMessage("You do not have the required price at hand!");
            return false;
        }
    }

    private transient final Countdown countdown = new Countdown(10, TimeUnit.SECONDS, () -> setOpen(false));

    @Override
    public void setOpen(boolean open) {
        allParts().forEach(part -> part.setBlocking(!open));
        if (!open) {
            acceptedPlayers.clear();
            countdown.cancel();
        } else {
            countdown.reset();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractTurnstile that = (AbstractTurnstile) o;
        return owner.equals(that.owner) &&
                name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, name);
    }
}
