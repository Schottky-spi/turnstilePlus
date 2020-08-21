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

import java.util.Objects;

@Cmd(value = "setPrice", permission = "ts.command.setPrice", desc = "Sets money, ticket or item price for the turnstile.")
public class PriceCommand extends SubCommand<BaseCommand> {

    public PriceCommand(BaseCommand parentCommand) {
        super(parentCommand);
    }

    @SubCmd("money")
    public void setMoneyPrice(EconomyPrice price, Turnstile turnstile) {
        setPrice(price, turnstile);
    }

    @SubCmd("ticket")
    public void setTicketPrice(TicketPrice price, Turnstile turnstile) {
        setPrice(price, turnstile);
    }

    @SubCmd("item")
    public void setItemPrice(ItemPrice price, Turnstile turnstile) {
        setPrice(price, turnstile);
    }

    private void setPrice(Price price, Turnstile turnstile) {
        turnstile.setPrice(price);
        final OfflinePlayer player = turnstile.owningPlayer();
        if (player.isOnline()) {
            Objects.requireNonNull(player.getPlayer()).sendMessage(
                    "New price " + price + " set for turnstile " + turnstile.name());
        }
    }

    @Override
    public String tooFewArgumentsMessage(int missing) {
        if (missing == 2) return "please provide the name for the turnstile";
        if (missing == 1) return "please provide a price";
        return "";
    }
}
