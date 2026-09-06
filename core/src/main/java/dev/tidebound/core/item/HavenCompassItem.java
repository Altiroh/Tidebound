package dev.tidebound.core.item;

import dev.tidebound.core.registry.TideboundDataComponents;
import dev.tidebound.core.service.HavenCompassService;
import java.util.Optional;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Early-game safety net that reads the persistent harbour index. */
public final class HavenCompassItem extends Item {
    private static final int TARGET_REFRESH_TICKS = 20;

    public HavenCompassItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            HavenCompassService.read(serverPlayer);
            serverPlayer.getCooldowns().addCooldown(this, 20);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide() || !(entity instanceof ServerPlayer player)
                || player.tickCount % TARGET_REFRESH_TICKS != 0) {
            return;
        }
        Optional<GlobalPos> target = HavenCompassService.resolveTarget(player);
        if (target.isPresent()) {
            stack.set(TideboundDataComponents.COMPASS_TARGET.get(), target.get());
        } else {
            stack.remove(TideboundDataComponents.COMPASS_TARGET.get());
        }
    }
}
