package dev.tidebound.core.service;

import dev.tidebound.core.fishing.CatchData;
import dev.tidebound.core.fishing.CatchGenerator;
import dev.tidebound.core.fishing.CatchProfile;
import dev.tidebound.core.fishing.CatchProfiles;
import dev.tidebound.core.fishing.CatchValuation;
import dev.tidebound.core.progression.SkillProgression;
import dev.tidebound.core.registry.TideboundDataComponents;
import java.util.Optional;
import java.util.OptionalLong;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.item.ItemStack;

/** Bridges pure catch rules with Minecraft ItemStacks. */
public final class CatchService {
    private CatchService() {
    }

    public static Optional<CatchData> data(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(stack.get(TideboundDataComponents.CATCH_DATA.get()));
    }

    public static Optional<CatchData> stampFishedItem(ServerPlayer player, ItemStack stack, BlockPos catchPosition) {
        if (stack == null || stack.isEmpty()) {
            return Optional.empty();
        }
        if (data(stack).isPresent()) {
            return Optional.empty();
        }
        String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        Optional<CatchProfile> profile = CatchProfiles.find(itemId);
        if (profile.isEmpty()) {
            return Optional.empty();
        }

        ServerLevel level = player.serverLevel();
        long gameTime = player.getServer().overworld().getGameTime();
        String biomeId = level.getBiome(catchPosition).unwrapKey()
                .map(key -> key.location().toString())
                .orElse("minecraft:unknown");
        boolean ocean = level.getBiome(catchPosition).is(BiomeTags.IS_OCEAN);
        long timeOfDay = Math.floorMod(level.getDayTime(), 24_000L);
        boolean eerieWater = ocean && timeOfDay >= 13_000L && timeOfDay <= 23_000L;
        int fishingLevel = SkillProgression.levelForXp(ProgressionService.progress(player).skillXp("fishing"));
        long seed = player.getRandom().nextLong() ^ catchPosition.asLong() ^ gameTime;
        CatchData catchData = CatchGenerator.generate(
                profile.orElseThrow(), biomeId, gameTime, seed, fishingLevel, eerieWater);
        stack.set(TideboundDataComponents.CATCH_DATA.get(), catchData);
        return Optional.of(catchData);
    }

    public static OptionalLong value(ItemStack stack, long currentGameTime) {
        Optional<CatchData> data = data(stack);
        if (data.isEmpty()) {
            return OptionalLong.empty();
        }
        Optional<CatchProfile> profile = CatchProfiles.find(data.orElseThrow().speciesId());
        if (profile.isEmpty()) {
            return OptionalLong.empty();
        }
        return OptionalLong.of(CatchValuation.value(profile.orElseThrow(), data.orElseThrow(), currentGameTime));
    }
}
