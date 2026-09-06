package dev.tidebound.core.service;

import dev.tidebound.core.advancement.TideboundCriteriaTriggers;
import dev.tidebound.core.content.ContractDefinition;
import dev.tidebound.core.content.TideboundContentManager;
import dev.tidebound.core.data.ContractProgress;
import dev.tidebound.core.data.PlayerProgress;
import dev.tidebound.core.data.PlayerVessel;
import dev.tidebound.core.data.VesselDeployment;
import dev.tidebound.core.data.VesselHoldPolicy;
import dev.tidebound.core.data.VesselUpgrade;
import dev.tidebound.core.data.VesselUpgradeQuote;
import dev.tidebound.core.progression.SkillProgression;
import dev.tidebound.core.npc.PortNpcEntity;
import dev.tidebound.core.npc.PortNpcRole;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/** Turns tagged vanilla villagers into server-side Tidebound contract boards. */
public final class HarborBoardService {
    public static final String BOARD_TAG = "tidebound_contract_board";
    private static final double BOARD_RANGE = 8.0;

    private HarborBoardService() {
    }

    public static void register(IEventBus gameBus) {
        gameBus.addListener(HarborBoardService::onEntityInteract);
    }

    public static boolean registerBoard(Entity entity) {
        if (!(entity instanceof Villager villager)) {
            return false;
        }
        villager.addTag(BOARD_TAG);
        villager.setCustomName(Component.literal("Intendant du port"));
        villager.setCustomNameVisible(true);
        villager.setPersistenceRequired();
        return true;
    }

    public static boolean unregisterBoard(Entity entity) {
        return entity instanceof Villager && entity.removeTag(BOARD_TAG);
    }

    public static boolean isNearBoard(ServerPlayer player) {
        return isNearRole(player, PortNpcRole.INTENDANT) || !player.serverLevel().getEntitiesOfClass(
                Villager.class,
                player.getBoundingBox().inflate(BOARD_RANGE),
                HarborBoardService::isBoard
        ).isEmpty();
    }

    public static boolean isNearShipwright(ServerPlayer player) {
        return isNearRole(player, PortNpcRole.SHIPWRIGHT);
    }

    public static boolean isNearRole(ServerPlayer player, PortNpcRole role) {
        return !player.serverLevel().getEntitiesOfClass(
                PortNpcEntity.class,
                player.getBoundingBox().inflate(BOARD_RANGE),
                npc -> npc.role() == role
        ).isEmpty();
    }

    public static boolean showNearbyBoard(ServerPlayer player) {
        if (!isNearBoard(player)) {
            player.sendSystemMessage(Component.literal("Approchez-vous d'un intendant de port.")
                    .withStyle(ChatFormatting.RED));
            return false;
        }
        show(player);
        return true;
    }

