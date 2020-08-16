package de.schottky.turnstile.command;

import com.github.schottky.zener.command.Cmd;
import com.github.schottky.zener.command.CommandBase;
import com.github.schottky.zener.command.SubCommand;
import de.schottky.turnstile.Turnstile;
import de.schottky.turnstile.TurnstileManager;
import de.schottky.turnstile.activator.ButtonActivator;
import de.schottky.turnstile.activator.PressurePlateActivator;
import de.schottky.turnstile.tag.CustomTags;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Cmd(name = "link", permission = "ts.command.link", minArgs = 1)
public class LinkCommand extends SubCommand<BaseCommand> {

    public LinkCommand(BaseCommand parentCommand) {
        super(parentCommand);
    }

    @Override
    public boolean onPlayerCommand(
            @NotNull Player player,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args)
    {
        final Optional<Turnstile> turnstile = TurnstileManager.instance().forName(args[0], player);
        if (!turnstile.isPresent()) {
            player.sendMessage("Turnstile not present");
            return true;
        }
        // Use a BlockIterator so that Buttons are not considered "too small" which can be the
        // case for player#getTargetBlock(...)
        final BlockIterator blockIterator = new BlockIterator(player.getLocation(), 1.5, 5);
        while (blockIterator.hasNext()) {
            final Block block = blockIterator.next();
            if (Tag.BUTTONS.isTagged(block.getType())) {
                turnstile.get().link(new ButtonActivator(block));
                player.sendMessage("You have linked this button to turnstile " + turnstile.get().name());
                return true;
            } else if (CustomTags.PRESSURE_PLATES.isTagged(block.getType())) {
                turnstile.get().link(new PressurePlateActivator(block));
                player.sendMessage("You have linked this pressure plate to turnstile " + turnstile.get().name());
                return true;
            }
            if (block.getType().isSolid()) { break; }
        }
        player.sendMessage("You are not looking at a linkable block!");
        return true;
    }

    @Override
    public String tooFewArgumentsMessage(int missing) {
        return "Please provide the name of your turnstile";
    }

    @Override
    protected @Nullable List<String> tabCompleteOptionsFor(
            CommandSender sender,
            Command command,
            String label,
            String[] args)
    {
        if (sender instanceof Player) {
            return TurnstileManager.instance()
                    .allTurnstilesForPlayer((Player) sender)
                    .stream()
                    .map(Turnstile::name)
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }
}
