package de.schottky.turnstile.persistence;

import com.google.common.collect.Multimap;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Collection;

public class MultimapAdapter implements JsonCoder<Multimap<Object,Object>> {

    @Override
    public Multimap<Object,Object> deserialize(
            JsonElement json,
            Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException
    {
        return null;
    }

    @Override
    public JsonElement serialize(
            Multimap<Object,Object> src,
            Type typeOfSrc,
            JsonSerializationContext context)
    {
        final JsonObject object = new JsonObject();
        for (Object key: src.keySet()) {
            final Collection<?> values = src.get(key);
            final JsonElement element = context.serialize(values);
        }
        return null;
    }
}
