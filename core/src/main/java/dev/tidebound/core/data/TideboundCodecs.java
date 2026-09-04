package dev.tidebound.core.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public final class TideboundCodecs {
    public static final Codec<TideWallet> TIDE_WALLET = RecordCodecBuilder.create(instance -> instance.group(
            Codec.LONG.optionalFieldOf("balance", 0L).forGetter(TideWallet::balance)
    ).apply(instance, TideWallet::new));

    public static final Codec<PlayerVessel> PLAYER_VESSEL = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("vessel_id", "").forGetter(PlayerVessel::vesselId),
            Codec.STRING.optionalFieldOf("name", "Barque sans nom").forGetter(PlayerVessel::name),
            Codec.BOOL.optionalFieldOf("unlocked", false).forGetter(PlayerVessel::unlocked),
            Codec.INT.optionalFieldOf("hull_tier", 0).forGetter(PlayerVessel::hullTier),
            Codec.INT.optionalFieldOf("motor_tier", 0).forGetter(PlayerVessel::motorTier),
            Codec.INT.optionalFieldOf("hold_tier", 0).forGetter(PlayerVessel::holdTier),
            Codec.INT.optionalFieldOf("module_slots", 0).forGetter(PlayerVessel::moduleSlots)
    ).apply(instance, PlayerVessel::new));

    public static final Codec<VesselEntityLink> VESSEL_ENTITY_LINK = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("owner_id", "").forGetter(VesselEntityLink::ownerId),
            Codec.STRING.optionalFieldOf("vessel_id", "").forGetter(VesselEntityLink::vesselId)
    ).apply(instance, VesselEntityLink::new));

    public static final Codec<VesselDeployment> VESSEL_DEPLOYMENT = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("entity_id", "").forGetter(VesselDeployment::entityId),
            Codec.STRING.optionalFieldOf("dimension_id", "").forGetter(VesselDeployment::dimensionId),
            Codec.INT.optionalFieldOf("block_x", 0).forGetter(VesselDeployment::blockX),
            Codec.INT.optionalFieldOf("block_y", 0).forGetter(VesselDeployment::blockY),
            Codec.INT.optionalFieldOf("block_z", 0).forGetter(VesselDeployment::blockZ),
            Codec.INT.optionalFieldOf("chunk_x", 0).forGetter(VesselDeployment::chunkX),
            Codec.INT.optionalFieldOf("chunk_z", 0).forGetter(VesselDeployment::chunkZ),
            Codec.STRING.optionalFieldOf("state", "legacy")
                    .xmap(VesselDeploymentState::fromId, VesselDeploymentState::id)
                    .forGetter(VesselDeployment::state)
    ).apply(instance, VesselDeployment::new));

    public static final Codec<ContractProgress> CONTRACT_PROGRESS = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("completion_count", 0).forGetter(ContractProgress::completionCount),
            Codec.LONG.optionalFieldOf("next_available_at", 0L).forGetter(ContractProgress::nextAvailableAt)
    ).apply(instance, ContractProgress::new));

    public static final Codec<PlayerProgress> PLAYER_PROGRESS = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.listOf().optionalFieldOf("claimed_receipts", java.util.List.of())
                    .forGetter(PlayerProgress::claimedReceipts),
            Codec.STRING.listOf().optionalFieldOf("completed_milestones", java.util.List.of())
                    .forGetter(PlayerProgress::completedMilestones),
            Codec.unboundedMap(Codec.STRING, Codec.LONG).optionalFieldOf("skill_xp", java.util.Map.of())
                    .forGetter(PlayerProgress::skillXp),
            Codec.unboundedMap(Codec.STRING, CONTRACT_PROGRESS).optionalFieldOf("contracts", java.util.Map.of())
                    .forGetter(PlayerProgress::contracts)
    ).apply(instance, PlayerProgress::new));

    private TideboundCodecs() {
    }
}
