package dev.tidebound.core.service;

import dev.tidebound.core.world.WreckPlan;
import dev.tidebound.core.world.WreckRegistry;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.loot.LootTable;

/**
 * Bounded procedural shipwreck skeleton, resting on the open ocean floor — same raw-block-placement
 * discipline as {@link HarborPlacementService}, no external structure template. First pass for the
 * "Plateau des Épaves" region: a modest broken hull, not the large rare shipwreck previously deferred
 * for lack of a {@code .nbt} decision.
 */
public final class WreckPlacementService {
    private static final int SEARCH_RADIUS = 64;
    private static final int MIN_WATER_DEPTH = 4;
    private static final ResourceKey<LootTable> LOOT_TABLE = ResourceKey.create(
            net.minecraft.core.registries.Registries.LOOT_TABLE,
            ResourceLocation.fromNamespaceAndPath("tidebound", "chests/shipwreck_plateau"));

    private WreckPlacementService() {
    }

    public static boolean placeNear(ServerLevel level, BlockPos requestedOrigin, WreckPlan plan) {
        BlockPos seabed = findSeabed(level, requestedOrigin).orElse(null);
        if (seabed == null) {
            return false;
        }
        buildWreck(level, seabed, plan.siteId());
        WreckRegistry.get(level).register(plan.siteId());
        return true;
    }

    private static Optional<BlockPos> findSeabed(ServerLevel level, BlockPos origin) {
        int seaY = level.getSeaLevel();
        for (int dx = -SEARCH_RADIUS; dx <= SEARCH_RADIUS; dx += 4) {
            for (int dz = -SEARCH_RADIUS; dz <= SEARCH_RADIUS; dz += 4) {
                int x = origin.getX() + dx;
                int z = origin.getZ() + dz;
                if (!level.getBiome(new BlockPos(x, seaY, z)).is(BiomeTags.IS_OCEAN)) {
                    continue;
                }
                int floorY = level.getHeight(Heightmap.Types.OCEAN_FLOOR, x, z);
                if (seaY - floorY < MIN_WATER_DEPTH) {
                    continue;
                }
                BlockPos floor = new BlockPos(x, floorY, z);
                if (!level.getFluidState(floor.above()).is(FluidTags.WATER)) {
                    continue;
                }
                return Optional.of(floor);
            }
        }
        return Optional.empty();
    }

    private static void buildWreck(ServerLevel level, BlockPos floor, long siteId) {
        RandomSource random = RandomSource.create(siteId);
        Direction axis = Direction.Plane.HORIZONTAL.stream()
                .toList().get(random.nextInt(4));
        Direction across = axis.getClockWise();
        int length = 9;

        for (int i = 0; i < length; i++) {
            BlockPos base = floor.relative(axis, i);
            if (random.nextFloat() < 0.8F) {
                level.setBlock(base, Blocks.OAK_PLANKS.defaultBlockState(), 3);
            } else {
                level.setBlock(base, Blocks.GRAVEL.defaultBlockState(), 3);
            }

            for (int side = -1; side <= 1; side += 2) {
                BlockPos wallBase = base.relative(across, side);
                int wallHeight = random.nextInt(3);
                for (int y = 1; y <= wallHeight; y++) {
                    BlockState wall = (y == wallHeight && random.nextBoolean())
                            ? Blocks.OAK_LOG.defaultBlockState()
                            : Blocks.OAK_PLANKS.defaultBlockState();
                    level.setBlock(wallBase.above(y), wall, 3);
                }
            }
        }

        BlockPos barrelPos = floor.relative(axis, length / 2).above();
        level.setBlock(barrelPos, Blocks.BARREL.defaultBlockState(), 3);
        RandomizableContainer.setBlockEntityLootTable(level, random, barrelPos, LOOT_TABLE);

        BlockPos hatch = floor.relative(axis, 2).above();
        level.setBlock(hatch, Blocks.OAK_TRAPDOOR.defaultBlockState(), 3);

        Direction mastDirection = random.nextBoolean() ? across : across.getOpposite();
        BlockPos mastOrigin = floor.relative(axis, length - 2).relative(across, 2);
        for (int i = 0; i < 5; i++) {
            level.setBlock(mastOrigin.relative(mastDirection, i), Blocks.OAK_LOG.defaultBlockState(), 3);
        }
    }
}
