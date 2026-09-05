package dev.tidebound.core.vessel;

import dev.tidebound.core.data.PlayerVessel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Dedicated physical vessel. ChestBoat supplies proven buoyancy, controls and inventory while this
 * type owns Tidebound's identity, larger crew limit and synchronized modular appearance.
 */
public final class TideboundVesselEntity extends ChestBoat {
    private static final EntityDataAccessor<Integer> HULL_TIER =
            SynchedEntityData.defineId(TideboundVesselEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> MOTOR_TIER =
            SynchedEntityData.defineId(TideboundVesselEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> HOLD_TIER =
            SynchedEntityData.defineId(TideboundVesselEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> MODULE_SLOTS =
            SynchedEntityData.defineId(TideboundVesselEntity.class, EntityDataSerializers.INT);

    public TideboundVesselEntity(EntityType<? extends ChestBoat> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HULL_TIER, 1);
        builder.define(MOTOR_TIER, 1);
        builder.define(HOLD_TIER, 1);
        builder.define(MODULE_SLOTS, 1);
    }

    public void syncVisuals(PlayerVessel vessel) {
        VesselVisualProfile profile = VesselVisualProfile.from(vessel);
        entityData.set(HULL_TIER, profile.hullTier());
        entityData.set(MOTOR_TIER, profile.motorTier());
        entityData.set(HOLD_TIER, profile.holdTier());
        entityData.set(MODULE_SLOTS, profile.moduleSlots());
    }

    public VesselVisualProfile visualProfile() {
        return new VesselVisualProfile(
                entityData.get(HULL_TIER),
                entityData.get(MOTOR_TIER),
                entityData.get(HOLD_TIER),
                entityData.get(MODULE_SLOTS));
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return getPassengers().size() < 4 && !isEyeInFluid(net.minecraft.tags.FluidTags.WATER);
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
        Vec3 vanilla = super.getPassengerAttachmentPoint(passenger, dimensions, scaleFactor);
        int seat = Math.max(0, getPassengers().indexOf(passenger));
        double side = seat % 2 == 0 ? -0.42 : 0.42;
        double foreAft = seat < 2 ? 0.36 : -0.42;
        return new Vec3(side, vanilla.y + 0.08, foreAft);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        VesselVisualProfile profile = visualProfile();
        tag.putInt("TideboundHullTier", profile.hullTier());
        tag.putInt("TideboundMotorTier", profile.motorTier());
        tag.putInt("TideboundHoldTier", profile.holdTier());
        tag.putInt("TideboundModuleSlots", profile.moduleSlots());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(HULL_TIER, bounded(tag.getInt("TideboundHullTier"), PlayerVessel.MAX_TIER));
        entityData.set(MOTOR_TIER, bounded(tag.getInt("TideboundMotorTier"), PlayerVessel.MAX_TIER));
        entityData.set(HOLD_TIER, bounded(tag.getInt("TideboundHoldTier"), PlayerVessel.MAX_TIER));
        entityData.set(MODULE_SLOTS, bounded(tag.getInt("TideboundModuleSlots"), PlayerVessel.MAX_MODULE_SLOTS));
    }

    private static int bounded(int value, int maximum) {
        return Math.max(1, Math.min(maximum, value));
    }
}
