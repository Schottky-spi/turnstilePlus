package de.schottky.turnstile.command;

import com.github.schottky.zener.command.Cmd;
import com.github.schottky.zener.command.CommandBase;
import com.github.schottky.zener.command.SubCommand;
import de.schottky.turnstile.TurnstileManager;
import de.schottky.turnstile.TurnstilePart;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Cmd(name = "setup", permission = "ts.command.setup", maxArgs = 0)
public class TurnstileSetupCommand extends SubCommand {

    public TurnstileSetupCommand(CommandBase parentCommand) {
        super(parentCommand);
    }

    @Override
    public boolean onPlayerCommand(
            @NotNull Player player,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args)
    {
        final Block block = player.getTargetBlock(null, 20);
        final TestTurnstile testTurnstile = new TestTurnstile();
        final BlockData data = Bukkit.createBlockData(Material.ACACIA_FENCE);
        final TurnstilePart part = new SingleBlockTurnstilePart(testTurnstile, block, data);
        testTurnstile.addPart(part);
        TurnstileManager.registerTurnstile(testTurnstile);
        testTurnstile.setOpen(false);
        return true;
    }
}
