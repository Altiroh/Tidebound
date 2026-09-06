package dev.tidebound.core.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

/**
 * Generic "something real happened server-side" trigger, fired from Core services at their actual
 * success path (vessel registered, contract delivered, upgrade purchased...). FTB Quests tasks watch
 * the matching hidden advancement instead of a manual checkmark, so a reward cannot be claimed
 * without the underlying server state actually changing.
 */
public final class TideboundSignalTrigger extends SimpleCriterionTrigger<TideboundSignalTrigger.TriggerInstance> {
    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, String signal) {
        trigger(player, (Predicate<TriggerInstance>) instance -> instance.matches(signal));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, String signal)
            implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                Codec.STRING.fieldOf("signal").forGetter(TriggerInstance::signal)
        ).apply(instance, TriggerInstance::new));

        public boolean matches(String candidate) {
            return signal.equals(candidate);
        }
    }
}
