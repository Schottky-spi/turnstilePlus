package de.schottky.turnstile.command;

import com.github.schottky.zener.command.Cmd;
import com.github.schottky.zener.command.CommandBase;
import com.github.schottky.zener.command.SubCommand;
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
                new ActivateCommand(this),
                new LinkCommand(this),
                new PriceCommand(this),
                new List(this),
                new Remove(this),
                new LinkDisplayCommand(this),
                new UnlinkCommand(this));
    }

    @Override
    public boolean onPlayerCommand(@NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        player.sendMessage(":: TurnstilePlus ::");
        subCommands.forEach(subCommands -> player.sendMessage("/turnstile " + subCommands.name()));
        return true;
    }

    @Cmd(name = "list", maxArgs = 0, permission = "ts.command.list")
    static class List extends SubCommand {

        public List(CommandBase parentCommand) {
            super(parentCommand);
        }

        @Override
        public boolean onPlayerCommand(
                @NotNull Player player,
                @NotNull Command command,
                @NotNull String label,
                @NotNull String[] args)
        {
            Collection<Turnstile> turnstiles = TurnstileManager.instance().allTurnstilesForPlayer(player);

            if (!turnstiles.isEmpty()) {
                player.sendMessage("You have setup the following turnstiles:");
                for (Turnstile turnstile : turnstiles) {
                    player.sendMessage(turnstile.name());
                }
            } else {
                player.sendMessage("You don't have any turnstiles.");
            }
            return true;
        }
    }

    @Cmd(name = "remove", minArgs = 1, permission = "ts.command.remove")
    static class Remove extends SubCommand {

        public Remove(CommandBase parentCommand) {
            super(parentCommand);
        }

        @Override
        public boolean onPlayerCommand(@NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
            TurnstileManager.instance().removeTurnstile(args[0], player);
            return true;
        }
    }

}
