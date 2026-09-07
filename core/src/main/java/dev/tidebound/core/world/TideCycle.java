package dev.tidebound.core.world;

/**
 * Simplified tide: no water level changes, just a slow rhythm gating ambiance-driven bonuses.
 * Chosen over a real dynamic water-level simulation (never attempted in this project, higher risk
 * of client/server desync) per the Marais des Lanternes design decision.
 */
public final class TideCycle {
    private static final long PERIOD_TICKS = 48_000L;

    private TideCycle() {
    }

    public static boolean isLowTide(long dayTime) {
        return Math.floorMod(dayTime, PERIOD_TICKS) < PERIOD_TICKS / 2;
    }
}
