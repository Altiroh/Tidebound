package dev.tidebound.core.service;

import dev.tidebound.core.npc.PortNpcEntity;
import dev.tidebound.core.registry.TideboundEntities;
import dev.tidebound.core.world.PortPlan;
import dev.tidebound.core.world.PortService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

/** Builds the small runtime harbour prototype used before authored structure templates exist. */
public final class HarborPlacementService {
    private static final int SEARCH_RADIUS = 48;
    private static final String GENERATED_TAG = "tidebound_generated_port";

    private HarborPlacementService() {
    }

    public static PlacementResult placeNear(ServerLevel level, BlockPos requestedOrigin, PortPlan plan) {
        String siteTag = siteTag(plan.siteId());
        boolean alreadyPresent = !level.getEntitiesOfClass(
                PortNpcEntity.class,
                new AABB(requestedOrigin).inflate(96.0),
                npc -> npc.getTags().contains(siteTag)
        ).isEmpty();
        if (alreadyPresent) {
            return PlacementResult.failure("Ce site portuaire est déjà matérialisé à proximité.");
        }

        ShoreSite shore = findShore(level, requestedOrigin).orElse(null);
        if (shore == null) {
            return PlacementResult.failure("Aucun rivage exploitable trouvé dans un rayon de 48 blocs.");
        }

        buildPier(level, shore, plan);
        int spawned = spawnServices(level, shore, plan, siteTag);
        return new PlacementResult(true, shore.deckOrigin(), spawned,
                "Avant-poste " + plan.archetype().name() + " créé avec " + spawned + " PNJ.");
    }

