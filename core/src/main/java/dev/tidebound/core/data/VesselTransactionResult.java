package dev.tidebound.core.data;

import java.util.Objects;

/** User-facing result for an upgrade or repair transaction. */
public record VesselTransactionResult(boolean success, String code, String message) {
    public VesselTransactionResult {
        code = Objects.requireNonNull(code, "code");
        message = Objects.requireNonNull(message, "message");
    }

    public static VesselTransactionResult completed(String code, String message) {
        return new VesselTransactionResult(true, code, message);
    }

    public static VesselTransactionResult failure(String code, String message) {
        return new VesselTransactionResult(false, code, message);
    }
}
