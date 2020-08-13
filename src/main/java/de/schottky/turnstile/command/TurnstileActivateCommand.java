package de.schottky.turnstile.command;

import com.github.schottky.zener.command.Cmd;
import com.github.schottky.zener.command.CommandBase;
import com.github.schottky.zener.command.SubCommand;
import de.schottky.turnstile.Turnstile;
import de.schottky.turnstile.TurnstileManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

@Cmd(name = "activate", permission = "ts.command.activate", minArgs = 1)
public class TurnstileActivateCommand extends SubCommand {

    public TurnstileActivateCommand(CommandBase parentCommand) {
        super(parentCommand);
    }

    @Override
    public boolean onPlayerCommand(
            @NotNull Player player,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args)
    {
        TurnstileManager.instance().activateTurnstile(args[0], player);
        return true;
    }

    @Override
    protected @Nullable List<String> tabCompleteOptionsFor(CommandSender sender, Command command, String label, String[] args) {
        return TurnstileManager.instance()
                .allTurnstilesForPlayer((Player) sender)
                .stream()
                .map(Turnstile::name)
                .collect(Collectors.toList());
    }
}
