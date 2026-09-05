package dev.tidebound.core.data;

import java.util.Map;
import java.util.UUID;
import dev.tidebound.core.fishing.CatchAnomaly;
import dev.tidebound.core.fishing.CatchData;
import dev.tidebound.core.fishing.CatchFreshness;
import dev.tidebound.core.fishing.CatchGenerator;
import dev.tidebound.core.fishing.CatchProfile;
import dev.tidebound.core.fishing.CatchProfiles;
import dev.tidebound.core.fishing.CatchQuality;
import dev.tidebound.core.fishing.CatchValuation;
import dev.tidebound.core.navigation.WakeBearing;
import dev.tidebound.core.progression.SkillProgression;

/**
 * Dependency-free smoke test. It can be executed with the JDK alone; see README.md.
 */
public final class DomainSelfTest {
    private DomainSelfTest() {
    }

    public static void main(String[] args) {
        walletCreditsAndDebits();
        walletRejectsInvalidOperations();
        vesselUnlockAndUpgrades();
        vesselRejectsInvalidState();
        rewardsAreIdempotent();
        contractsRespectCooldowns();
        skillLevelsFollowCurve();
        vesselRuntimeLinksAreValidated();
        wakeCompassBearingIsStable();
        vesselUpgradeQuotesFollowProgression();
        vesselHoldCapacityIsProgressive();
        repairQuotesScaleWithDamage();
        catchGenerationIsDeterministic();
        catchFreshnessAgesWithoutTicking();
        catchValueUsesEveryMultiplier();
        vanillaFishProfilesAreComplete();
        System.out.println("DomainSelfTest: OK");
    }

    private static void walletCreditsAndDebits() {
        TideWallet wallet = TideWallet.empty().credit(125).debit(25);
        check(wallet.balance() == 100, "wallet balance");
        check(wallet.canAfford(100), "wallet exact affordability");
        check(!wallet.canAfford(101), "wallet insufficient affordability");
    }

    private static void walletRejectsInvalidOperations() {
        expect(IllegalArgumentException.class, () -> TideWallet.empty().credit(0));
        expect(IllegalStateException.class, () -> TideWallet.empty().debit(1));
        expect(IllegalArgumentException.class, () -> new TideWallet(TideWallet.MAX_BALANCE).credit(1));
    }

    private static void vesselUnlockAndUpgrades() {
        PlayerVessel locked = PlayerVessel.locked();
        check(!locked.unlocked(), "locked vessel");

        PlayerVessel vessel = PlayerVessel.unlock("  L'Écumeur  ", UUID.randomUUID())
                .upgradeHull()
                .upgradeMotor()
                .upgradeHold()
                .addModuleSlot();
        check(vessel.name().equals("L'Écumeur"), "normalized vessel name");
        check(vessel.hullTier() == 2, "hull upgrade");
        check(vessel.motorTier() == 2, "motor upgrade");
        check(vessel.holdTier() == 2, "hold upgrade");
        check(vessel.moduleSlots() == 2, "module slot upgrade");
    }

    private static void vesselRejectsInvalidState() {
        expect(IllegalStateException.class, () -> PlayerVessel.locked().upgradeHull());
        expect(IllegalArgumentException.class,
                () -> new PlayerVessel("", "Invalide", true, 1, 1, 1, 1));
        expect(IllegalArgumentException.class,
                () -> PlayerVessel.unlock(" ", UUID.randomUUID()));
    }

    private static void rewardsAreIdempotent() {
        PlayerProgress progress = PlayerProgress.empty()
                .claimReceipt("ftb:first_sale")
                .addSkillXp(Map.of("trade", 50L));
        check(progress.hasReceipt("ftb:first_sale"), "claimed reward receipt");
        check(progress.skillXp("trade") == 50, "skill XP reward");
        expect(IllegalStateException.class, () -> progress.claimReceipt("ftb:first_sale"));

        PlayerProgress milestone = progress.completeMilestone("tidebound:first_sale");
        check(milestone.hasCompletedMilestone("tidebound:first_sale"), "completed milestone");
        expect(IllegalStateException.class,
                () -> milestone.completeMilestone("tidebound:first_sale"));
    }

