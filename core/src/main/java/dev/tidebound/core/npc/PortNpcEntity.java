package dev.tidebound.core.npc;

import dev.tidebound.core.service.HarborBoardService;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** A villager-compatible Tidebound NPC whose service is fixed by its registered entity type. */
public final class PortNpcEntity extends Villager {
    private final PortNpcRole role;

    public PortNpcEntity(EntityType<? extends Villager> type, Level level, PortNpcRole role) {
        super(type, level);
        this.role = role;
        setCustomName(Component.translatable(role.translationKey()));
        setCustomNameVisible(true);
        setPersistenceRequired();
    }

    public PortNpcRole role() {
        return role;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            HarborBoardService.open(serverPlayer, role);
        }
        return InteractionResult.sidedSuccess(level().isClientSide());
    }
}
