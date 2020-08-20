package de.schottky.turnstile.command;

import com.github.schottky.zener.command.Cmd;
import com.github.schottky.zener.command.CommandBase;
import com.github.schottky.zener.command.SubCmd;
import com.github.schottky.zener.command.resolver.CommandException;
import com.github.schottky.zener.command.resolver.Unresolved;
import de.schottky.turnstile.*;
import de.schottky.turnstile.economy.ItemPrice;
import org.bukkit.OfflinePlayer;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Collection;

@Cmd(value = "turnstile", maxArgs = 0)
public class BaseCommand extends CommandBase {

    public BaseCommand() {
        super();
        this.registerSubCommands(new PriceCommand(this));
    }

    @SubCmd(value = "list", desc = "Lists all turnstiles you own.")
    public void listAll(@Unresolved Player sender) {
        Collection<Turnstile> turnstiles = TurnstileManager.instance().allTurnstilesForPlayer(sender);

        if (!turnstiles.isEmpty()) {
            sender.sendMessage("You own following turnstiles:");
            for (Turnstile turnstile : turnstiles) {
                sender.sendMessage(turnstile.name());
            }
        } else {
            sender.sendMessage("You don't have any turnstiles.");
        }
    }

    @SubCmd(value = "setup", desc = "Create a turnstile from a target block.")
    public void setupTurnstile(Block block, String turnstileName, @Unresolved Player sender) throws CommandException {
        TurnstilePart part;
        if (Tag.DOORS.isTagged(block.getType())) {
            part = new DoorBlockTurnstilePart(block);
        } else if (Tag.FENCES.isTagged(block.getType())) {
            part = new SingleBlockTurnstilePart(block);
        } else {
            throw new CommandException("You are not looking at a turnstile-block");
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


    @SubCmd(value = "setOwner", desc = "Sets owner of a turnstile (gets reward).")
    public void setOwner(OfflinePlayer owner, Turnstile turnstile) {
        turnstile.setOwner(owner);
    }


    @SubCmd(value = "remove", desc = "Removes a turnstile.")
    public void removeTurnstile(Turnstile turnstile) {
        TurnstileManager.instance().removeTurnstile(turnstile);
    }


    @SubCmd(value = "activate", desc = "Activates the turnstile for a player.")
    public void activateTurnstile(Player player, Turnstile turnstile) {
        turnstile.requestActivation(player);
    }


    @SubCmd(value = "collect", desc = "Collect all items from your turnstile.")
    public void collectPendingItems(@Unresolved Player sender) {
        ItemPrice.collectPendingItems(sender);
    }


    @SubCmd(value = "info", desc = "Displays information about the turnstile.")
    public void info(@Unresolved Player player, Turnstile turnstile) {
        player.sendMessage("Price: " + turnstile.price());
    }

    @SubCmd(value = "link", desc = "Links the turnstile to a target button or sign.")
    public void link(Linkable linkable, Turnstile turnstile, @Unresolved Player sender) {
        if (linkable.linkedTurnstile() != null && linkable.linkedTurnstile() != turnstile) {
            sender.sendMessage(
                    "This " + linkable + " is already linked to turnstile " + linkable.linkedTurnstile().name());
        } else {
            turnstile.link(linkable);
        }
    }

    @SubCmd(value = "unlink", desc = "Unlink target buttons/signs from the turnstile.")
    public void unlink(Linkable linkable, Turnstile turnstile) {
        turnstile.unlink(linkable);
    }
}
