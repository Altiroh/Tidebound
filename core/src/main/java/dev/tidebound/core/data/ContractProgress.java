package dev.tidebound.core.data;

public record ContractProgress(int completionCount, long nextAvailableAt) {
    public static final int MAX_COMPLETIONS = 1_000_000_000;

    public ContractProgress {
        if (completionCount < 0 || completionCount > MAX_COMPLETIONS) {
            throw new IllegalArgumentException("Invalid contract completion count");
        }
        if (nextAvailableAt < 0) {
            throw new IllegalArgumentException("Contract availability time cannot be negative");
        }
    }

    public static ContractProgress fresh() {
        return new ContractProgress(0, 0);
    }

    public boolean isAvailable(long gameTime) {
        return gameTime >= nextAvailableAt;
    }

    public ContractProgress complete(long gameTime, long cooldownTicks) {
        if (!isAvailable(gameTime)) {
            throw new IllegalStateException("Contract is on cooldown");
        }
        if (cooldownTicks < 0) {
            throw new IllegalArgumentException("Cooldown cannot be negative");
        }
        if (completionCount == MAX_COMPLETIONS) {
            throw new IllegalStateException("Contract completion limit reached");
        }
        if (cooldownTicks > Long.MAX_VALUE - gameTime) {
            throw new IllegalArgumentException("Cooldown is too large");
        }
        return new ContractProgress(completionCount + 1, gameTime + cooldownTicks);
    }
}
