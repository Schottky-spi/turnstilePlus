package de.schottky.turnstile.persistence;

import com.github.schottky.zener.messaging.Console;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;

import java.io.IOException;
import java.lang.reflect.Type;

public class TypeAdapters {

    public static AppendingClassAdapter appendingClassAdapter = new AppendingClassAdapter();

    public static class AppendingClassAdapter implements JsonCoder<Object> {

        @Override
        public Object deserialize(
                JsonElement json,
                Type typeOfT,
                JsonDeserializationContext context) throws JsonParseException
        {
            JsonObject object = json.getAsJsonObject();
            if (object.has("class")) {
                final String clazzName = object.get("class").getAsString();
                final Class<?> clazz = forName(clazzName);
                if (clazz == null) {
                    Console.warning("Null class found, skipping class " + clazzName);
                    return null;
                }else
                    return context.deserialize(object, clazz);
            } else {
                return null;
            }
        }

        @Override
        public JsonElement serialize(
                Object src,
                Type typeOfSrc,
                JsonSerializationContext context)
        {
            final JsonObject object = context.serialize(src, src.getClass()).getAsJsonObject();
            if (object == null)
                return null;
            else {
                object.addProperty("class", src.getClass().getName());
                return object;
            }
        }
    }

    static Class<?> forName(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            Console.error(e);
            return null;
        }
    }

    public static TypeAdapter<BlockData> blockDataTypeAdapter = new TypeAdapter<BlockData>() {

        @Override
        public void write(JsonWriter out, BlockData value) throws IOException {
            out.value(value.getAsString());
        }

        @Override
        public BlockData read(JsonReader in) throws IOException {
            return Bukkit.createBlockData(in.nextString());
        }
    }.nullSafe();

    public static Object locationTypeAdapter = new LocationTypeAdapter();
}
