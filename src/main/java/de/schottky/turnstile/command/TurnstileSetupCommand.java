package de.schottky.turnstile.command;

import com.github.schottky.zener.command.Cmd;
import com.github.schottky.zener.command.CommandBase;
import com.github.schottky.zener.command.SubCommand;
import de.schottky.turnstile.TurnstileManager;
import de.schottky.turnstile.TurnstilePart;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

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
        final TurnstilePart part = new SingleBlockTurnstilePart(testTurnstile, block);
        testTurnstile.addParts(Collections.singleton(part));
        TurnstileManager.registerTurnstile(testTurnstile);
        block.setType(Material.RED_WOOL);
        return true;
    }
}