    private static Optional<ShoreSite> findShore(ServerLevel level, BlockPos origin) {
        List<ShoreSite> candidates = new ArrayList<>();
        int seaY = level.getSeaLevel() - 1;
        for (int dx = -SEARCH_RADIUS; dx <= SEARCH_RADIUS; dx += 2) {
            for (int dz = -SEARCH_RADIUS; dz <= SEARCH_RADIUS; dz += 2) {
                int x = origin.getX() + dx;
                int z = origin.getZ() + dz;
                int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) - 1;
                BlockPos ground = new BlockPos(x, surfaceY, z);
                if (surfaceY < seaY - 2 || surfaceY > seaY + 4
                        || level.getFluidState(ground).is(FluidTags.WATER)
                        || !level.getBlockState(ground).isSolid()) {
                    continue;
                }
                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    BlockPos water = new BlockPos(
                            x + direction.getStepX() * 3, seaY, z + direction.getStepZ() * 3);
                    if (level.getFluidState(water).is(FluidTags.WATER)) {
                        int deckY = Math.max(surfaceY + 1, level.getSeaLevel());
                        double distance = ground.distSqr(origin);
                        candidates.add(new ShoreSite(new BlockPos(x, deckY, z), direction, distance));
                    }
                }
            }
        }
        return candidates.stream().min(Comparator.comparingDouble(ShoreSite::distance));
    }

    private static void buildPier(ServerLevel level, ShoreSite shore, PortPlan plan) {
        BlockPos origin = shore.deckOrigin();
        Direction water = shore.waterDirection();
        Direction across = water.getClockWise();

        for (int depth = -1; depth <= 6; depth++) {
            int halfWidth = depth <= 1 ? 3 : 2;
            for (int lateral = -halfWidth; lateral <= halfWidth; lateral++) {
                BlockPos deck = origin.relative(water, depth).relative(across, lateral);
                level.setBlock(deck, deckBlock(plan), 3);
            }
        }

        for (int depth : new int[] {1, 5}) {
            for (int lateral : new int[] {-2, 2}) {
                BlockPos post = origin.relative(water, depth).relative(across, lateral);
                for (int y = post.getY() - 1; y >= Math.max(level.getMinBuildHeight(), post.getY() - 6); y--) {
                    BlockPos support = new BlockPos(post.getX(), y, post.getZ());
                    if (level.getBlockState(support).isSolid()) {
                        break;
                    }
                    level.setBlock(support, Blocks.STRIPPED_OAK_LOG.defaultBlockState(), 3);
                }
                level.setBlock(post.above(), Blocks.OAK_FENCE.defaultBlockState(), 3);
                level.setBlock(post.above(2), Blocks.LANTERN.defaultBlockState(), 3);
            }
        }

        BlockPos serviceRow = origin.relative(water.getOpposite(), 1);
        if (plan.services().contains(PortService.STORAGE)) {
            level.setBlock(serviceRow.relative(across, -3), Blocks.BARREL.defaultBlockState(), 3);
            level.setBlock(serviceRow.relative(across, 3), Blocks.BARREL.defaultBlockState(), 3);
        }
        if (plan.services().contains(PortService.CONTRACT_BOARD)) {
            level.setBlock(serviceRow.relative(across, 2), Blocks.LECTERN.defaultBlockState(), 3);
        }
        if (plan.services().contains(PortService.INTENDANT)) {
            level.setBlock(serviceRow.relative(across, -2), Blocks.BELL.defaultBlockState(), 3);
        }
        if (plan.services().contains(PortService.CREATE_MECHANISM)) {
            level.setBlock(serviceRow.relative(across, 3), Blocks.COPPER_BLOCK.defaultBlockState(), 3);
        }
    }

    private static net.minecraft.world.level.block.state.BlockState deckBlock(PortPlan plan) {
        return switch (plan.archetype()) {
            case FISHING_HAMLET -> Blocks.OAK_PLANKS.defaultBlockState();
            case SHIPYARD_QUAY -> Blocks.SPRUCE_PLANKS.defaultBlockState();
            case LIGHTHOUSE_OUTPOST -> Blocks.STONE_BRICKS.defaultBlockState();
            case MARKET_HARBOR -> Blocks.DARK_OAK_PLANKS.defaultBlockState();
            case FIELD_STATION -> Blocks.MANGROVE_PLANKS.defaultBlockState();
        };
    }

    private static int spawnServices(ServerLevel level, ShoreSite shore, PortPlan plan, String siteTag) {
        Direction across = shore.waterDirection().getClockWise();
        List<PortService> npcServices = plan.services().stream()
                .filter(HarborPlacementService::isNpc)
                .sorted()
                .toList();
        int index = 0;
        for (PortService service : npcServices) {
            PortNpcEntity npc = entityType(service).get().create(level);
            if (npc == null) {
                continue;
            }
            int lateral = (index - (npcServices.size() - 1) / 2) * 2;
            BlockPos position = shore.deckOrigin().relative(shore.waterDirection(), 1).relative(across, lateral);
            npc.moveTo(position.getX() + 0.5, position.getY() + 1.0, position.getZ() + 0.5,
                    shore.waterDirection().toYRot() + 180.0F, 0.0F);
            npc.addTag(GENERATED_TAG);
            npc.addTag(siteTag);
            level.addFreshEntity(npc);
            index++;
        }
        return index;
    }

    private static boolean isNpc(PortService service) {
        return switch (service) {
            case INTENDANT, SHIPWRIGHT, FISHMONGER, NATURALIST, LIGHTHOUSE_KEEPER -> true;
            case CONTRACT_BOARD, STORAGE, CREATE_MECHANISM -> false;
        };
    }

    private static Supplier<? extends EntityType<PortNpcEntity>> entityType(PortService service) {
        return switch (service) {
            case INTENDANT -> TideboundEntities.HARBOR_INTENDANT;
            case SHIPWRIGHT -> TideboundEntities.SHIPWRIGHT;
            case FISHMONGER -> TideboundEntities.FISHMONGER;
            case NATURALIST -> TideboundEntities.NATURALIST;
            case LIGHTHOUSE_KEEPER -> TideboundEntities.LIGHTHOUSE_KEEPER;
            default -> throw new IllegalArgumentException("Not an NPC service: " + service);
        };
    }

    private static String siteTag(long siteId) {
        return "tidebound_site_" + Long.toUnsignedString(siteId, 16);
    }

    private record ShoreSite(BlockPos deckOrigin, Direction waterDirection, double distance) {
    }

    public record PlacementResult(boolean placed, BlockPos origin, int npcCount, String message) {
        private static PlacementResult failure(String message) {
            return new PlacementResult(false, BlockPos.ZERO, 0, message);
        }
    }
}
