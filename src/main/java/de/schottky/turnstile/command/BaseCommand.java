package de.schottky.turnstile.command;

import com.github.schottky.zener.command.Cmd;
import com.github.schottky.zener.command.CommandBase;
import com.github.schottky.zener.command.SubCmd;
import de.schottky.turnstile.Turnstile;
import de.schottky.turnstile.TurnstileManager;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

@Cmd(name = "turnstile", maxArgs = 0)
public class BaseCommand extends CommandBase {

    public BaseCommand() {
        super();
        this.registerSubCommands(
                new SetupCommand(this),
                new LinkCommand(this),
                new PriceCommand(this),
                new LinkDisplayCommand(this),
                new UnlinkCommand(this));
    }

    @Override
    public boolean onPlayerCommand(@NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        player.sendMessage(":: TurnstilePlus ::");
        subCommands.forEach(subCommands -> player.sendMessage("/turnstile " + subCommands.name() + " - " + subCommands.simpleDescription()));
        return true;
    }

    @SubCmd(value = "list", desc = "Lists all turnstiles that you have")
    public void listAll(Player player) {
        Collection<Turnstile> turnstiles = TurnstileManager.instance().allTurnstilesForPlayer(player);

        if (!turnstiles.isEmpty()) {
            player.sendMessage("You have setup the following turnstiles:");
            for (Turnstile turnstile : turnstiles) {
                player.sendMessage(turnstile.name());
            }
        } else {
            player.sendMessage("You don't have any turnstiles.");
        }
    }

    @SubCmd(value = "remove", desc = "removes a turnstile")
    public void removeTurnstile(Player player, String turnstile) {
        TurnstileManager.instance().removeTurnstile(turnstile, player);
    }

    @SubCmd("activate")
    public void activateTurnstile(Player player, String turnstile) {
        TurnstileManager.instance().activateTurnstile(turnstile, player);
    }

}
