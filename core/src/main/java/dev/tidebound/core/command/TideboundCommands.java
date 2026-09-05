package dev.tidebound.core.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.tidebound.core.api.TideboundApi;
import dev.tidebound.core.content.ContractDefinition;
import dev.tidebound.core.content.TideboundContentManager;
import dev.tidebound.core.data.ContractProgress;
import dev.tidebound.core.data.PlayerVessel;
import dev.tidebound.core.data.PlayerProgress;
import dev.tidebound.core.data.TideWallet;
import dev.tidebound.core.data.VesselUpgrade;
import dev.tidebound.core.data.VesselTransactionResult;
import dev.tidebound.core.fishing.CatchData;
import dev.tidebound.core.progression.ProgressionResult;
import dev.tidebound.core.progression.SkillProgression;
import dev.tidebound.core.service.HarborBoardService;
import dev.tidebound.core.service.ArchipelagoSurveyService;
import dev.tidebound.core.service.ProgressionService;
import dev.tidebound.core.world.ArchipelagoSurvey;
import dev.tidebound.core.world.StarterPortPlan;
import java.util.Locale;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public final class TideboundCommands {
    private static final int ADMIN_PERMISSION = 2;
    private static final long MAX_DIAGNOSTIC_TRANSACTION = 1_000_000L;

    private TideboundCommands() {
    }

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("tidebound")
                .then(tideNode())
                .then(vesselNode())
                .then(progressionNode())
                .then(catchNode())
                .then(skillsNode())
                .then(contractBoardNode())
                .then(harborNode())
                .then(worldNode()));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> tideNode() {
        return Commands.literal("tide")
                .then(Commands.literal("balance")
                        .executes(context -> showBalance(
                                context.getSource(), context.getSource().getPlayerOrException()))
                        .then(Commands.argument("player", EntityArgument.player())
                                .requires(source -> source.hasPermission(ADMIN_PERMISSION))
                                .executes(context -> showBalance(
                                        context.getSource(), EntityArgument.getPlayer(context, "player")))))
                .then(Commands.literal("grant")
                        .requires(source -> source.hasPermission(ADMIN_PERMISSION))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("amount", LongArgumentType.longArg(
                                                1, MAX_DIAGNOSTIC_TRANSACTION))
                                        .executes(context -> grant(
                                                context.getSource(),
                                                EntityArgument.getPlayer(context, "player"),
                                                LongArgumentType.getLong(context, "amount"))))))
                .then(Commands.literal("spend")
                        .requires(source -> source.hasPermission(ADMIN_PERMISSION))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("amount", LongArgumentType.longArg(
                                                1, MAX_DIAGNOSTIC_TRANSACTION))
                                        .executes(context -> spend(
                                                context.getSource(),
                                                EntityArgument.getPlayer(context, "player"),
                                                LongArgumentType.getLong(context, "amount"))))));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> vesselNode() {
        return Commands.literal("vessel")
                .then(Commands.literal("claim")
                        .executes(context -> claimVessel(
                                context.getSource(), context.getSource().getPlayerOrException(), "L'Écumeur"))
                        .then(Commands.argument("name", StringArgumentType.greedyString())
                                .executes(context -> claimVessel(
                                        context.getSource(),
                                        context.getSource().getPlayerOrException(),
                                        StringArgumentType.getString(context, "name")))))
                .then(Commands.literal("deploy")
                        .executes(context -> deployVessel(
                                context.getSource(), context.getSource().getPlayerOrException())))
                .then(Commands.literal("register")
                        .executes(context -> registerVessel(
                                context.getSource(), context.getSource().getPlayerOrException(), "Ma Barque"))
                        .then(Commands.argument("name", StringArgumentType.greedyString())
                                .executes(context -> registerVessel(
                                        context.getSource(),
                                        context.getSource().getPlayerOrException(),
                                        StringArgumentType.getString(context, "name")))))
                .then(Commands.literal("compass")
                        .executes(context -> issueWakeCompass(
                                context.getSource(), context.getSource().getPlayerOrException())))
                .then(Commands.literal("locate")
                        .requires(source -> source.hasPermission(ADMIN_PERMISSION))
                        .executes(context -> locateVessel(
                                context.getSource(), context.getSource().getPlayerOrException())))
                .then(Commands.literal("rename")
                        .then(Commands.argument("name", StringArgumentType.greedyString())
                                .executes(context -> renameVessel(
                                        context.getSource(),
                                        context.getSource().getPlayerOrException(),
                                        StringArgumentType.getString(context, "name")))))
                .then(Commands.literal("purchase")
                        .then(purchaseUpgradeNode("hull", VesselUpgrade.HULL))
                        .then(purchaseUpgradeNode("motor", VesselUpgrade.MOTOR))
                        .then(purchaseUpgradeNode("hold", VesselUpgrade.HOLD))
                        .then(purchaseUpgradeNode("module", VesselUpgrade.MODULE_SLOT)))
                .then(Commands.literal("repair")
                        .executes(context -> showVesselTransaction(
                                context.getSource(), TideboundApi.repairVessel(
                                        context.getSource().getPlayerOrException()))))
                .then(Commands.literal("inspect")
                        .executes(context -> inspectVessel(
                                context.getSource(), context.getSource().getPlayerOrException()))
                        .then(Commands.argument("player", EntityArgument.player())
                                .requires(source -> source.hasPermission(ADMIN_PERMISSION))
                                .executes(context -> inspectVessel(
                                        context.getSource(), EntityArgument.getPlayer(context, "player")))))
                .then(Commands.literal("unlock")
                        .requires(source -> source.hasPermission(ADMIN_PERMISSION))
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(context -> unlockVessel(
                                        context.getSource(), EntityArgument.getPlayer(context, "player"), "L'Écumeur"))
                                .then(Commands.argument("name", StringArgumentType.greedyString())
                                        .executes(context -> unlockVessel(
                                                context.getSource(),
                                                EntityArgument.getPlayer(context, "player"),
                                                StringArgumentType.getString(context, "name"))))))
                .then(Commands.literal("upgrade")
                        .requires(source -> source.hasPermission(ADMIN_PERMISSION))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(vesselUpgradeNode("hull", VesselUpgrade.HULL))
                                .then(vesselUpgradeNode("motor", VesselUpgrade.MOTOR))
                                .then(vesselUpgradeNode("hold", VesselUpgrade.HOLD))
                                .then(vesselUpgradeNode("module", VesselUpgrade.MODULE_SLOT))));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> vesselUpgradeNode(String name, VesselUpgrade upgrade) {
        return Commands.literal(name).executes(context -> upgradeVessel(
                context.getSource(), EntityArgument.getPlayer(context, "player"), upgrade));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> purchaseUpgradeNode(
            String name, VesselUpgrade upgrade) {
        return Commands.literal(name).executes(context -> showVesselTransaction(
                context.getSource(), TideboundApi.purchaseVesselUpgrade(
                        context.getSource().getPlayerOrException(), upgrade)));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> progressionNode() {
        return Commands.literal("progression")
                .then(Commands.literal("inspect")
                        .executes(context -> inspectProgression(
                                context.getSource(), context.getSource().getPlayerOrException()))
                        .then(Commands.argument("player", EntityArgument.player())
                                .requires(source -> source.hasPermission(ADMIN_PERMISSION))
                                .executes(context -> inspectProgression(
                                        context.getSource(), EntityArgument.getPlayer(context, "player")))))
                .then(Commands.literal("content")
                        .then(Commands.literal("summary").executes(context -> contentSummary(context.getSource())))
                        .then(Commands.literal("milestones").executes(context -> contentIds(
                                context.getSource(), "Paliers", TideboundContentManager.milestoneIds())))
                        .then(Commands.literal("contracts").executes(context -> contentIds(
                                context.getSource(), "Contrats", TideboundContentManager.contractIds()))))
                .then(Commands.literal("reward-once")
                        .requires(source -> source.hasPermission(ADMIN_PERMISSION))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("receipt", StringArgumentType.word())
                                        .then(Commands.argument("tides", LongArgumentType.longArg(
                                                        1, MAX_DIAGNOSTIC_TRANSACTION))
                                                .executes(context -> showProgressionResult(
                                                        context.getSource(), TideboundApi.grantTidesOnce(
                                                                EntityArgument.getPlayer(context, "player"),
                                                                StringArgumentType.getString(context, "receipt"),
                                                                LongArgumentType.getLong(context, "tides"))))))))
                .then(Commands.literal("skill")
                        .then(Commands.literal("grant")
                                .requires(source -> source.hasPermission(ADMIN_PERMISSION))
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("skill", StringArgumentType.word())
                                                .then(Commands.argument("xp", LongArgumentType.longArg(
                                                                1, MAX_DIAGNOSTIC_TRANSACTION))
                                                        .executes(context -> grantSkillXp(
                                                                context.getSource(),
                                                                EntityArgument.getPlayer(context, "player"),
                                                                StringArgumentType.getString(context, "skill"),
                                                                LongArgumentType.getLong(context, "xp"))))))))
                .then(Commands.literal("milestone")
                        .then(Commands.literal("complete")
                                .requires(source -> source.hasPermission(ADMIN_PERMISSION))
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("id", StringArgumentType.word())
                                                .executes(context -> showProgressionResult(
                                                        context.getSource(), TideboundApi.completeMilestone(
                                                                EntityArgument.getPlayer(context, "player"),
                                                                StringArgumentType.getString(context, "id"))))))))
                .then(Commands.literal("contract")
                        .then(Commands.literal("status")
                                .then(Commands.argument("id", StringArgumentType.word())
                                        .executes(context -> contractStatus(
                                                context.getSource(), context.getSource().getPlayerOrException(),
                                                StringArgumentType.getString(context, "id")))))
                        .then(Commands.literal("complete")
                                .requires(source -> source.hasPermission(ADMIN_PERMISSION))
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("id", StringArgumentType.word())
                                                .executes(context -> showProgressionResult(
                                                        context.getSource(), TideboundApi.completeContract(
                                                                EntityArgument.getPlayer(context, "player"),
                                                                StringArgumentType.getString(context, "id"))))))));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> skillsNode() {
        return Commands.literal("skills")
                .executes(context -> showSkills(
                        context.getSource(), context.getSource().getPlayerOrException()))
                .then(Commands.argument("player", EntityArgument.player())
                        .requires(source -> source.hasPermission(ADMIN_PERMISSION))
                        .executes(context -> showSkills(
                                context.getSource(), EntityArgument.getPlayer(context, "player"))));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> catchNode() {
        return Commands.literal("catch")
                .then(Commands.literal("inspect")
                        .executes(context -> inspectCatch(
                                context.getSource(), context.getSource().getPlayerOrException())));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> contractBoardNode() {
        return Commands.literal("contracts")
                .executes(context -> HarborBoardService.showNearbyBoard(
                        context.getSource().getPlayerOrException()) ? 1 : 0)
                .then(Commands.literal("deliver")
                        .then(Commands.argument("id", StringArgumentType.word())
                                .executes(context -> deliverNearbyContract(
                                        context.getSource(),
                                        context.getSource().getPlayerOrException(),
                                        StringArgumentType.getString(context, "id")))));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> harborNode() {
        return Commands.literal("harbor")
                .requires(source -> source.hasPermission(ADMIN_PERMISSION))
                .then(Commands.literal("register")
                        .then(Commands.argument("villager", EntityArgument.entity())
                                .executes(context -> configureHarborBoard(
                                        context.getSource(), EntityArgument.getEntity(context, "villager"), true))))
                .then(Commands.literal("unregister")
                        .then(Commands.argument("villager", EntityArgument.entity())
                                .executes(context -> configureHarborBoard(
                                        context.getSource(), EntityArgument.getEntity(context, "villager"), false))));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> worldNode() {
        return Commands.literal("world")
                .requires(source -> source.hasPermission(ADMIN_PERMISSION))
                .then(Commands.literal("diagnose")
                        .executes(context -> diagnoseWorld(context.getSource(), 128))
                        .then(Commands.argument("radius", IntegerArgumentType.integer(64, 256))
                                .executes(context -> diagnoseWorld(
                                        context.getSource(),
                                        IntegerArgumentType.getInteger(context, "radius")))));
    }

    private static int diagnoseWorld(CommandSourceStack source, int radius) {
        ServerPlayer player;
        try {
            player = source.getPlayerOrException();
        } catch (Exception exception) {
            source.sendFailure(Component.literal("Cette commande doit être exécutée par un joueur."));
            return 0;
        }
        ArchipelagoSurvey survey = ArchipelagoSurveyService.survey(
                player.serverLevel(), player.serverLevel().getSharedSpawnPos(), radius);
        boolean portRoll = StarterPortPlan.shouldGenerate(player.serverLevel().getSeed());
        int landPercent = (int) Math.round(survey.landRatio() * 100.0);
        int waterPercent = (int) Math.round(survey.waterRatio() * 100.0);
        source.sendSuccess(() -> Component.literal("Diagnostic Tidebound — rayon " + radius
                + " : terre " + landPercent + " %, eau " + waterPercent + " %, rivages "
                + survey.shoreSamples() + ", bois " + survey.logSamples() + ".")
                .withStyle(ChatFormatting.AQUA), false);
        source.sendSuccess(() -> Component.literal("Spawn " + (survey.playable() ? "JOUABLE" : "À REJETER")
                + (survey.continentLike() ? " — masse continentale détectée" : " — archipel confirmé")
                + " — tirage du port initial : " + (portRoll ? "oui" : "non"))
                .withStyle(survey.playable() && !survey.continentLike()
                        ? ChatFormatting.GREEN : ChatFormatting.RED), false);
        return survey.playable() && !survey.continentLike() ? 1 : 0;
    }

    private static int showBalance(CommandSourceStack source, ServerPlayer player) {
        TideWallet wallet = TideboundApi.wallet(player);
        source.sendSuccess(() -> Component.literal(player.getGameProfile().getName() + " possède "
                + wallet.balance() + " Tide(s).").withStyle(ChatFormatting.AQUA), false);
        return 1;
    }

    private static int inspectCatch(CommandSourceStack source, ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();
        CatchData data = TideboundApi.catchData(stack).orElse(null);
        if (data == null) {
            source.sendFailure(Component.literal("Tenez une prise Tidebound dans votre main principale."));
            return 0;
        }
        long gameTime = player.getServer().overworld().getGameTime();
        long value = TideboundApi.catchValue(stack, gameTime).orElse(0L);
        source.sendSuccess(() -> Component.literal(stack.getHoverName().getString()
                + " — " + data.weightGrams() + " g — qualité " + data.quality().id()
                + " — fraîcheur " + data.freshness(gameTime).id()
                + " — origine " + data.originBiomeId()
                + " — anomalie " + data.anomaly().id()
                + " — valeur estimée " + value + " Tides")
                .withStyle(ChatFormatting.AQUA), false);
        return 1;
    }

    private static int grant(CommandSourceStack source, ServerPlayer player, long amount) {
        try {
            TideWallet wallet = TideboundApi.grantTides(player, amount);
            source.sendSuccess(() -> Component.literal("+" + amount + " Tide(s) pour "
                    + player.getGameProfile().getName() + " — solde : " + wallet.balance())
                    .withStyle(ChatFormatting.GREEN), true);
            return 1;
        } catch (IllegalArgumentException exception) {
            source.sendFailure(Component.literal(exception.getMessage()));
            return 0;
        }
    }

    private static int spend(CommandSourceStack source, ServerPlayer player, long amount) {
        if (!TideboundApi.spendTides(player, amount)) {
            source.sendFailure(Component.literal("Solde insuffisant pour "
                    + player.getGameProfile().getName() + "."));
            return 0;
        }
        long balance = TideboundApi.wallet(player).balance();
        source.sendSuccess(() -> Component.literal("-" + amount + " Tide(s) pour "
                + player.getGameProfile().getName() + " — solde : " + balance)
                .withStyle(ChatFormatting.YELLOW), true);
        return 1;
    }

    private static int inspectVessel(CommandSourceStack source, ServerPlayer player) {
        PlayerVessel vessel = TideboundApi.vessel(player);
        if (!vessel.unlocked()) {
            source.sendSuccess(() -> Component.literal(player.getGameProfile().getName()
                    + " n'a pas encore débloqué son bateau.").withStyle(ChatFormatting.GRAY), false);
            return 1;
        }

        source.sendSuccess(() -> Component.literal(vessel.name() + " [" + vessel.vesselId() + "] — coque "
                + vessel.hullTier() + ", moteur " + vessel.motorTier() + ", cale " + vessel.holdTier()
                + ", modules " + vessel.moduleSlots()).withStyle(ChatFormatting.AQUA), false);
        return 1;
    }

    private static int claimVessel(CommandSourceStack source, ServerPlayer player, String name) {
        if (!HarborBoardService.isNearBoard(player)) {
            source.sendFailure(Component.literal("Approchez-vous d'un intendant de port."));
            return 0;
        }
        if (TideboundApi.vessel(player).unlocked()) {
            source.sendFailure(Component.literal("Vous avez déjà réclamé votre bateau."));
            return 0;
        }
        try {
            PlayerVessel vessel = TideboundApi.unlockVessel(player, name);
            TideboundApi.giveWakeCompass(player);
            source.sendSuccess(() -> Component.literal("L'intendant vous confie " + vessel.name()
                    + " et un Compas de sillage. Vous pouvez maintenant mettre le navire à l'eau près du quai.")
                    .withStyle(ChatFormatting.GOLD), false);
            return 1;
        } catch (IllegalArgumentException exception) {
            source.sendFailure(Component.literal(exception.getMessage()));
            return 0;
        }
    }

    private static int registerVessel(CommandSourceStack source, ServerPlayer player, String name) {
        try {
            var boat = TideboundApi.registerNearbyVanillaBoat(player, name);
            PlayerVessel vessel = TideboundApi.vessel(player);
            source.sendSuccess(() -> Component.literal("La barque vanilla devient " + vessel.name()
                    + ". L'intendant vous remet son Compas de sillage.")
                    .withStyle(ChatFormatting.GOLD), false);
            return boat.isAlive() ? 1 : 0;
        } catch (IllegalArgumentException | IllegalStateException exception) {
            source.sendFailure(Component.literal(exception.getMessage()));
            return 0;
        }
    }

    private static int issueWakeCompass(CommandSourceStack source, ServerPlayer player) {
        if (!HarborBoardService.isNearBoard(player)) {
            source.sendFailure(Component.literal("Le compas de remplacement est remis uniquement au port."));
            return 0;
        }
        if (!TideboundApi.vessel(player).unlocked()) {
            source.sendFailure(Component.literal("Enregistrez d'abord un navire Tidebound."));
            return 0;
        }
        boolean issued = TideboundApi.giveWakeCompass(player);
        source.sendSuccess(() -> Component.literal(issued
                ? "L'intendant vous remet un Compas de sillage."
                : "Vous possédez déjà un Compas de sillage.")
                .withStyle(issued ? ChatFormatting.GREEN : ChatFormatting.GRAY), false);
        return 1;
    }

    private static int deployVessel(CommandSourceStack source, ServerPlayer player) {
        try {
            var boat = TideboundApi.deployVessel(player);
            source.sendSuccess(() -> Component.literal(TideboundApi.vessel(player).name()
                    + " a été mis à l'eau en " + boat.blockPosition().toShortString() + ".")
                    .withStyle(ChatFormatting.AQUA), false);
            return 1;
        } catch (IllegalArgumentException | IllegalStateException exception) {
            source.sendFailure(Component.literal(exception.getMessage()));
            return 0;
        }
    }

    private static int locateVessel(CommandSourceStack source, ServerPlayer player) {
        String location = TideboundApi.locateVessel(player);
        source.sendSuccess(() -> Component.literal("Navire : " + location)
                .withStyle(ChatFormatting.AQUA), false);
        return 1;
    }

    private static int renameVessel(CommandSourceStack source, ServerPlayer player, String name) {
        if (!HarborBoardService.isNearBoard(player)) {
            source.sendFailure(Component.literal("Le navire ne peut être renommé qu'auprès d'un intendant."));
            return 0;
        }
        try {
            PlayerVessel vessel = TideboundApi.renameVessel(player, name);
            source.sendSuccess(() -> Component.literal("Votre navire porte désormais le nom " + vessel.name() + ".")
                    .withStyle(ChatFormatting.GREEN), false);
            return 1;
        } catch (IllegalArgumentException | IllegalStateException exception) {
            source.sendFailure(Component.literal(exception.getMessage()));
            return 0;
        }
    }

    private static int unlockVessel(CommandSourceStack source, ServerPlayer player, String name) {
        PlayerVessel before = TideboundApi.vessel(player);
        if (before.unlocked()) {
            source.sendFailure(Component.literal(player.getGameProfile().getName()
                    + " possède déjà le bateau " + before.name() + "."));
            return 0;
        }

        try {
            PlayerVessel vessel = TideboundApi.unlockVessel(player, name);
            source.sendSuccess(() -> Component.literal("Bateau débloqué pour "
                    + player.getGameProfile().getName() + " : " + vessel.name())
                    .withStyle(ChatFormatting.GREEN), true);
            return 1;
        } catch (IllegalArgumentException exception) {
            source.sendFailure(Component.literal(exception.getMessage()));
            return 0;
        }
    }

    private static int upgradeVessel(CommandSourceStack source, ServerPlayer player, VesselUpgrade upgrade) {
        try {
            PlayerVessel vessel = TideboundApi.upgradeVessel(player, upgrade);
            source.sendSuccess(() -> Component.literal("Amélioration " + upgrade.name().toLowerCase(Locale.ROOT)
                    + " appliquée à " + vessel.name() + ".").withStyle(ChatFormatting.GREEN), true);
            return 1;
        } catch (IllegalStateException exception) {
            source.sendFailure(Component.literal(exception.getMessage()));
            return 0;
        }
    }

    private static int inspectProgression(CommandSourceStack source, ServerPlayer player) {
        PlayerProgress progress = ProgressionService.progress(player);
        source.sendSuccess(() -> Component.literal(player.getGameProfile().getName()
                + " — paliers : " + progress.completedMilestones().size()
                + ", contrats connus : " + progress.contracts().size()
                + ", reçus : " + progress.claimedReceipts().size()
                + ", XP : " + progress.skillXp()).withStyle(ChatFormatting.AQUA), false);
        return 1;
    }

    private static int grantSkillXp(CommandSourceStack source, ServerPlayer player, String skill, long amount) {
        try {
            PlayerProgress before = ProgressionService.progress(player);
            PlayerProgress after = TideboundApi.grantSkillXp(player, skill, amount);
            int oldLevel = SkillProgression.levelForXp(before.skillXp(skill));
            int newLevel = SkillProgression.levelForXp(after.skillXp(skill));
            source.sendSuccess(() -> Component.literal("+" + amount + " XP " + skill + " pour "
                    + player.getGameProfile().getName() + " — niveau " + newLevel
                    + (newLevel > oldLevel ? " atteint !" : ".")).withStyle(ChatFormatting.GREEN), true);
            return 1;
        } catch (IllegalArgumentException | IllegalStateException exception) {
            source.sendFailure(Component.literal(exception.getMessage()));
            return 0;
        }
    }

    private static int showSkills(CommandSourceStack source, ServerPlayer player) {
        PlayerProgress progress = ProgressionService.progress(player);
        source.sendSuccess(() -> Component.literal("Métiers de " + player.getGameProfile().getName())
                .withStyle(ChatFormatting.GOLD), false);
        for (String skill : SkillProgression.CORE_SKILLS) {
            long xp = progress.skillXp(skill);
            int level = SkillProgression.levelForXp(xp);
            long remaining = SkillProgression.xpUntilNextLevel(xp);
            String next = remaining == 0 ? "niveau maximum" : remaining + " XP avant le niveau suivant";
            source.sendSuccess(() -> Component.literal("• " + skill + " — niveau " + level
                    + " — " + xp + " XP — " + next).withStyle(ChatFormatting.AQUA), false);
        }
        return 1;
    }

    private static int deliverNearbyContract(CommandSourceStack source, ServerPlayer player, String id) {
        if (!HarborBoardService.isNearBoard(player)) {
            source.sendFailure(Component.literal("Vous devez être près d'un intendant de port."));
            return 0;
        }
        return showProgressionResult(source, TideboundApi.completeContract(player, id));
    }

    private static int configureHarborBoard(CommandSourceStack source, Entity entity, boolean register) {
        boolean changed = register
                ? HarborBoardService.registerBoard(entity)
                : HarborBoardService.unregisterBoard(entity);
        if (!changed) {
            source.sendFailure(Component.literal(register
                    ? "La cible doit être un villageois."
                    : "Ce villageois n'est pas un intendant Tidebound."));
            return 0;
        }
        source.sendSuccess(() -> Component.literal(register
                ? "Intendant de port Tidebound enregistré."
                : "Intendant de port Tidebound retiré.").withStyle(ChatFormatting.GREEN), true);
        return 1;
    }

    private static int contentSummary(CommandSourceStack source) {
        source.sendSuccess(() -> Component.literal("Contenu Tidebound chargé — "
                + TideboundContentManager.milestoneCount() + " palier(s), "
                + TideboundContentManager.contractCount() + " contrat(s).")
                .withStyle(ChatFormatting.AQUA), false);
        return 1;
    }

    private static int contentIds(CommandSourceStack source, String label, List<String> ids) {
        String value = ids.isEmpty() ? "aucun" : String.join(", ", ids);
        source.sendSuccess(() -> Component.literal(label + " : " + value)
                .withStyle(ChatFormatting.GRAY), false);
        return ids.size();
    }

    private static int contractStatus(CommandSourceStack source, ServerPlayer player, String contractId) {
        ContractDefinition definition = TideboundContentManager.contract(contractId).orElse(null);
        if (definition == null) {
            source.sendFailure(Component.literal("Contrat inconnu : " + contractId));
            return 0;
        }

        PlayerProgress progress = ProgressionService.progress(player);
        if (definition.skillRequirement().isPresent()) {
            var required = definition.skillRequirement().orElseThrow();
            int level = SkillProgression.levelForXp(progress.skillXp(required.skillId()));
            if (level < required.level()) {
                source.sendSuccess(() -> Component.literal(definition.title() + " — verrouillé : "
                        + required.skillId() + " niveau " + required.level())
                        .withStyle(ChatFormatting.GRAY), false);
                return 1;
            }
        }

        ContractProgress state = progress.contract(definition.id());
        long gameTime = player.getServer().overworld().getGameTime();
        long remaining = Math.max(0, state.nextAvailableAt() - gameTime);
        String availability = remaining == 0 ? "disponible" : "disponible dans " + remaining + " ticks";
        source.sendSuccess(() -> Component.literal(definition.title() + " — " + availability
                + ", réalisations : " + state.completionCount()).withStyle(ChatFormatting.AQUA), false);
        return 1;
    }

    private static int showProgressionResult(CommandSourceStack source, ProgressionResult result) {
        Component message = Component.literal(result.message())
                .withStyle(result.success() ? ChatFormatting.GREEN : ChatFormatting.RED);
        if (result.success()) {
            source.sendSuccess(() -> message, true);
            return 1;
        }
        source.sendFailure(message);
        return 0;
    }

    private static int showVesselTransaction(CommandSourceStack source, VesselTransactionResult result) {
        Component message = Component.literal(result.message())
                .withStyle(result.success() ? ChatFormatting.GREEN : ChatFormatting.RED);
        if (result.success()) {
            source.sendSuccess(() -> message, false);
            return 1;
        }
        source.sendFailure(message);
        return 0;
    }
}