    public static void show(ServerPlayer player) {
        showVesselActions(player);
        player.sendSystemMessage(Component.literal("=== TABLEAU DES CONTRATS ===")
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        List<String> ids = TideboundContentManager.contractIds();
        if (ids.isEmpty()) {
            player.sendSystemMessage(Component.literal("Aucun contrat disponible.")
                    .withStyle(ChatFormatting.GRAY));
            return;
        }
        for (String id : ids) {
            TideboundContentManager.contract(id).ifPresent(definition -> showContract(player, definition));
        }
    }

    public static void open(ServerPlayer player) {
        open(player, PortNpcRole.INTENDANT);
    }

    public static void open(ServerPlayer player, PortNpcRole role) {
        TideboundCriteriaTriggers.QUEST_SIGNAL.get().trigger(player, "port_visited");
        if (role == PortNpcRole.INTENDANT) {
            TideboundCriteriaTriggers.QUEST_SIGNAL.get().trigger(player, "contract_board_opened");
        }
        player.openMenu(new SimpleMenuProvider(
                (containerId, inventory, ignored) ->
                        new dev.tidebound.core.menu.HarborMenu(containerId, inventory, player, role),
                Component.translatable(role.translationKey())
        ));
    }

    private static void showVesselActions(ServerPlayer player) {
        PlayerVessel vessel = VesselService.vessel(player);
        player.sendSystemMessage(Component.literal("=== CAPITAINERIE TIDEBOUND ===")
                .withStyle(ChatFormatting.DARK_AQUA, ChatFormatting.BOLD));
        if (!vessel.unlocked()) {
            player.sendSystemMessage(Component.literal("Amenez votre barque vanilla au quai pour l'enregistrer. ")
                    .withStyle(ChatFormatting.GRAY)
                    .append(commandButton("[ENREGISTRER LA BARQUE]", "/tidebound vessel register"))
                    .append(Component.literal("  "))
                    .append(commandButton("[OBTENIR UNE BARQUE]", "/tidebound vessel claim")));
            return;
        }

        VesselDeployment deployment = VesselDeploymentService.deployment(player);
        MutableComponent line = Component.literal(vessel.name() + " — Coque " + vessel.hullTier()
                + ", Moteur " + vessel.motorTier() + ", Cale " + vessel.holdTier()
                + ", Modules " + vessel.moduleSlots() + "  ").withStyle(ChatFormatting.AQUA);
        if (deployment.active()) {
            line.append(Component.literal("Utilisez le Compas de sillage pour le retrouver.")
                    .withStyle(ChatFormatting.GRAY));
        } else {
            line.append(commandButton("[METTRE À L'EAU]", "/tidebound vessel deploy"));
        }
        if (!WakeCompassService.hasCompass(player)) {
            line.append(Component.literal("  "))
                    .append(commandButton("[COMPAS DE REMPLACEMENT]", "/tidebound vessel compass"));
        }
        player.sendSystemMessage(line);
        player.sendSystemMessage(Component.literal("Cale utilisable : "
                + VesselHoldPolicy.usableSlots(vessel.holdTier()) + "/27 emplacements.")
                .withStyle(ChatFormatting.GRAY));
        showUpgrade(player, VesselUpgrade.HULL, "COQUE", "hull");
        showUpgrade(player, VesselUpgrade.MOTOR, "MOTEUR", "motor");
        showUpgrade(player, VesselUpgrade.HOLD, "CALE", "hold");
        showUpgrade(player, VesselUpgrade.MODULE_SLOT, "MODULE", "module");
        VesselMaintenanceService.nearbyPhysicalVessel(player).ifPresent(boat -> {
            if (boat.getDamage() > 0.01F) {
                player.sendSystemMessage(commandButton("[RÉPARER LE NAVIRE]", "/tidebound vessel repair"));
            }
        });
    }

    private static void showUpgrade(ServerPlayer player, VesselUpgrade upgrade, String label, String command) {
        VesselUpgradeQuote quote = VesselMaintenanceService.nextUpgrade(player, upgrade).orElse(null);
        if (quote == null) {
            player.sendSystemMessage(Component.literal("• " + label + " — niveau maximum")
                    .withStyle(ChatFormatting.DARK_GRAY));
            return;
        }
        MutableComponent line = Component.literal("• " + label + " → " + quote.targetTier() + " — "
                + quote.tideCost() + " Tides + " + quote.materialCount() + " × "
                + quote.materialItemId() + " — " + quote.requiredSkill() + " niv. "
                + quote.requiredSkillLevel() + "  ").withStyle(ChatFormatting.GRAY);
        line.append(commandButton("[ACHETER]", "/tidebound vessel purchase " + command));
        player.sendSystemMessage(line);
    }

    private static MutableComponent commandButton(String label, String command) {
        return Component.literal(label).withStyle(style -> style
                .withColor(ChatFormatting.GOLD)
                .withBold(true)
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command)));
    }

    private static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!isBoard(event.getTarget())) {
            return;
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
        if (event.getHand() == InteractionHand.MAIN_HAND && event.getEntity() instanceof ServerPlayer player) {
            open(player);
        }
    }

    private static boolean isBoard(Entity entity) {
        return entity instanceof Villager && entity.getTags().contains(BOARD_TAG);
    }

    private static void showContract(ServerPlayer player, ContractDefinition definition) {
        PlayerProgress progress = ProgressionService.progress(player);
        String state = state(player, progress, definition);
        ChatFormatting color = state.equals("PRÊT") ? ChatFormatting.GREEN : ChatFormatting.GRAY;
        MutableComponent line = Component.literal("• " + definition.title() + " — "
                + definition.requirement().count() + " × " + definition.requirement().itemId()
                + " — " + definition.reward().tides() + " Tides — " + state).withStyle(color);

        if (state.equals("PRÊT")) {
            line.append(Component.literal("  [LIVRER]").withStyle(style -> style
                    .withColor(ChatFormatting.AQUA)
                    .withBold(true)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                            "/tidebound contracts deliver " + definition.id()))));
        }
        player.sendSystemMessage(line);
    }

    private static String state(ServerPlayer player, PlayerProgress progress, ContractDefinition definition) {
        if (definition.skillRequirement().isPresent()) {
            var required = definition.skillRequirement().orElseThrow();
            int level = SkillProgression.levelForXp(progress.skillXp(required.skillId()));
            if (level < required.level()) {
                return "NIVEAU " + required.level() + " " + required.skillId();
            }
        }

        ContractProgress contract = progress.contract(definition.id());
        long remaining = Math.max(0, contract.nextAvailableAt() - player.serverLevel().getGameTime());
        if (remaining > 0) {
            long minutes = Math.max(1, (remaining + 1_199) / 1_200);
            return "RETOUR DANS " + minutes + " MIN";
        }

        try {
            Item item = RewardService.resolveItem(definition.requirement());
            int owned = player.getInventory().countItem(item);
            if (owned < definition.requirement().count()) {
                return owned + "/" + definition.requirement().count();
            }
        } catch (IllegalArgumentException exception) {
            return "INDISPONIBLE";
        }
        return "PRÊT";
    }
}
