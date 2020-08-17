package de.schottky.turnstile.command;

import com.github.schottky.zener.command.Cmd;
import com.github.schottky.zener.command.SubCmd;
import com.github.schottky.zener.command.SubCommand;
import de.schottky.turnstile.Turnstile;
import de.schottky.turnstile.TurnstileManager;
import de.schottky.turnstile.economy.EconomyPrice;
import de.schottky.turnstile.economy.ItemPrice;
import de.schottky.turnstile.economy.TicketPrice;
import de.schottky.turnstile.economy.VaultHandler;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Cmd(name = "setPrice", permission = "ts.command.setPrice")
public class PriceCommand extends SubCommand<BaseCommand> {

    public PriceCommand(BaseCommand parentCommand) {
        super(parentCommand);
    }

    @SubCmd(value = "money", desc = "Set a new price")
    public void setMoneyPrice(Player player, String turnstileName, int amount) {
        final Optional<Turnstile> turnstile = TurnstileManager.instance().forName(turnstileName, player);
        if (!turnstile.isPresent()) {
            player.sendMessage("No turnstile by name " + turnstileName);
        } else {
            turnstile.get().setPrice(new EconomyPrice(amount));
            player.sendMessage("Price " + VaultHandler.economy.format(amount) + " set for turnstile " + turnstileName);
        }
    }

    @SubCmd(value = "ticket", desc = "Set a new ticket-price")
    public void setTicketPrice(Player player, String turnstileName, NamespacedKey item) {
        final Optional<Turnstile> turnstile = TurnstileManager.instance().forName(turnstileName, player);
        if (!turnstile.isPresent()) {
            player.sendMessage("No turnstile by name " + turnstileName);
        } else {
            final Material type = Material.matchMaterial(item.getKey());
            if (type == null) {
                player.sendMessage("There is no material by the name " + item);
                return;
            }
            turnstile.get().setPrice(new TicketPrice(new ItemStack(type)));
            player.sendMessage("Price " + type + " set for turnstile " + turnstileName);
        }
    }

    @SubCmd(value = "item", desc = "Set a new item-price")
    public void setItemPrice(Player player, String turnstileName, NamespacedKey item) {
        final Optional<Turnstile> turnstile = TurnstileManager.instance().forName(turnstileName, player);
        if (!turnstile.isPresent()) {
            player.sendMessage("No turnstile by name " + turnstileName);
        } else {
            final Material type = Material.matchMaterial(item.getKey());
            if (type == null) {
                player.sendMessage("There is no material by the name " + item);
                return;
            }
            turnstile.get().setPrice(new ItemPrice(new ItemStack(type)));
            player.sendMessage("Price " + type + " set for turnstile " + turnstileName);
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
