package de.schottky.turnstile.persistence;

import com.github.schottky.zener.messaging.Console;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.schottky.turnstile.Turnstile;
import de.schottky.turnstile.TurnstileManager;
import de.schottky.turnstile.TurnstilePart;
import de.schottky.turnstile.TurnstilePlugin;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

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
                .registerTypeAdapter(Turnstile.class, TypeAdapters.appendingClassAdapter)
                .registerTypeAdapter(TurnstilePart.class, TypeAdapters.appendingClassAdapter)
                .registerTypeHierarchyAdapter(BlockData.class, TypeAdapters.blockDataTypeAdapter)
                .create();
    }

    public static void saveAll() {
        final Multimap<UUID,Turnstile> playerTurnstiles = TurnstileManager.retrieveTurnstileData();
        for (UUID uuid: playerTurnstiles.keySet()) {
            save(uuid, playerTurnstiles.get(uuid));
        }
    }

    public static void save(UUID uuid, Collection<Turnstile> turnstile) {
        final File file = getUUIDFile(uuid);
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
            final UUID uuid = UUID.fromString(fileName.substring(0, fileName.length() - ".json".length()));
            try {
                final FileReader reader = new FileReader(file);
                final Turnstile[] turnstiles = gson.fromJson(reader, Turnstile[].class);
                playerTurnstiles.putAll(uuid, Arrays.asList(turnstiles));
            } catch (FileNotFoundException e) {
                Console.error(e);
            }
        }
        TurnstileManager.loadTurnstileData(playerTurnstiles);
    }

    private static File getUUIDFile(UUID uuid) {
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
}