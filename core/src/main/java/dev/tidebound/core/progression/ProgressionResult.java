package dev.tidebound.core.progression;

import java.util.Objects;

public record ProgressionResult(ProgressionStatus status, String message) {
    public ProgressionResult {
        status = Objects.requireNonNull(status, "status");
        message = Objects.requireNonNull(message, "message");
    }

    public boolean success() {
        return status == ProgressionStatus.COMPLETED;
    }

    public static ProgressionResult completed(String message) {
        return new ProgressionResult(ProgressionStatus.COMPLETED, message);
    }

    public static ProgressionResult failure(ProgressionStatus status, String message) {
        if (status == ProgressionStatus.COMPLETED) {
            throw new IllegalArgumentException("Use completed() for successful results");
        }
        return new ProgressionResult(status, message);
    }
}
