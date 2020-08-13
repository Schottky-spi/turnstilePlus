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
public class TurnstileCommand extends CommandBase {

    public TurnstileCommand() {
        super();
        this.registerSubCommands(
                new TurnstileSetupCommand(this),
                new TurnstileActivateCommand(this),
                new List(this));
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
            for (Turnstile turnstile: TurnstileManager.allTurnstilesForPlayer(player)) {
                player.sendMessage(turnstile.name());
            }
            return true;
        }
    }

}
