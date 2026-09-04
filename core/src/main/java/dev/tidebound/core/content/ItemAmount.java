package dev.tidebound.core.content;

import java.util.Objects;

public record ItemAmount(String itemId, int count) {
    public ItemAmount {
        itemId = Objects.requireNonNull(itemId, "itemId").strip().toLowerCase(java.util.Locale.ROOT);
        if (itemId.isBlank() || !itemId.matches("[a-z0-9_.-]+:[a-z0-9_./-]+")) {
            throw new IllegalArgumentException("Invalid item id: " + itemId);
        }
        if (count < 1 || count > 2_304) {
            throw new IllegalArgumentException("Item count must be between 1 and 2304");
        }
    }
}
