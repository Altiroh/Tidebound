package dev.tidebound.core;

import dev.tidebound.core.command.TideboundCommands;
import dev.tidebound.core.content.TideboundContentManager;
import dev.tidebound.core.event.TideboundGameplayEvents;
import dev.tidebound.core.registry.TideboundAttachments;
import dev.tidebound.core.registry.TideboundItems;
import dev.tidebound.core.service.HarborBoardService;
import dev.tidebound.core.service.VesselDeploymentService;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(TideboundCore.MOD_ID)
public final class TideboundCore {
    public static final String MOD_ID = "tidebound";

    public TideboundCore(IEventBus modBus) {
        TideboundAttachments.register(modBus);
        TideboundItems.register(modBus);
        NeoForge.EVENT_BUS.addListener(TideboundCommands::register);
        NeoForge.EVENT_BUS.addListener(TideboundContentManager::register);
        TideboundGameplayEvents.register(NeoForge.EVENT_BUS);
        HarborBoardService.register(NeoForge.EVENT_BUS);
        VesselDeploymentService.register(NeoForge.EVENT_BUS);
    }
}
