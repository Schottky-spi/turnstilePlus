package de.schottky.turnstile.command;

import com.github.schottky.zener.command.Cmd;
import com.github.schottky.zener.command.SubCmd;
import com.github.schottky.zener.command.SubCommand;
import de.schottky.turnstile.Turnstile;
import de.schottky.turnstile.economy.EconomyPrice;
import de.schottky.turnstile.economy.ItemPrice;
import de.schottky.turnstile.economy.TicketPrice;

@Cmd(value = "setPrice", permission = "ts.command.setPrice")
public class PriceCommand extends SubCommand<BaseCommand> {

    public PriceCommand(BaseCommand parentCommand) {
        super(parentCommand);
    }

    @SubCmd("money")
    public void setMoneyPrice(EconomyPrice price, Turnstile turnstile) {
        turnstile.setPrice(price);
    }

    @SubCmd("ticket")
    public void setTicketPrice(TicketPrice price, Turnstile turnstile) {
        turnstile.setPrice(price);
    }

    @SubCmd("item")
    public void setItemPrice(ItemPrice price, Turnstile turnstile) {
        turnstile.setPrice(price);
    }
}
