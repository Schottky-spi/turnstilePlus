package de.schottky.turnstile.command;

import com.github.schottky.zener.command.Cmd;
import com.github.schottky.zener.command.SubCmd;
import com.github.schottky.zener.command.SubCommand;
import de.schottky.turnstile.Turnstile;
import de.schottky.turnstile.economy.EconomyPrice;
import de.schottky.turnstile.economy.ItemPrice;
import de.schottky.turnstile.economy.Price;
import de.schottky.turnstile.economy.TicketPrice;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Cmd(value = "setPrice", permission = "ts.command.setPrice")
public class PriceCommand extends SubCommand<BaseCommand> {

    public PriceCommand(BaseCommand parentCommand) {
        super(parentCommand);
    }

    @SubCmd(value = "money", desc = "Set a new price")
    public void setMoneyPrice(Turnstile turnstile, EconomyPrice price) {
        setPrice(turnstile, price);
    }

    @SubCmd(value = "ticket", desc = "Set a new ticket-price")
    public void setTicketPrice(Turnstile turnstile, TicketPrice price) {
        setPrice(turnstile, price);
    }

    @SubCmd(value = "item", desc = "Set a new item-price")
    public void setItemPrice(Turnstile turnstile, ItemPrice price) {
        setPrice(turnstile, price);
    }

    private void setPrice(Turnstile turnstile, Price price) {
        turnstile.setPrice(price);
        final OfflinePlayer player = turnstile.owningPlayer();
        if (player.isOnline()) {
            Objects.requireNonNull(player.getPlayer()).sendMessage(
                    "New price " + price + " set for turnstile " + turnstile.name());
        }
    }

    @Override
    public boolean onPlayerCommand(@NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        for (SubCommand<?> command1: subCommands) {
            player.sendMessage("/turnstile setPrice " + command1.name() + " - " + command1.simpleDescription());
        }
        return true;
    }

    @Override
    public String tooFewArgumentsMessage(int missing) {
        if (missing == 2) return "please provide the name for thr turnstile";
        if (missing == 1) return "please provide a price";
        return "";
    }
}
