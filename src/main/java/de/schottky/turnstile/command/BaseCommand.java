package de.schottky.turnstile.command;

import com.github.schottky.zener.command.Cmd;
import com.github.schottky.zener.command.CommandBase;
import com.github.schottky.zener.command.SubCmd;
import com.github.schottky.zener.command.resolver.CommandException;
import com.github.schottky.zener.command.resolver.Describe;
import com.github.schottky.zener.command.resolver.Unresolved;
import com.github.schottky.zener.localization.I18n;
import de.schottky.turnstile.*;
import de.schottky.turnstile.economy.ItemPrice;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Cmd("turnstile")
public class BaseCommand extends CommandBase {

    public BaseCommand() {
        super();
        this.registerSubCommands(new PriceCommand(this));
    }

    @SubCmd("list")
    public void listAll(@Unresolved Player sender) {
        Collection<Turnstile> turnstiles = TurnstileManager.instance().allTurnstilesForPlayer(sender);

        if (!turnstiles.isEmpty()) {
            sender.sendMessage(I18n.of("message.list_turnstiles_owned"));
            for (Turnstile turnstile : turnstiles) {
                sender.sendMessage(turnstile.name());
            }
        } else {
            sender.sendMessage(I18n.of("message.no_turnstiles_owned"));
        }
    }

    @SubCmd("setup")
    public void setupTurnstile(Block block, @Describe("turnstile") String turnstileName, @Unresolved Player sender) {
        TurnstilePart part;
        if (Tag.DOORS.isTagged(block.getType())) {
            part = new DoorBlockTurnstilePart(block);
        } else if (Tag.FENCES.isTagged(block.getType())) {
            part = new SingleBlockTurnstilePart(block);
        } else {
            throw new CommandException(I18n.of("no_turnstile_block"));
        }
        final Turnstile turnstile = new SimpleTurnstile(turnstileName, sender, part);
        TurnstileManager.instance().registerTurnstile(sender, turnstile);
    }

    @SubCmd("enterEditMode")
    public void enterEditMode(@Unresolved Player player, Turnstile turnstile) {
        TurnstileEditMode.enterEditMode(player, turnstile);
    }

    @SubCmd("exitEditMode")
    public void exitEditMode(@Unresolved Player player) {
        TurnstileEditMode.exitEditMode(player);
    }

    @SubCmd("setOwner")
    public void setOwner(OfflinePlayer owner, Turnstile turnstile) {
        turnstile.setOwner(owner);
    }

    @SubCmd("remove")
    public void removeTurnstile(Turnstile turnstile) {
        TurnstileManager.instance().removeTurnstile(turnstile);
    }

    @SubCmd("activate")
    public void activateTurnstile(Player player, Turnstile turnstile) {
        turnstile.requestActivation(player);
    }

    @SubCmd("collect")
    public void collectPendingItems(@Unresolved Player sender) {
        ItemPrice.collectPendingItems(sender);
    }

    @SubCmd("info")
    public void info(@Unresolved Player player, Turnstile turnstile) {
        player.sendMessage(I18n.of("message.info", "price", turnstile.price()));
    }

    @SubCmd("link")
    public void link(Linkable linkable, Turnstile turnstile, @Unresolved Player sender) {
        if (linkable.linkedTurnstile() != null && linkable.linkedTurnstile() != turnstile) {
            sender.sendMessage(I18n.of("message.linkable_already_linked",
                    "linkable", linkable,
                    "name", linkable.linkedTurnstile().name()));
        } else {
            turnstile.link(linkable);
        }
    }

    @SubCmd("unlink")
    public void unlink(Linkable linkable, Turnstile turnstile) {
        turnstile.unlink(linkable);
    }

    @SubCmd("locate")
    public void locate(Turnstile turnstile, @Unresolved Player requester) {
        requester.sendMessage(I18n.of("message.turnstile_located",
                "name", turnstile.name(),
                "location", locToDescription(turnstile.location())));
    }

    private String locToDescription(Location location) {
        final Map<String,Object> replacements = new HashMap<>();
        replacements.put("x", location.getX());
        replacements.put("y", location.getY());
        replacements.put("z", location.getZ());
        replacements.put("world", Optional.ofNullable(location.getWorld()).map(World::getName).orElse("null"));
        return I18n.of("misc.simple_location", replacements);
    }
}
