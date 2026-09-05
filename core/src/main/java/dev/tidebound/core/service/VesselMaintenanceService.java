package dev.tidebound.core.service;

import dev.tidebound.core.content.ItemAmount;
import dev.tidebound.core.data.PlayerProgress;
import dev.tidebound.core.data.PlayerVessel;
import dev.tidebound.core.data.TideWallet;
import dev.tidebound.core.data.VesselRepairQuote;
import dev.tidebound.core.data.VesselTransactionResult;
import dev.tidebound.core.data.VesselUpgrade;
import dev.tidebound.core.data.VesselUpgradeQuote;
import dev.tidebound.core.progression.SkillProgression;
import dev.tidebound.core.registry.TideboundAttachments;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Server-authoritative, rollback-safe vessel upgrades and harbour repairs. */
public final class VesselMaintenanceService {
    private static final double SERVICE_RANGE_SQR = 12.0 * 12.0;
    private static final Set<UUID> ACTIVE_TRANSACTIONS = new HashSet<>();

    private VesselMaintenanceService() {
    }

    public static Optional<VesselUpgradeQuote> nextUpgrade(ServerPlayer player, VesselUpgrade upgrade) {
        try {
            return Optional.of(VesselUpgradeQuote.next(VesselService.vessel(player), upgrade));
        } catch (IllegalArgumentException | IllegalStateException exception) {
            return Optional.empty();
        }
    }

    public static VesselTransactionResult purchaseUpgrade(ServerPlayer player, VesselUpgrade upgrade) {
        if (!HarborBoardService.isNearBoard(player)) {
            return failure("not_at_harbor", "Approchez-vous d'un intendant de port.");
        }
        if (!begin(player)) {
            return failure("transaction_busy", "Une transaction est déjà en cours.");
        }

        try {
            Entity physical = nearbyPhysicalVessel(player).orElse(null);
            if (physical == null) {
                return failure("vessel_not_at_harbor", "Amenez votre navire au quai avant de l'améliorer.");
            }
            if (!(physical instanceof dev.tidebound.core.vessel.TideboundVesselEntity)) {
                return failure("simple_boat", "Cette barque de fortune ne peut pas être améliorée. "
                        + "Faites construire un navire Tidebound au chantier naval.");
            }

            PlayerVessel beforeVessel = VesselService.vessel(player);
            VesselUpgradeQuote quote;
            try {
                quote = VesselUpgradeQuote.next(beforeVessel, upgrade);
            } catch (IllegalArgumentException | IllegalStateException exception) {
                return failure("upgrade_unavailable", exception.getMessage());
            }

            PlayerProgress progress = ProgressionService.progress(player);
            int skillLevel = SkillProgression.levelForXp(progress.skillXp(quote.requiredSkill()));
            if (skillLevel < quote.requiredSkillLevel()) {
                return failure("skill_locked", "Nécessite " + quote.requiredSkill() + " niveau "
                        + quote.requiredSkillLevel() + " (actuel : " + skillLevel + ").");
            }

            Item material;
            try {
                material = RewardService.resolveItem(
                        new ItemAmount(quote.materialItemId(), quote.materialCount()));
            } catch (IllegalArgumentException exception) {
                return failure("invalid_material", exception.getMessage());
            }
            Inventory inventory = player.getInventory();
            int owned = inventory.countItem(material);
            if (owned < quote.materialCount()) {
                return failure("missing_materials", "Matériaux manquants : " + owned + "/"
                        + quote.materialCount() + " × " + quote.materialItemId() + ".");
            }

            TideWallet beforeWallet = TideEconomy.wallet(player);
            if (!beforeWallet.canAfford(quote.tideCost())) {
                return failure("insufficient_tides", "Tides insuffisants : " + beforeWallet.balance() + "/"
                        + quote.tideCost() + ".");
            }

            try {
                TideEconomy.set(player, beforeWallet.debit(quote.tideCost()));
                consume(inventory, material, quote.materialCount());
                PlayerVessel upgraded = VesselService.upgrade(player, upgrade);
                if (upgrade == VesselUpgrade.HOLD) {
                    VesselDeploymentService.ensureCargoVessel(player, upgraded);
                }
                return VesselTransactionResult.completed("upgrade_purchased", label(upgrade) + " niveau "
                        + quote.targetTier() + " installé pour " + quote.tideCost() + " Tides et "
                        + quote.materialCount() + " × " + quote.materialItemId() + ".");
            } catch (RuntimeException exception) {
                TideEconomy.set(player, beforeWallet);
                player.setData(TideboundAttachments.PLAYER_VESSEL, beforeVessel);
                restore(player, material, quote.materialCount());
                VesselDeploymentService.syncLoaded(player);
                return failure("upgrade_rolled_back", "Amélioration annulée et ressources remboursées : "
                        + safeMessage(exception));
            }
        } finally {
            end(player);
        }
    }

