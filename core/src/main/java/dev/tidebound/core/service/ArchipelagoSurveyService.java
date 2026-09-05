package dev.tidebound.core.service;

import dev.tidebound.core.world.ArchipelagoSurvey;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.levelgen.Heightmap;

/** Explicit, administrator-triggered scan; it never runs every tick. */
public final class ArchipelagoSurveyService {
    private static final int SAMPLE_STEP = 8;

    private ArchipelagoSurveyService() {
    }

    public static ArchipelagoSurvey survey(ServerLevel level, BlockPos center, int radius) {
        int seaLevel = level.getSeaLevel();
        int total = 0;
        int land = 0;
        int water = 0;
        int shore = 0;
        int logs = 0;

        for (int x = center.getX() - radius; x <= center.getX() + radius; x += SAMPLE_STEP) {
            for (int z = center.getZ() - radius; z <= center.getZ() + radius; z += SAMPLE_STEP) {
                total++;
                int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
                BlockPos top = new BlockPos(x, surfaceY - 1, z);
                boolean isWater = level.getFluidState(top).is(FluidTags.WATER);
                boolean isLand = !isWater && surfaceY > seaLevel;
                if (isLand) {
                    land++;
                    if (touchesWater(level, x, z, seaLevel)) {
                        shore++;
                    }
                    int bottom = Math.max(level.getMinBuildHeight(), surfaceY - 18);
                    for (int y = bottom; y < surfaceY + 6; y++) {
                        if (level.getBlockState(new BlockPos(x, y, z)).is(BlockTags.LOGS)) {
                            logs++;
                            break;
                        }
                    }
                } else if (isWater || surfaceY <= seaLevel) {
                    water++;
                }
            }
        }
        return new ArchipelagoSurvey(total, land, water, shore, logs);
    }

    private static boolean touchesWater(ServerLevel level, int x, int z, int seaLevel) {
        return level.getFluidState(new BlockPos(x + SAMPLE_STEP, seaLevel, z)).is(FluidTags.WATER)
                || level.getFluidState(new BlockPos(x - SAMPLE_STEP, seaLevel, z)).is(FluidTags.WATER)
                || level.getFluidState(new BlockPos(x, seaLevel, z + SAMPLE_STEP)).is(FluidTags.WATER)
                || level.getFluidState(new BlockPos(x, seaLevel, z - SAMPLE_STEP)).is(FluidTags.WATER);
    }
}
