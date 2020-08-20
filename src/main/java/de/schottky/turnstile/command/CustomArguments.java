package de.schottky.turnstile.command;

import com.github.schottky.zener.command.CommandContext;
import com.github.schottky.zener.command.resolver.ArgumentNotResolvable;
import com.github.schottky.zener.command.resolver.ArgumentResolver;
import com.github.schottky.zener.command.resolver.CommandException;
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
import de.schottky.turnstile.metadata.MetadataKeys;
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
        ArgumentResolver.registerArgument(Block.class, BlockArgument::new);
    }

    public static class TurnstileArgument extends AbstractLowLevelArg<Turnstile> {

        public TurnstileArgument() {
            this.description = "turnstile";
        }

        @Override
        protected Turnstile fromArgument(String arg, CommandContext context) throws CommandException {
            return TurnstileManager.instance().forName(arg, context.getPlayer())
                    .orElseThrow(() -> ArgumentNotResolvable.withMessage("You do not own a turnstile by that name"));
        }

        @Override
        public Stream<Turnstile> options(CommandContext context) throws CommandException {
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
            super(new Arguments.DoubleArgument().withDescription("price"));
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
        public Linkable fromContext(CommandContext context) throws CommandException {
            final Player player = context.getPlayer();
            final BlockIterator blockIterator = new BlockIterator(player.getLocation(), 1.5, 5);
            while (blockIterator.hasNext()) {
                final Block block = blockIterator.next();
                for (String metadata: MetadataKeys.allKeys()) {
                    if (block.hasMetadata(metadata)) {
                        final Object value = block.getMetadata(metadata).get(0);
                        if (value instanceof Linkable)
                            return (Linkable) value;
                    }
                }
                if (Tag.BUTTONS.isTagged(block.getType())) {
                    return new ButtonActivator(block);
                } else if (CustomTags.PRESSURE_PLATES.isTagged(block.getType())) {
                    return new PressurePlateActivator(block);
                } else if (Tag.SIGNS.isTagged(block.getType())) {
                    return new SignDisplay(block);
                }
                if (block.getType().isSolid()) { break; }
            }
            throw ArgumentNotResolvable.withMessage("This cannot be linked");
        }

    }

    public static class BlockArgument extends AbstractContextualArgument<Block> {

        @Override
        public Block fromContext(CommandContext context) throws CommandException {
            final Block block = context.getPlayer().getTargetBlock(null, 20);
            if (block.getType().isAir())
                throw ArgumentNotResolvable.withMessage("You are not looking at a block!");
            return block;
        }
    }
}
