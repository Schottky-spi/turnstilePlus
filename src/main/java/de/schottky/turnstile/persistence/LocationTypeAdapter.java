package de.schottky.turnstile.persistence;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.lang.reflect.Type;
import java.util.UUID;

public class LocationTypeAdapter implements JsonSerializer<Location>, JsonDeserializer<Location> {

    @Override
    public Location deserialize(
            JsonElement json,
            Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException
    {
        final JsonObject obj = json.getAsJsonObject();
        final UUID worldUUID = UUID.fromString(obj.get("world").getAsString());
        final Location location = new Location(
                Bukkit.getWorld(worldUUID),
                obj.get("x").getAsDouble(),
                obj.get("y").getAsDouble(),
                obj.get("z").getAsDouble());
        if (obj.has("pitch"))
            location.setPitch(obj.get("pitch").getAsFloat());
        if (obj.has("yaw"))
            location.setYaw(obj.get("yaw").getAsFloat());
        return location;
    }


    @Override
    public JsonElement serialize(Location src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        obj.addProperty("x", src.getX());
        obj.addProperty("y", src.getY());
        obj.addProperty("z", src.getZ());
        if (src.getWorld() == null)
            obj.addProperty("world", (String) null);
        else
            obj.addProperty("world", src.getWorld().getUID().toString());
        obj.addProperty("pitch", src.getPitch());
        obj.addProperty("yaw", src.getYaw());
        return obj;
    }
}
