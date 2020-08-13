package de.schottky.turnstile.persistence;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

public interface JsonCoder<T> extends JsonSerializer<T>, JsonDeserializer<T> {
}
