package de.schottky.turnstile.command;

import com.github.schottky.zener.command.CommandContext;
import com.github.schottky.zener.command.resolver.ArgumentNotResolvable;
import com.github.schottky.zener.command.resolver.ArgumentResolver;
import com.github.schottky.zener.command.resolver.CommandException;
import com.github.schottky.zener.command.resolver.argument.AbstractContextualArgument;
import com.github.schottky.zener.command.resolver.argument.AbstractHighLevelArg;
import com.github.schottky.zener.command.resolver.argument.AbstractHighLevelVarArg;
import com.github.schottky.zener.command.resolver.argument.ArgumentBuilder;
import com.github.schottky.zener.localization.I18n;
import de.schottky.turnstile.Linkable;
import de.schottky.turnstile.Turnstile;
import de.schottky.turnstile.TurnstileEditMode;
import de.schottky.turnstile.TurnstileManager;
import de.schottky.turnstile.activator.ButtonActivator;
import de.schottky.turnstile.activator.PressurePlateActivator;
import de.schottky.turnstile.display.SignDisplay;
import de.schottky.turnstile.economy.EconomyPrice;
import de.schottky.turnstile.economy.ItemPrice;
import de.schottky.turnstile.economy.TicketPrice;
import de.schottky.turnstile.metadata.MetadataKeys;
import de.schottky.turnstile.tag.CustomTags;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;

public class CustomArguments {

    public static void registerAll() {
        ArgumentResolver.registerArgument(Turnstile.class, TurnstileArgument::new);
        ArgumentResolver.registerArgument(EconomyPrice.class, EconomyPriceArgument::new);
        ArgumentResolver.registerArgument(ItemPrice.class, ItemPriceArgument::new);
        ArgumentResolver.registerArgument(TicketPrice.class, TicketArgument::new);
        ArgumentResolver.registerArgument(Linkable.class, LinkableArgument::new);
        ArgumentResolver.registerArgument(Block.class, BlockArgument::new);
        ArgumentResolver.registerArgument(ItemStack.class, NonVariableItemStackArgument::new);
    }

    public static class TurnstileArgument extends AbstractHighLevelVarArg<Turnstile> {

        @Override
        public int minArgs() {
            return 0;
        }

        @Override
        public int maxArgs() {
            return 2;
        }

        public TurnstileArgument(CommandContext context) {
            super(context,
                    ArgumentBuilder.of(String.class)
                            .options(TurnstileManager
                                    .instance()
                                    .allTurnstilesForPlayer(context.getPlayer())
                                    .stream()
                                    .map(Turnstile::name))
                            .description(TurnstileEditMode.isEditing(context.getPlayer()) ? null : "turnstile")
                            .initialValue(TurnstileEditMode.forPlayer(context.getPlayer()).orElse(null))
                            .setOptional(false),
                    ArgumentBuilder.of(Player.class)
                            .requirePermission("turnstile.edit_others")
                            .description(TurnstileEditMode.isEditing(context.getPlayer()) ? null : "owner")
                            .initialValue(context.getPlayer())
                            .setOptional(true));
        }

        @Override
        public Turnstile value() {
            final String name = contents[0].as(String.class);
            final Player player = contents[1].as(Player.class);

            if (name == null) {
                return TurnstileEditMode.forPlayer(context.getPlayer())
                        .orElseThrow(() -> ArgumentNotResolvable.withMessage(I18n.of("message.missing_name")));
            } else if (player == null) {
                return TurnstileManager
                        .instance()
                        .forName(contents[0].as(String.class), context.getPlayer())
                        .orElseThrow(() -> ArgumentNotResolvable.withMessage(I18n.of("message.turnstile_not_owned")));
            } else {
                return TurnstileManager
                        .instance()
                        .forName(name, player)
                        .orElseThrow(() -> new CommandException(
                                I18n.of("message.player_does_not_own_turnstile", "player", player)));
            }
        }
    }

    public static class EconomyPriceArgument extends AbstractHighLevelArg<EconomyPrice> {

        public EconomyPriceArgument(CommandContext context) {
            super(context, ArgumentBuilder.Double().description("price").options(1, 10, 100));
        }

        @Override
        public EconomyPrice value() {
            return new EconomyPrice(contents[0].asDouble());
        }
    }

    public static class ItemPriceArgument extends AbstractHighLevelArg<ItemPrice> {
        public ItemPriceArgument(CommandContext context) {
            super(context,
                    new NonVariableItemStackArgument(context));
        }

        @Override
        public ItemPrice value() {
            return new ItemPrice(contents[0].as(ItemStack.class));
        }
    }

    public static class TicketArgument extends AbstractHighLevelArg<TicketPrice> {

        public TicketArgument(CommandContext context) {
            super(context, new NonVariableItemStackArgument(context));
        }

        @Override
        public TicketPrice value() {
            return new TicketPrice(contents[0].as(ItemStack.class));
        }
    }

    public static class NonVariableItemStackArgument extends AbstractHighLevelArg<ItemStack> {

        public NonVariableItemStackArgument(CommandContext context) {
            super(context,
                    ArgumentBuilder.of(Material.class)
                            .description("type"),
                    ArgumentBuilder.of(Integer.class)
                            .options(1, 32, 64)
                            .description("amount"));
        }

        @Override
        public ItemStack value() {
            return new ItemStack(contents[0].as(Material.class), contents[1].asInt());
        }
    }

    public static class LinkableArgument extends AbstractContextualArgument<Linkable> {

        public LinkableArgument(CommandContext context) {
            super(context);
        }

        @Override
        public Linkable fromContext() throws CommandException {
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
            throw ArgumentNotResolvable.withMessage(I18n.of("message.not_linkable"));
        }

    }

    public static class BlockArgument extends AbstractContextualArgument<Block> {

        public BlockArgument(CommandContext context) {
            super(context);
        }

        @Override
        public Block fromContext() throws CommandException {
            final Block block = context.getPlayer().getTargetBlock(null, 20);
            if (block.getType().isAir())
                throw ArgumentNotResolvable.withMessage(I18n.of("command.not_looking_at_block"));
            return block;
        }
    }
}
