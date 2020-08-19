package de.schottky.turnstile.command;

import com.github.schottky.zener.command.CommandContext;
import com.github.schottky.zener.command.resolver.*;
import com.github.schottky.zener.command.resolver.argument.AbstractContextualArgument;
import com.github.schottky.zener.command.resolver.argument.AbstractHighLevelArg;
import com.github.schottky.zener.command.resolver.argument.AbstractLowLevelArg;
import com.github.schottky.zener.command.resolver.argument.Arguments;
import de.schottky.turnstile.Linkable;
import de.schottky.turnstile.Turnstile;
import de.schottky.turnstile.TurnstileManager;
import de.schottky.turnstile.activator.ButtonActivator;
import de.schottky.turnstile.activator.PressurePlateActivator;
import de.schottky.turnstile.display.SignDisplay;
import de.schottky.turnstile.economy.EconomyPrice;
import de.schottky.turnstile.economy.ItemPrice;
import de.schottky.turnstile.economy.TicketPrice;
import de.schottky.turnstile.tag.CustomTags;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;

import java.util.stream.Stream;

public class CustomArguments {

    public static void registerAll() {
        ArgumentResolver.registerArgument(Turnstile.class, TurnstileArgument::new);
        ArgumentResolver.registerArgument(EconomyPrice.class, EconomyPriceArgument::new);
        ArgumentResolver.registerArgument(ItemPrice.class, ItemPriceArgument::new);
        ArgumentResolver.registerArgument(TicketPrice.class, TicketArgument::new);
        ArgumentResolver.registerArgument(Linkable.class, LinkableArgument::new);
    }

    public static class TurnstileArgument extends AbstractLowLevelArg<Turnstile> {

        @Override
        protected Turnstile fromArgument(String arg, CommandContext context) throws ArgumentNotResolvable {
            return TurnstileManager.instance().forName(arg, context.getPlayer())
                    .orElseThrow(() -> ArgumentNotResolvable.withMessage("You do not own a turnstile by that name"));
        }

        @Override
        public Stream<Turnstile> options(CommandContext context) {
            return TurnstileManager
                    .instance()
                    .allTurnstilesForPlayer(context.getPlayer())
                    .stream();
        }

        @Override
        public String toString(Turnstile value) {
            return value.name();
        }
    }

    public static class EconomyPriceArgument extends AbstractHighLevelArg<EconomyPrice> {

        public EconomyPriceArgument() {
            super(new Arguments.DoubleArgument());
        }

        @Override
        public EconomyPrice value() {
            return new EconomyPrice(contents[0].asDouble());
        }
    }

    public static class ItemPriceArgument extends AbstractHighLevelArg<ItemPrice> {
        public ItemPriceArgument() {
            super(new Arguments.ItemStackArgument());
        }

        @Override
        public ItemPrice value() {
            return new ItemPrice(contents[0].as(ItemStack.class));
        }
    }

    public static class TicketArgument extends AbstractHighLevelArg<TicketPrice> {

        public TicketArgument() {
            super(new Arguments.ItemStackArgument());
        }

        @Override
        public TicketPrice value() {
            return new TicketPrice(contents[0].as(ItemStack.class));
        }
    }

    public static class LinkableArgument extends AbstractContextualArgument<Linkable> {

        @Override
        public Linkable fromContext(CommandContext context) throws ArgumentNotResolvable {
            final Player player = context.getPlayer();
            final BlockIterator blockIterator = new BlockIterator(player.getLocation(), 1.5, 5);
            while (blockIterator.hasNext()) {
                final Block block = blockIterator.next();
                if (Tag.BUTTONS.isTagged(block.getType())) {
                    return new ButtonActivator(block);
                } else if (CustomTags.PRESSURE_PLATES.isTagged(block.getType())) {
                    return new PressurePlateActivator(block);
                } else if (Tag.SIGNS.isTagged(block.getType())) {
                    return new SignDisplay(block);
                }
                if (block.getType().isSolid()) { break; }
            }
            throw ArgumentNotResolvable.withMessage("");
        }
    }
}
