package de.schottky.turnstile.persistence;

import com.google.gson.*;
import de.schottky.turnstile.activator.TurnstileActivator;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class TurnstileActivatorSetAdapter implements JsonCoder<Set<TurnstileActivator>> {

    @Override
    public Set<TurnstileActivator> deserialize(
            JsonElement json,
            Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException
    {
        final JsonArray array = json.getAsJsonArray();
        final Set<TurnstileActivator> activators = new HashSet<>();
        for (JsonElement element: array) {
            activators.add(context.deserialize(element, TurnstileActivator.class));
        }
        return activators;
    }

    @Override
    public JsonElement serialize(
            Set<TurnstileActivator> set,
            Type typeOfSrc,
            JsonSerializationContext context)
    {
        return context.serialize(set.toArray(new TurnstileActivator[0]));
    }
}
