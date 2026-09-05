package dev.tidebound.core.fishing;

import java.util.Arrays;
import java.util.Locale;

/** Rare visible traits. They hint at the sea without turning every catch into horror. */
public enum CatchAnomaly {
    NONE("none", 1_000),
    ASHEN("ashen", 1_500),
    HOLLOW_EYED("hollow_eyed", 2_200),
    INK_VEINED("ink_veined", 3_000);

    private final String id;
    private final int valuePermille;

    CatchAnomaly(String id, int valuePermille) {
        this.id = id;
        this.valuePermille = valuePermille;
    }

    public String id() {
        return id;
    }

    public int valuePermille() {
        return valuePermille;
    }

    public static CatchAnomaly fromId(String value) {
        String normalized = value == null ? "" : value.strip().toLowerCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(anomaly -> anomaly.id.equals(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown catch anomaly: " + value));
    }
}
