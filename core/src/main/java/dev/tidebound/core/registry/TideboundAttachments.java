package dev.tidebound.core.registry;

import dev.tidebound.core.TideboundCore;
import dev.tidebound.core.data.PlayerVessel;
import dev.tidebound.core.data.PlayerProgress;
import dev.tidebound.core.data.TideWallet;
import dev.tidebound.core.data.TideboundCodecs;
import dev.tidebound.core.data.VesselDeployment;
import dev.tidebound.core.data.VesselEntityLink;
import java.util.function.Supplier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class TideboundAttachments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, TideboundCore.MOD_ID);

    public static final Supplier<AttachmentType<TideWallet>> TIDE_WALLET = ATTACHMENT_TYPES.register(
            "tide_wallet",
            () -> AttachmentType.builder(TideWallet::empty)
                    .serialize(TideboundCodecs.TIDE_WALLET)
                    .copyOnDeath()
                    .sync((holder, to) -> holder == to, TideboundCodecs.TIDE_WALLET_STREAM)
                    .build()
    );

    public static final Supplier<AttachmentType<PlayerVessel>> PLAYER_VESSEL = ATTACHMENT_TYPES.register(
            "player_vessel",
            () -> AttachmentType.builder(PlayerVessel::locked)
                    .serialize(TideboundCodecs.PLAYER_VESSEL)
                    .copyOnDeath()
                    .build()
    );

    public static final Supplier<AttachmentType<PlayerProgress>> PLAYER_PROGRESS = ATTACHMENT_TYPES.register(
            "player_progress",
            () -> AttachmentType.builder(PlayerProgress::empty)
                    .serialize(TideboundCodecs.PLAYER_PROGRESS)
                    .copyOnDeath()
                    .build()
    );

    public static final Supplier<AttachmentType<VesselEntityLink>> VESSEL_ENTITY_LINK = ATTACHMENT_TYPES.register(
            "vessel_entity_link",
            () -> AttachmentType.builder(VesselEntityLink::unlinked)
                    .serialize(TideboundCodecs.VESSEL_ENTITY_LINK)
                    .build()
    );

    public static final Supplier<AttachmentType<VesselDeployment>> VESSEL_DEPLOYMENT = ATTACHMENT_TYPES.register(
            "vessel_deployment",
            () -> AttachmentType.builder(VesselDeployment::docked)
                    .serialize(TideboundCodecs.VESSEL_DEPLOYMENT)
                    .copyOnDeath()
                    .build()
    );

    private TideboundAttachments() {
    }

    public static void register(IEventBus modBus) {
        ATTACHMENT_TYPES.register(modBus);
    }
}
