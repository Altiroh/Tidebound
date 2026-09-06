package dev.tidebound.core.registry;

import dev.tidebound.core.TideboundCore;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

/** Keys for the data-driven enchantments defined under {@code data/tidebound/enchantment/}. */
public final class TideboundEnchantments {
    public static final ResourceKey<Enchantment> FAST_SMELTING = ResourceKey.create(
            Registries.ENCHANTMENT,
            ResourceLocation.fromNamespaceAndPath(TideboundCore.MOD_ID, "fast_smelting"));

    private TideboundEnchantments() {
    }
}
