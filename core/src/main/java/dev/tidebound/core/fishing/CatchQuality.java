package dev.tidebound.core.fishing;

import java.util.Arrays;
import java.util.Locale;

/** Stable quality bands stored on a caught fish. */
public enum CatchQuality {
    COMMON("common", 1_000),
    FINE("fine", 1_250),
    EXCEPTIONAL("exceptional", 1_750),
    LEGENDARY("legendary", 2_500);

    private final String id;
    private final int valuePermille;

    CatchQuality(String id, int valuePermille) {
        this.id = id;
        this.valuePermille = valuePermille;
    }

    public String id() {
        return id;
    }

    public int valuePermille() {
        return valuePermille;
    }

    public static CatchQuality fromId(String value) {
        String normalized = value == null ? "" : value.strip().toLowerCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(quality -> quality.id.equals(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown catch quality: " + value));
    }
}
