package dev.tidebound.core.service;

import dev.tidebound.core.data.TideWallet;
import dev.tidebound.core.registry.TideboundAttachments;
import net.minecraft.server.level.ServerPlayer;

public final class TideEconomy {
    private TideEconomy() {
    }

    public static TideWallet wallet(ServerPlayer player) {
        return player.getData(TideboundAttachments.TIDE_WALLET);
    }

    public static TideWallet grant(ServerPlayer player, long amount) {
        TideWallet updated = wallet(player).credit(amount);
        player.setData(TideboundAttachments.TIDE_WALLET, updated);
        return updated;
    }

    public static boolean trySpend(ServerPlayer player, long amount) {
        TideWallet current = wallet(player);
        if (!current.canAfford(amount)) {
            return false;
        }
        player.setData(TideboundAttachments.TIDE_WALLET, current.debit(amount));
        return true;
    }

    static void set(ServerPlayer player, TideWallet wallet) {
        player.setData(TideboundAttachments.TIDE_WALLET, wallet);
    }
}
