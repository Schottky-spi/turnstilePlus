package de.schottky.turnstile.command;

import com.github.schottky.zener.command.Cmd;
import com.github.schottky.zener.command.CommandBase;
import com.github.schottky.zener.command.SubCommand;
import de.schottky.turnstile.Linkable;
import de.schottky.turnstile.Turnstile;
import de.schottky.turnstile.TurnstilePlugin;
import de.schottky.turnstile.metadata.MetadataKeys;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.BlockIterator;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Cmd(name = "unlink", permission = "ts.command.unlink", maxArgs = 0)
public class UnlinkCommand extends SubCommand {

    public UnlinkCommand(CommandBase parentCommand) {
        super(parentCommand);
    }

    @Override
    public boolean onPlayerCommand(
            @NotNull Player player,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args)
    {
        System.out.println(MetadataKeys.getAllKeys());
        final BlockIterator blockIterator = new BlockIterator(player.getLocation(), 1.5, 5);
        while (blockIterator.hasNext()) {
            final Block block = blockIterator.next();
            for (String key: MetadataKeys.getAllKeys()) {
                final List<MetadataValue> values = block.getMetadata(key);
                for (MetadataValue metadata: values) {
                    final Object value = metadata.value();
                    if (value instanceof Linkable) {
                        Turnstile turnstile = ((Linkable) value).unlink();
                        block.removeMetadata(key, TurnstilePlugin.instance());
                        if (turnstile != null) player.sendMessage("Successfully unlinked from " + turnstile.name());
                        return true;
                    }
                }
            }
            if (block.getType().isSolid()) break;
        }
        player.sendMessage("Nothing here can be unlinked");
        return true;
    }
}
