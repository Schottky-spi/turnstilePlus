package de.schottky.turnstile.metadata;

import de.schottky.turnstile.TurnstilePlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MetadataKeys {

    private static final List<String> allKeys = new ArrayList<>();

    public static String create(String metadataKey) {
        final String finalKey = TurnstilePlugin.instance().getName() + ":" + metadataKey;
        allKeys.add(finalKey);
        return finalKey;
    }

    public static List<String> getAllKeys() {
        return Collections.unmodifiableList(allKeys);
    }
}
