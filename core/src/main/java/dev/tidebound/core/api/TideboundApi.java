package dev.tidebound.core.api;

import dev.tidebound.core.content.RewardDefinition;
import dev.tidebound.core.data.PlayerVessel;
import dev.tidebound.core.data.PlayerProgress;
import dev.tidebound.core.data.TideWallet;
import dev.tidebound.core.data.VesselUpgrade;
import dev.tidebound.core.data.VesselDeployment;
import dev.tidebound.core.progression.ProgressionResult;
import dev.tidebound.core.service.ContractService;
import dev.tidebound.core.service.MilestoneService;
import dev.tidebound.core.service.ProgressionService;
import dev.tidebound.core.service.RewardService;
import dev.tidebound.core.service.TideEconomy;
import dev.tidebound.core.service.VesselService;
import dev.tidebound.core.service.VesselDeploymentService;
import dev.tidebound.core.service.WakeCompassService;
import java.util.List;
import java.util.Map;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.entity.vehicle.Boat;

/**
 * Stable server-side entry points for Tidebound gameplay integrations.
 *
 * <p>Contracts, milestones, NPC trades and KubeJS bridges should call this facade instead of
 * accessing attachments directly.</p>
 */
public final class TideboundApi {
    private TideboundApi() {
    }

    public static TideWallet wallet(ServerPlayer player) {
        return TideEconomy.wallet(player);
    }

    public static TideWallet grantTides(ServerPlayer player, long amount) {
        return TideEconomy.grant(player, amount);
    }

    public static boolean spendTides(ServerPlayer player, long amount) {
        return TideEconomy.trySpend(player, amount);
    }

    public static PlayerVessel vessel(ServerPlayer player) {
        return VesselService.vessel(player);
    }

    public static PlayerVessel unlockVessel(ServerPlayer player, String name) {
        return VesselService.unlock(player, name);
    }

    public static PlayerVessel upgradeVessel(ServerPlayer player, VesselUpgrade upgrade) {
        return VesselService.upgrade(player, upgrade);
    }

    public static PlayerVessel renameVessel(ServerPlayer player, String name) {
        return VesselService.rename(player, name);
    }

    public static VesselDeployment vesselDeployment(ServerPlayer player) {
        return VesselDeploymentService.deployment(player);
    }

    public static ChestBoat deployVessel(ServerPlayer player) {
        return VesselDeploymentService.deploy(player);
    }

    public static Boat registerNearbyVanillaBoat(ServerPlayer player, String name) {
        return VesselDeploymentService.registerNearbyVanillaBoat(player, name);
    }

    public static boolean giveWakeCompass(ServerPlayer player) {
        return WakeCompassService.giveIfMissing(player);
    }

    public static String locateVessel(ServerPlayer player) {
        return VesselDeploymentService.locate(player);
    }

    public static PlayerProgress progress(ServerPlayer player) {
        return ProgressionService.progress(player);
    }

    public static PlayerProgress grantSkillXp(ServerPlayer player, String skillId, long amount) {
        return ProgressionService.addSkillXp(player, skillId, amount);
    }

    public static ProgressionResult grantTidesOnce(ServerPlayer player, String receiptId, long amount) {
        return RewardService.grantOnce(player, receiptId,
                new RewardDefinition(amount, Map.of(), List.of()));
    }

    public static ProgressionResult completeMilestone(ServerPlayer player, String milestoneId) {
        return MilestoneService.complete(player, milestoneId);
    }

    public static ProgressionResult completeContract(ServerPlayer player, String contractId) {
        return ContractService.complete(player, contractId);
    }
}
