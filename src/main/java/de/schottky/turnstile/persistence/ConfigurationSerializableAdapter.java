package de.schottky.turnstile.persistence;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.reflect.TypeToken;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ConfigurationSerializableAdapter implements JsonCoder<ConfigurationSerializable> {

    @Override
    public ConfigurationSerializable deserialize(
            JsonElement json,
            Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException
    {
        Map<String,Object> map = context.deserialize(json, new TypeToken<Map<String,Object>>() {}.getType());
        return ConfigurationSerialization.deserializeObject(map);
    }

    @Override
    public JsonElement serialize(
            ConfigurationSerializable src,
            Type typeOfSrc,
            JsonSerializationContext context)
    {
        final Map<String,Object> map = new HashMap<>(src.serialize());
        map.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY,ConfigurationSerialization.getAlias(src.getClass()));
        return context.serialize(map, new TypeToken<Map<String,Object>>(){}.getType());
    }
}
