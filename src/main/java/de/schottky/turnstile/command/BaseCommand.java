package de.schottky.turnstile.command;

import com.github.schottky.zener.command.*;
import com.github.schottky.zener.command.resolver.SuccessMessage;
import com.github.schottky.zener.command.resolver.Unresolved;
import de.schottky.turnstile.Linkable;
import de.schottky.turnstile.Turnstile;
import de.schottky.turnstile.TurnstileManager;
import de.schottky.turnstile.economy.ItemPrice;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

@Cmd(value = "turnstile", maxArgs = 0)
public class BaseCommand extends CommandBase {

    public BaseCommand() {
        super();
        this.registerSubCommands(
                new SetupCommand(this),
                new PriceCommand(this));
    }

    @Override
    public boolean onPlayerCommand(@NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        player.sendMessage(":: TurnstilePlus ::");
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

    @SubCmd(value = "list", desc = "Lists all turnstiles that you have")
    public void listAll(@Unresolved Player sender) {
        Collection<Turnstile> turnstiles = TurnstileManager.instance().allTurnstilesForPlayer(sender);

        if (!turnstiles.isEmpty()) {
            sender.sendMessage("You have setup the following turnstiles:");
            for (Turnstile turnstile : turnstiles) {
                sender.sendMessage(turnstile.name());
            }
        } else {
            sender.sendMessage("You don't have any turnstiles.");
        }
    }


    @SubCmd("setOwner")
    public void setOwner(Turnstile turnstile, OfflinePlayer owner) {
        turnstile.setOwner(owner);
    }


    @SubCmd(value = "remove", desc = "removes a turnstile")
    public void removeTurnstile(Turnstile turnstile) {
        TurnstileManager.instance().removeTurnstile(turnstile);
    }


    @SubCmd(value = "activate", desc = "activates the turnstile for a player")
    public void activateTurnstile(Turnstile turnstile, Player player) {
        turnstile.requestActivation(player);
    }


    @SubCmd(value = "collect", desc = "Collect all items from your turnstile")
    public void collectPendingItems(@Unresolved Player sender) {
        ItemPrice.collectPendingItems(sender);
    }


    @SubCmd(value = "info", desc = "displays information about the turnstile")
    public void info(@Unresolved Player player, Turnstile turnstile) {
        player.sendMessage("Price: " + turnstile.price());
    }


    @SubCmd(value = "link")
    public void link(Turnstile turnstile, Linkable linkable) {
        turnstile.link(linkable);
    }


    @SubCmd(value = "unlink")
    public void unlink(Turnstile turnstile, Linkable linkable) {
        turnstile.unlink(linkable);
    }

}
