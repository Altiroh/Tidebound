package dev.tidebound.core.navigation;

/** Dependency-free direction and distance calculations used by the Wake Compass. */
public final class WakeBearing {
    private static final String[] DIRECTIONS = {
            "nord", "nord-est", "est", "sud-est",
            "sud", "sud-ouest", "ouest", "nord-ouest"
    };

    private WakeBearing() {
    }

    public static String direction(double deltaX, double deltaZ) {
        if (Math.abs(deltaX) < 0.0001 && Math.abs(deltaZ) < 0.0001) {
            return "ici";
        }
        double angle = Math.atan2(deltaX, -deltaZ);
        int index = Math.floorMod((int) Math.round(angle / (Math.PI / 4.0)), DIRECTIONS.length);
        return DIRECTIONS[index];
    }

    public static int distance(double deltaX, double deltaZ) {
        return (int) Math.round(Math.hypot(deltaX, deltaZ));
    }
}
