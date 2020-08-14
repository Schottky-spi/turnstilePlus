package de.schottky.turnstile.command;

import com.github.schottky.zener.command.Cmd;
import com.github.schottky.zener.command.CommandBase;
import com.github.schottky.zener.command.SubCommand;
import de.schottky.turnstile.Turnstile;
import de.schottky.turnstile.TurnstileManager;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
                new Remove(this));
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
            player.sendMessage("You have setup the following turnstiles:");
            for (Turnstile turnstile: TurnstileManager.instance().allTurnstilesForPlayer(player)) {
                player.sendMessage(turnstile.name());
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