    private static void contractsRespectCooldowns() {
        PlayerProgress first = PlayerProgress.empty()
                .completeContract("tidebound:coastal_delivery", 1_000, 24_000);
        ContractProgress state = first.contract("tidebound:coastal_delivery");
        check(state.completionCount() == 1, "contract completion count");
        check(!state.isAvailable(24_999), "contract cooldown active");
        check(state.isAvailable(25_000), "contract cooldown elapsed");
        expect(IllegalStateException.class,
                () -> first.completeContract("tidebound:coastal_delivery", 24_999, 24_000));
    }

    private static void skillLevelsFollowCurve() {
        check(SkillProgression.levelForXp(0) == 1, "initial skill level");
        check(SkillProgression.levelForXp(99) == 1, "level one ceiling");
        check(SkillProgression.levelForXp(100) == 2, "level two threshold");
        check(SkillProgression.levelForXp(4_700) == 10, "maximum skill level");
        check(SkillProgression.xpUntilNextLevel(249) == 1, "XP to next level");
        check(SkillProgression.xpUntilNextLevel(4_700) == 0, "maximum level progress");
    }

    private static void vesselRuntimeLinksAreValidated() {
        UUID owner = UUID.randomUUID();
        UUID vessel = UUID.randomUUID();
        VesselEntityLink link = VesselEntityLink.linked(owner, vessel.toString());
        check(link.linked(), "physical vessel link");
        check(link.belongsTo(owner), "physical vessel ownership");
        check(!link.belongsTo(UUID.randomUUID()), "physical vessel ownership rejection");

        VesselDeployment deployment = VesselDeployment.active(
                UUID.randomUUID(), "minecraft:overworld", 200, 63, -60, 12, -4);
        check(deployment.active(), "active vessel deployment");
        check(deployment.hasKnownPosition(), "known vessel position");
        check(deployment.markMissing().state() == VesselDeploymentState.MISSING,
                "missing vessel keeps last position");
        check(deployment.markDestroyed().state() == VesselDeploymentState.DESTROYED,
                "destroyed vessel keeps last position");
        check(!VesselDeployment.docked().active(), "docked vessel deployment");
        expect(IllegalArgumentException.class,
                () -> new VesselDeployment(UUID.randomUUID().toString(), "",
                        0, 0, 0, 0, 0, VesselDeploymentState.DEPLOYED));
    }

    private static void wakeCompassBearingIsStable() {
        check(WakeBearing.direction(0, -10).equals("nord"), "north bearing");
        check(WakeBearing.direction(10, 0).equals("est"), "east bearing");
        check(WakeBearing.direction(-10, 10).equals("sud-ouest"), "south-west bearing");
        check(WakeBearing.direction(0, 0).equals("ici"), "same-position bearing");
        check(WakeBearing.distance(3, 4) == 5, "horizontal compass distance");
    }

    private static void vesselUpgradeQuotesFollowProgression() {
        PlayerVessel vessel = PlayerVessel.unlock("La Vigie", UUID.randomUUID());
        VesselUpgradeQuote hull = VesselUpgradeQuote.next(vessel, VesselUpgrade.HULL);
        check(hull.targetTier() == 2, "hull quote target");
        check(hull.tideCost() == 120, "hull quote tides");
        check(hull.materialItemId().equals("minecraft:oak_planks"), "hull quote material");
        check(hull.requiredSkill().equals("navigation") && hull.requiredSkillLevel() == 2,
                "hull quote skill");

        VesselUpgradeQuote hold = VesselUpgradeQuote.next(vessel.upgradeHold(), VesselUpgrade.HOLD);
        check(hold.targetTier() == 3 && hold.tideCost() == 275, "second hold quote");

        PlayerVessel maximum = vessel.upgradeHull().upgradeHull().upgradeHull().upgradeHull();
        expect(IllegalStateException.class, () -> VesselUpgradeQuote.next(maximum, VesselUpgrade.HULL));
    }

    private static void vesselHoldCapacityIsProgressive() {
        check(VesselHoldPolicy.usableSlots(1) == 9, "hold tier one slots");
        check(VesselHoldPolicy.usableSlots(2) == 18, "hold tier two slots");
        check(VesselHoldPolicy.usableSlots(3) == 27, "hold tier three slots");
        check(VesselHoldPolicy.usableSlots(5) == 27, "hold tier five vanilla ceiling");
        expect(IllegalArgumentException.class, () -> VesselHoldPolicy.usableSlots(0));
    }

