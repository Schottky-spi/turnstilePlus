package de.schottky.turnstile.command;

import com.github.schottky.zener.command.Cmd;
import com.github.schottky.zener.command.SubCommand;
import de.schottky.turnstile.Turnstile;
import de.schottky.turnstile.TurnstileManager;
import de.schottky.turnstile.display.SignDisplay;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Cmd(name = "linkSign", permission = "ts.command.linkSign", minArgs = 1)
public class LinkDisplayCommand extends SubCommand<BaseCommand> {

    public LinkDisplayCommand(BaseCommand parentCommand) {
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
            player.sendMessage("You do not have a turnstile by that name!");
            return true;
        } else {
            final BlockIterator blockIterator = new BlockIterator(player.getLocation(), 1.5, 5);
            while (blockIterator.hasNext()) {
                final Block block = blockIterator.next();
                if (Tag.SIGNS.isTagged(block.getType())) {
                    turnstile.get().addInformationDisplay(new SignDisplay(block));
                    player.sendMessage("Sign linked");
                    return true;
                }
                if (block.getType().isSolid()) { return true; }
            }
        }
        return true;
    }
}
