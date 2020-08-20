package de.schottky.turnstile.command;

import com.github.schottky.zener.command.Cmd;
import com.github.schottky.zener.command.CommandBase;
import com.github.schottky.zener.command.SubCmd;
import com.github.schottky.zener.command.SubCommand;
import com.github.schottky.zener.command.resolver.CommandException;
import com.github.schottky.zener.command.resolver.Unresolved;
import de.schottky.turnstile.DoorBlockTurnstilePart;
import de.schottky.turnstile.Linkable;
import de.schottky.turnstile.SimpleTurnstile;
import de.schottky.turnstile.SingleBlockTurnstilePart;
import de.schottky.turnstile.Turnstile;
import de.schottky.turnstile.TurnstileManager;
import de.schottky.turnstile.TurnstilePart;
import de.schottky.turnstile.economy.ItemPrice;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.OfflinePlayer;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

@Cmd(value = "turnstile", maxArgs = 0)
public class BaseCommand extends CommandBase {

    public BaseCommand() {
        super();
        this.registerSubCommands(new PriceCommand(this));
    }

    @Override
    public boolean onPlayerCommand(@NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        player.sendMessage("==:: TurnstilePlus ::==");
        subCommands.forEach(subCommand -> player.spigot().sendMessage(createNiceDescription(subCommand)));
        return true;
    }

    private BaseComponent[] createNiceDescription(SubCommand<?> subCommand) {
        final BaseComponent[] command = new ComponentBuilder()
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/turnstile " + subCommand.name()))
                .append("/turnstile ")
                .color(ChatColor.AQUA)
                .append(subCommand.name())
                .create();
        return new ComponentBuilder()
                .append(command)
                .reset()
                .append(" - ")
                .append(subCommand.simpleDescription())
                .create();
    }

    @SubCmd(value = "list", desc = "Lists all turnstiles you own")
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

    @SubCmd(value = "setup", desc = "Create new turnstile from a block you are targeting.")
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


    @SubCmd(value = "setOwner", desc = "Sets owner of a turnstile (collects price).")
    public void setOwner(Turnstile turnstile, OfflinePlayer owner) {
        turnstile.setOwner(owner);
    }


    @SubCmd(value = "remove", desc = "Removes a turnstile")
    public void removeTurnstile(Turnstile turnstile) {
        TurnstileManager.instance().removeTurnstile(turnstile);
    }


    @SubCmd(value = "activate", desc = "Activates the turnstile for a player.")
    public void activateTurnstile(Turnstile turnstile, Player player) {
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

    @SubCmd(value = "link", desc = "Links the turnstile to button (opens) or sign (displays information) you are targeting.")
    public void link(Turnstile turnstile, Linkable linkable, @Unresolved Player sender) {
        if (linkable.linkedTurnstile() != null && linkable.linkedTurnstile() != turnstile) {
            sender.sendMessage(
                    "This " + linkable + " is already linked to turnstile " + linkable.linkedTurnstile().name());
        } else {
            turnstile.link(linkable);
        }
    }

    @SubCmd(value = "unlink", desc = "Unlink buttons or signs you are targeting from the turnstile.")
    public void unlink(Turnstile turnstile, Linkable linkable) {
        turnstile.unlink(linkable);
    }

}