    public static VesselTransactionResult repair(ServerPlayer player) {
        if (!HarborBoardService.isNearBoard(player)) {
            return failure("not_at_harbor", "Approchez-vous d'un intendant de port.");
        }
        if (!begin(player)) {
            return failure("transaction_busy", "Une transaction est déjà en cours.");
        }

        try {
            Entity entity = nearbyPhysicalVessel(player).orElse(null);
            if (!(entity instanceof Boat boat)) {
                return failure("vessel_not_at_harbor", "Amenez votre navire endommagé au quai.");
            }
            if (boat.getDamage() <= 0.01F) {
                return failure("not_damaged", "Le navire n'a pas besoin de réparation.");
            }

            VesselRepairQuote quote = VesselRepairQuote.forDamage(boat.getDamage());
            Item material;
            try {
                material = RewardService.resolveItem(
                        new ItemAmount(quote.materialItemId(), quote.materialCount()));
            } catch (IllegalArgumentException exception) {
                return failure("invalid_material", exception.getMessage());
            }
            Inventory inventory = player.getInventory();
            if (inventory.countItem(material) < quote.materialCount()) {
                return failure("missing_materials", "Il faut " + quote.materialCount() + " × "
                        + quote.materialItemId() + " pour réparer.");
            }

            TideWallet beforeWallet = TideEconomy.wallet(player);
            if (!beforeWallet.canAfford(quote.tideCost())) {
                return failure("insufficient_tides", "Il faut " + quote.tideCost() + " Tides pour réparer.");
            }

            float beforeDamage = boat.getDamage();
            try {
                TideEconomy.set(player, beforeWallet.debit(quote.tideCost()));
                consume(inventory, material, quote.materialCount());
                boat.setDamage(0);
                return VesselTransactionResult.completed("vessel_repaired", "Navire réparé pour "
                        + quote.tideCost() + " Tides et " + quote.materialCount() + " × "
                        + quote.materialItemId() + ".");
            } catch (RuntimeException exception) {
                boat.setDamage(beforeDamage);
                TideEconomy.set(player, beforeWallet);
                restore(player, material, quote.materialCount());
                return failure("repair_rolled_back", "Réparation annulée et ressources remboursées : "
                        + safeMessage(exception));
            }
        } finally {
            end(player);
        }
    }

    public static Optional<Boat> nearbyPhysicalVessel(ServerPlayer player) {
        return VesselDeploymentService.findActive(player)
                .filter(entity -> entity instanceof Boat)
                .filter(entity -> entity.level() == player.level())
                .filter(entity -> entity.distanceToSqr(player) <= SERVICE_RANGE_SQR)
                .map(entity -> (Boat) entity);
    }

    private static void consume(Inventory inventory, Item item, int requested) {
        int remaining = requested;
        for (int slot = 0; slot < inventory.getContainerSize() && remaining > 0; slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (!stack.is(item)) {
                continue;
            }
            int removed = Math.min(stack.getCount(), remaining);
            stack.shrink(removed);
            remaining -= removed;
        }
        if (remaining != 0) {
            throw new IllegalStateException("L'inventaire a changé pendant la transaction");
        }
        inventory.setChanged();
    }

    private static void restore(ServerPlayer player, Item item, int count) {
        int remaining = count;
        int maximum = item.getDefaultInstance().getMaxStackSize();
        while (remaining > 0) {
            ItemStack stack = new ItemStack(item, Math.min(remaining, maximum));
            player.getInventory().add(stack);
            if (!stack.isEmpty()) {
                player.drop(stack, false);
            }
            remaining -= Math.min(remaining, maximum);
        }
    }

    private static boolean begin(ServerPlayer player) {
        return ACTIVE_TRANSACTIONS.add(player.getUUID());
    }

    private static void end(ServerPlayer player) {
        ACTIVE_TRANSACTIONS.remove(player.getUUID());
    }

    private static VesselTransactionResult failure(String code, String message) {
        return VesselTransactionResult.failure(code, message == null ? code : message);
    }

    private static String safeMessage(RuntimeException exception) {
        return exception.getMessage() == null ? exception.getClass().getSimpleName() : exception.getMessage();
    }

    private static String label(VesselUpgrade upgrade) {
        return switch (upgrade) {
            case HULL -> "Coque";
            case MOTOR -> "Moteur";
            case HOLD -> "Cale";
            case MODULE_SLOT -> "Emplacement de module";
        };
    }
}
