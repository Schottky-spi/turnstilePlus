package de.schottky.turnstile;

import de.schottky.turnstile.chrono.Countdown;
import de.schottky.turnstile.display.TurnstileInformationDisplay;
import de.schottky.turnstile.economy.Price;
import de.schottky.turnstile.persistence.RequiredConstructor;
import de.schottky.turnstile.persistence.TurnstilePersistence;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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

    private final Set<Linkable> linkables = new HashSet<>();

    public void link(Linkable linkable) {
        if (linkable.link(this)) {
            // remove and add to allow overrides
            this.linkables.remove(linkable);
            this.linkables.add(linkable);
            TurnstilePersistence.saveAllAsyncFor(ownerUUID());
            owningOnlinePlayer().ifPresent(p -> p.sendMessage("You have linked this " + linkable));
        }
        postUpdate();
    }

    public void unlink(Linkable linkable) {
        linkable.destroy();
        owningOnlinePlayer().ifPresent(player -> player.sendMessage("Successfully removed " + linkable));
        TurnstilePersistence.saveAllAsyncFor(ownerUUID());
    }

    public void destroy() {
        linkables.forEach(Linkable::destroy);
        linkables.clear();
        allParts().forEach(TurnstilePart::destroy);
        owningOnlinePlayer().ifPresent(player -> player.sendMessage("removed turnstile " + name()));
    }

    @Override
    public void initAfterLoad() {
        allParts().forEach(TurnstilePart::initAfterLoad);
        boolean changed = false;
        final Iterator<Linkable> itr = linkables.iterator();
        while (itr.hasNext()) {
            final Linkable linkable = itr.next();
            if (linkable == null) {
                itr.remove();
                changed = true;
            } else if (!linkable.link(this)) {
                itr.remove();
                changed = true;
            }
        }
        if (changed)
            TurnstilePersistence.saveAllAsyncFor(ownerUUID());

        this.setOpen(false);
        postUpdate();
    }

    private void postUpdate() {
        linkables.stream().filter(linkable -> linkable instanceof TurnstileInformationDisplay)
                .map(linkable -> (TurnstileInformationDisplay) linkable)
                .forEach(TurnstileInformationDisplay::onTurnstileStateUpdate);
    }

    /**
     * The owner of the turnstile
     */
    private UUID owner;

    @Override
    public @NotNull UUID ownerUUID() {
        return owner;
    }

    private Optional<Player> owningOnlinePlayer() {
        return Optional.ofNullable(owningPlayer().getPlayer());
    }

    public void setOwner(OfflinePlayer player) {
        final Player previousOwner = Bukkit.getPlayer(ownerUUID());
        if (previousOwner != null)
            previousOwner.sendMessage("You are no longer the owner of turnstile " + name());

        this.owner = player.getUniqueId();
        if (player.isOnline())
            Objects.requireNonNull(player.getPlayer()).sendMessage(
                    "You are now the owner of turnstile " + name());

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
        if (price.withdrawFromPlayer(player, owningPlayer())) {
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
