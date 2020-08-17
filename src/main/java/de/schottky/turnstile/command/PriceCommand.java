package de.schottky.turnstile.command;

import com.github.schottky.zener.command.ArrayUtil;
import com.github.schottky.zener.command.Cmd;
import com.github.schottky.zener.command.SubCommand;
import de.schottky.turnstile.Turnstile;
import de.schottky.turnstile.TurnstileManager;
import de.schottky.turnstile.economy.EconomyPrice;
import de.schottky.turnstile.economy.ItemPrice;
import de.schottky.turnstile.economy.VaultHandler;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Cmd(name = "setPrice", permission = "ts.command.setPrice")
public class PriceCommand extends SubCommand<BaseCommand> {

    private final SubCommand<PriceCommand> moneyCommand = new Money(this);

    public PriceCommand(BaseCommand parentCommand) {
        super(parentCommand);
        this.registerSubCommands(moneyCommand, new Items(this));
    }

    @Cmd(name = "money", permission = "ts.command.setPrice.money", minArgs = 2)
    static class Money extends SubCommand<PriceCommand> {

        public Money(PriceCommand parentCommand) {
            super(parentCommand);
        }

        @Override
        public boolean onPlayerCommand(@NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
            if (!VaultHandler.isEnabled()) {
                player.sendMessage("Vault is not enabled; you cannot set a price!");
            } else {
                double price = 0;
                try {
                    price = Double.parseDouble(args[1]);
                } catch (NumberFormatException e) {
                    player.sendMessage("Not a number");
                }
                Optional<Turnstile> turnstile = TurnstileManager.instance().forName(args[0], player);
                if (!turnstile.isPresent()) {
                    player.sendMessage("No turnstile by name " + args[0]);
                } else {
                    turnstile.get().setPrice(new EconomyPrice(price));
                    player.sendMessage("Price " + VaultHandler.economy.format(price) + " set for turnstile " + turnstile.get().name());
                }
            }
            return true;
        }
    }

    @Cmd(name = "item", permission = "ts.command.setPrice.item", minArgs = 1)
    static class Items extends SubCommand<PriceCommand> {

        public Items(PriceCommand parentCommand) {
            super(parentCommand);
        }

        @Override
        public boolean onPlayerCommand(@NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
            final Material type = Material.matchMaterial(args[0]);
            new ItemPrice(new ItemStack(type));
            return true;
        }
    }

    @Override
    public boolean onPlayerCommand(@NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return moneyCommand.onPlayerCommand(player, command, label, ArrayUtil.popFirstN(args, 1));
    }

    @Override
    public String tooFewArgumentsMessage(int missing) {
        if (missing == 2) return "please provide the name for thr turnstile";
        if (missing == 1) return "please provide a price";
        return "";
    }
}
