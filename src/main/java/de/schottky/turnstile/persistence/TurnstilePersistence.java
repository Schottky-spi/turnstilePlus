package de.schottky.turnstile.persistence;

import com.github.schottky.zener.messaging.Console;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.schottky.turnstile.*;
import de.schottky.turnstile.activator.TurnstileActivator;
import de.schottky.turnstile.display.TurnstileInformationDisplay;
import de.schottky.turnstile.economy.ItemPrice;
import de.schottky.turnstile.economy.Price;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;

public final class TurnstilePersistence {

    private TurnstilePersistence() {}

    private static final File dataFolder;
    private static final Gson gson;

    static {
        dataFolder = new File(JavaPlugin.getPlugin(TurnstilePlugin.class).getDataFolder(), "data");
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            Console.severe("Cannot save data as file-creation of data-folder was unsuccessful (unknown reason)");
        }
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .registerTypeAdapter(Location.class, TypeAdapters.locationTypeAdapter)
                .registerTypeAdapter(Price.class, TypeAdapters.appendingClassAdapter)
                .registerTypeAdapter(Turnstile.class, TypeAdapters.appendingClassAdapter)
                .registerTypeAdapter(TurnstilePart.class, TypeAdapters.appendingClassAdapter)
                .registerTypeAdapter(Linkable.class, TypeAdapters.appendingClassAdapter)
                .registerTypeHierarchyAdapter(BlockData.class, TypeAdapters.blockDataTypeAdapter)
                .registerTypeHierarchyAdapter(ConfigurationSerializable.class, new ConfigurationSerializableAdapter())
                .create();
    }

    public static void saveAll() {
        final Multimap<UUID,Turnstile> playerTurnstiles = TurnstileManager.instance().retrieveTurnstileData();
        for (UUID uuid: playerTurnstiles.keySet()) {
            save(uuid, playerTurnstiles.get(uuid));
        }
        savePendingStacks(ItemPrice.save());
    }

    public static void saveAllAsyncFor(UUID uuid) {
        saveAsync(uuid, TurnstileManager.instance().allTurnstilesForPlayer(uuid));
    }

    public static void saveAsync(UUID uuid, Collection<Turnstile> turnstiles) {
        Executors.newSingleThreadExecutor().submit(() -> save(uuid, turnstiles));
    }

    // Use the async alternative
    @Deprecated
    public static void save(UUID uuid, Collection<Turnstile> turnstile) {
        final File file = getPlayerFile(uuid);
        final String json = gson.toJson(turnstile.toArray(new Turnstile[0]));
        try {
            final FileWriter writer = new FileWriter(file);
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            Console.error(e);
        }
    }

    public static void loadAll() {
        final Multimap<UUID,Turnstile> playerTurnstiles = HashMultimap.create();
        for (File file: dataFolder.listFiles((dir, name) -> name.endsWith(".json"))) {
            final String fileName = file.getName();
            UUID uuid;
            try {
                uuid = UUID.fromString(fileName.substring(0, fileName.length() - ".json".length()));
            } catch (IllegalArgumentException ignored) {
                continue;
            }
            try {
                final FileReader reader = new FileReader(file);
                final Turnstile[] turnstiles = gson.fromJson(reader, Turnstile[].class);
                playerTurnstiles.putAll(uuid, Arrays.asList(turnstiles));
            } catch (FileNotFoundException e) {
                Console.error(e);
            }
        }
        TurnstileManager.instance().loadTurnstileData(playerTurnstiles);
        ItemPrice.loadAll(loadPendingStacks());
    }

    private static File getPlayerFile(UUID uuid) {
        final File file = new File(dataFolder, uuid.toString() + ".json");
        if (!file.exists()) {
            try {
                if (!file.createNewFile())
                    Console.severe("Cannot create player file for UUID %s (unknown reason)", uuid.toString());
            } catch (IOException e) {
                Console.error(e);
            }
        }
        return file;
    }

    public static Multimap<UUID,ItemStack> loadPendingStacks() {
        final File file = new File(dataFolder, "pendingStacks.json");
        if (!file.exists())
            return ImmutableMultimap.of();
        try {
            final FileReader reader = new FileReader(file);
            final Map<UUID,Collection<ItemStack>> map = gson.fromJson(reader,
                    new TypeToken<Map<UUID,Collection<ItemStack>>>() {}.getType());
            return map == null ? ImmutableMultimap.of() : fromMap(map);
        } catch (FileNotFoundException e) {
            Console.error(e);
            return ImmutableMultimap.of();
        }
    }

    private static Multimap<UUID,ItemStack> fromMap(Map<UUID,Collection<ItemStack>> map) {
        ImmutableMultimap.Builder<UUID,ItemStack> builder = ImmutableMultimap.builder();
        for (UUID uuid: map.keySet()) {
            builder.putAll(uuid, map.get(uuid));
        }
        return builder.build();
    }

    public static void savePendingStackAsync(Multimap<UUID,ItemStack> pending) {
        Executors.newSingleThreadExecutor().submit(() -> savePendingStacks(pending));
    }

    // Use the async alternative
    @Deprecated
    public static void savePendingStacks(Multimap<UUID,ItemStack> pending) {
        final File file = new File(dataFolder, "pendingStacks.json");
        try {
            if (!file.exists() && !file.createNewFile()) {
                Console.severe("Cannot create file for pending items (unknown reason)");
                return;
            }
        } catch (IOException e) {
            Console.error(e);
            return;
        }
        try {
            final FileWriter writer = new FileWriter(file);
            writer.write(gson.toJson(pending.asMap(),
                    new TypeToken<Map<UUID,Collection<ItemStack>>>(){}.getType()));
            writer.close();
        } catch (IOException e) {
            Console.error(e);
        }
    }
}
