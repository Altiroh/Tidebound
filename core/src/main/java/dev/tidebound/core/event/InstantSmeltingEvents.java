package dev.tidebound.core.event;

import dev.tidebound.core.registry.TideboundEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

/**
 * Instant Smelting (TB-SMELT-001, redesigned after playtest feedback): the previous "tend an open
 * furnace" mechanic didn't match what players expected from the name. Renamed to match the actual
 * effect — mining with the enchanted tool smelts eligible drops (anything with a vanilla furnace
 * recipe) on the spot, exactly like Silk Touch/Fortune-style loot modifiers.
 */
public final class InstantSmeltingEvents {
    private InstantSmeltingEvents() {
    }

    public static void register(IEventBus gameBus) {
        gameBus.addListener(InstantSmeltingEvents::onBlockDrops);
    }

    private static void onBlockDrops(BlockDropsEvent event) {
        ItemStack tool = event.getTool();
        if (tool.isEmpty()) {
            return;
        }

        ServerLevel level = event.getLevel();
        Holder<Enchantment> instantSmelting = level.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(TideboundEnchantments.INSTANT_SMELTING);
        if (EnchantmentHelper.getItemEnchantmentLevel(instantSmelting, tool) <= 0) {
            return;
        }

        for (ItemEntity itemEntity : event.getDrops()) {
            ItemStack stack = itemEntity.getItem();
            level.getRecipeManager()
                    .getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(stack), level)
                    .ifPresent(recipe -> {
                        ItemStack smelted = recipe.value().getResultItem(level.registryAccess()).copy();
                        smelted.setCount(stack.getCount() * smelted.getCount());
                        itemEntity.setItem(smelted);
                    });
        }
    }
}
