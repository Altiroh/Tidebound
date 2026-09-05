package dev.tidebound.core.service;

import dev.tidebound.core.data.PlayerProgress;
import dev.tidebound.core.data.TideWallet;
import dev.tidebound.core.npc.PortNpcRole;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

/** Server-authoritative valuation and atomic sale of stamped Tidebound catches. */
public final class FishmongerService {
    private FishmongerService() {
    }

    public static SaleQuote estimateInventory(ServerPlayer player) {
        long gameTime = player.serverLevel().getGameTime();
        long tides = 0;
        int catches = 0;
        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            long unitValue = CatchService.value(stack, gameTime).orElse(0L);
            if (unitValue <= 0) {
                continue;
            }
            catches = Math.addExact(catches, stack.getCount());
            tides = Math.addExact(tides, Math.multiplyExact(unitValue, stack.getCount()));
        }
        return new SaleQuote(catches, tides, tradeXp(tides, catches));
    }

    public static SaleQuote sellInventory(ServerPlayer player) {
        if (!HarborBoardService.isNearRole(player, PortNpcRole.FISHMONGER)) {
            throw new IllegalStateException("Approchez-vous du poissonnier.");
        }
        SaleQuote quote = estimateInventory(player);
        if (quote.empty()) {
            throw new IllegalStateException("Aucune prise Tidebound à vendre.");
        }

        TideWallet updatedWallet = TideEconomy.wallet(player).credit(quote.tides());
        PlayerProgress updatedProgress = ProgressionService.progress(player)
                .addSkillXp(Map.of("trade", quote.tradeXp()));

        Inventory inventory = player.getInventory();
        List<Integer> soldSlots = new ArrayList<>();
        long gameTime = player.serverLevel().getGameTime();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            if (CatchService.value(inventory.getItem(slot), gameTime).orElse(0L) > 0) {
                soldSlots.add(slot);
            }
        }
        for (int slot : soldSlots) {
            inventory.setItem(slot, ItemStack.EMPTY);
        }
        inventory.setChanged();
        TideEconomy.set(player, updatedWallet);
        ProgressionService.set(player, updatedProgress);
        return quote;
    }

    private static long tradeXp(long tides, int catches) {
        return Math.max(catches, Math.max(1L, tides / 10L));
    }

    public record SaleQuote(int catches, long tides, long tradeXp) {
        public SaleQuote {
            if (catches < 0 || tides < 0 || tradeXp < 0) {
                throw new IllegalArgumentException("Sale quote values cannot be negative");
            }
        }

        public boolean empty() {
            return catches == 0;
        }
    }
}
