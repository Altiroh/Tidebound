package dev.tidebound.core.data;

/**
 * Immutable wallet for Tidebound's maritime currency.
 *
 * <p>The wallet lives on the server. Replacing the complete value on every mutation makes
 * persistence explicit and avoids dirty-state bugs in attachment holders.</p>
 */
public record TideWallet(long balance) {
    public static final long MAX_BALANCE = 1_000_000_000_000L;

    public TideWallet {
        if (balance < 0 || balance > MAX_BALANCE) {
            throw new IllegalArgumentException("Tide balance must be between 0 and " + MAX_BALANCE);
        }
    }

    public static TideWallet empty() {
        return new TideWallet(0);
    }

    public TideWallet credit(long amount) {
        requirePositive(amount);
        if (amount > MAX_BALANCE - balance) {
            throw new IllegalArgumentException("Tide balance limit exceeded");
        }
        return new TideWallet(balance + amount);
    }

    public TideWallet debit(long amount) {
        requirePositive(amount);
        if (amount > balance) {
            throw new IllegalStateException("Insufficient Tide balance");
        }
        return new TideWallet(balance - amount);
    }

    public boolean canAfford(long amount) {
        return amount > 0 && amount <= balance;
    }

    private static void requirePositive(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Tide amount must be positive");
        }
    }
}