    private static void repairQuotesScaleWithDamage() {
        VesselRepairQuote light = VesselRepairQuote.forDamage(1.0F);
        check(light.tideCost() == 10 && light.materialCount() == 1, "light repair quote");
        VesselRepairQuote heavy = VesselRepairQuote.forDamage(26.0F);
        check(heavy.tideCost() == 60 && heavy.materialCount() == 3, "heavy repair quote");
        expect(IllegalArgumentException.class, () -> VesselRepairQuote.forDamage(0));
    }

    private static void catchGenerationIsDeterministic() {
        CatchProfile cod = CatchProfiles.find("minecraft:cod").orElseThrow();
        CatchData first = CatchGenerator.generate(cod, "minecraft:ocean", 42_000, 123_456_789L, 4, true);
        CatchData second = CatchGenerator.generate(cod, "minecraft:ocean", 42_000, 123_456_789L, 4, true);
        check(first.equals(second), "deterministic catch generation");
        check(first.weightGrams() >= cod.minWeightGrams()
                && first.weightGrams() <= cod.maxWeightGrams(), "generated catch weight range");
        check(first.speciesId().equals("minecraft:cod"), "generated catch species");
        check(first.originBiomeId().equals("minecraft:ocean"), "generated catch origin");
        expect(IllegalArgumentException.class,
                () -> CatchGenerator.generate(cod, "minecraft:ocean", 1, 2, 0, false));
    }

    private static void catchFreshnessAgesWithoutTicking() {
        CatchData data = new CatchData("minecraft:cod", 1_800, CatchQuality.COMMON,
                1_000, "minecraft:ocean", CatchAnomaly.NONE);
        check(data.freshness(1_000) == CatchFreshness.FRESH, "fresh catch");
        check(data.freshness(25_000) == CatchFreshness.AGED, "aging catch");
        check(data.freshness(73_000) == CatchFreshness.STALE, "stale catch");
        check(data.freshness(145_000) == CatchFreshness.SPOILED, "spoiled catch");
        check(data.freshness(0) == CatchFreshness.FRESH, "time rollback does not spoil catches");
    }

    private static void catchValueUsesEveryMultiplier() {
        CatchProfile profile = new CatchProfile("tidebound:test_fish", 500, 2_000, 1_000, 20);
        CatchData ordinary = new CatchData("tidebound:test_fish", 1_000, CatchQuality.COMMON,
                0, "minecraft:ocean", CatchAnomaly.NONE);
        CatchData remarkable = new CatchData("tidebound:test_fish", 1_000, CatchQuality.EXCEPTIONAL,
                0, "minecraft:ocean", CatchAnomaly.INK_VEINED);
        check(CatchValuation.value(profile, ordinary, 0) == 20, "ordinary catch value");
        check(CatchValuation.value(profile, remarkable, 0) == 105, "quality and anomaly value");
        check(CatchValuation.value(profile, remarkable, CatchFreshness.FRESH_TICKS) == 89,
                "freshness value loss");
        expect(IllegalArgumentException.class,
                () -> CatchValuation.value(CatchProfiles.find("minecraft:cod").orElseThrow(), ordinary, 0));
    }

    private static void vanillaFishProfilesAreComplete() {
        check(CatchProfiles.all().size() == 4, "vanilla fish profile count");
        check(CatchProfiles.find("minecraft:cod").isPresent(), "cod profile");
        check(CatchProfiles.find("minecraft:salmon").isPresent(), "salmon profile");
        check(CatchProfiles.find("minecraft:tropical_fish").isPresent(), "tropical fish profile");
        check(CatchProfiles.find("minecraft:pufferfish").isPresent(), "pufferfish profile");
        check(CatchProfiles.find("minecraft:stick").isEmpty(), "non-fish profile rejection");
    }

    private static void check(boolean condition, String label) {
        if (!condition) {
            throw new AssertionError(label);
        }
    }

    private static void expect(Class<? extends Throwable> expected, Runnable action) {
        try {
            action.run();
        } catch (Throwable actual) {
            if (expected.isInstance(actual)) {
                return;
            }
            throw new AssertionError("Expected " + expected.getSimpleName() + " but got "
                    + actual.getClass().getSimpleName(), actual);
        }
        throw new AssertionError("Expected " + expected.getSimpleName());
    }
}
