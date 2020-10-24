package de.schottky.turnstile.persistence;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

/**
 * Interface that combines both {@link JsonSerializer} and
 * {@link JsonDeserializer} of the same type
 * @param <T> The type to serialize and deserialize
 */

public interface JsonCoder<T> extends JsonSerializer<T>, JsonDeserializer<T> { }
