package dev.tidebound.core.event;

import dev.tidebound.core.registry.TideboundEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Fast Smelting (TB-SMELT-001) has no vanilla enchantment effect component to hook into, so the
 * boost is applied by hand while a player actively watches an open furnace menu. Automated lines
 * (hoppers, Create) never have a menu open and are therefore never affected.
 */
public final class FastSmeltingEvents {
    private FastSmeltingEvents() {
    }

    public static void register(IEventBus gameBus) {
        gameBus.addListener(FastSmeltingEvents::onPlayerTick);
    }

    private static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (!(player.containerMenu instanceof AbstractFurnaceMenu furnaceMenu)) {
            return;
        }

        int enchantLevel = enchantmentLevel(player);
        if (enchantLevel <= 0) {
            return;
        }

        if (!(furnaceMenu.container instanceof AbstractFurnaceBlockEntity furnace)) {
            return;
        }
        if (furnace.litTime <= 0 || furnace.cookingTotalTime <= 0) {
            return;
        }

        int boosted = Math.min(furnace.cookingProgress + enchantLevel, furnace.cookingTotalTime - 1);
        if (boosted != furnace.cookingProgress) {
            furnace.cookingProgress = boosted;
            furnace.setChanged();
        }
    }

    private static int enchantmentLevel(ServerPlayer player) {
        Holder<Enchantment> fastSmelting = player.level().registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(TideboundEnchantments.FAST_SMELTING);
        int mainHand = EnchantmentHelper.getItemEnchantmentLevel(fastSmelting, player.getMainHandItem());
        int offHand = EnchantmentHelper.getItemEnchantmentLevel(fastSmelting, player.getOffhandItem());
        return Math.max(mainHand, offHand);
    }
}
