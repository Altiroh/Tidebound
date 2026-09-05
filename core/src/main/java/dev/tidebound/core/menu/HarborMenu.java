package dev.tidebound.core.menu;

import dev.tidebound.core.data.PlayerVessel;
import dev.tidebound.core.registry.TideboundMenus;
import dev.tidebound.core.service.HarborBoardService;
import dev.tidebound.core.service.TideEconomy;
import dev.tidebound.core.service.VesselDeploymentService;
import dev.tidebound.core.service.VesselMaintenanceService;
import dev.tidebound.core.service.VesselService;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import dev.tidebound.core.vessel.TideboundVesselEntity;

/**
 * Server-authoritative backend for the harbour screen. It deliberately exposes only compact visual state;
 * all mutations still go through the existing command/API validation path.
 */
public final class HarborMenu extends AbstractContainerMenu {
    public static final int DATA_COUNT = 9;

    public static final int ACTION_CLAIM = 0;
    public static final int ACTION_REGISTER = 1;
    public static final int ACTION_DEPLOY = 2;
    public static final int ACTION_COMPASS = 3;
    public static final int ACTION_REPAIR = 4;
    public static final int ACTION_CONTRACTS = 5;
    public static final int ACTION_REFIT = 6;
    public static final int ACTION_HULL = 10;
    public static final int ACTION_MOTOR = 11;
    public static final int ACTION_HOLD = 12;
    public static final int ACTION_MODULE = 13;

    private final ContainerData data;
    private final ServerPlayer serverPlayer;

    /** Client constructor used by the registered MenuType. */
    public HarborMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainerData(DATA_COUNT), null);
    }

    public HarborMenu(int containerId, Inventory inventory, ServerPlayer player) {
        this(containerId, inventory, createServerData(player), player);
    }

    private HarborMenu(int containerId, Inventory inventory, ContainerData data, ServerPlayer player) {
        super(TideboundMenus.HARBOR.get(), containerId);
        checkContainerDataCount(data, DATA_COUNT);
        this.data = data;
        this.serverPlayer = player;
        addDataSlots(data);
    }

    @Override
    public boolean stillValid(Player player) {
        return serverPlayer == null || HarborBoardService.isNearBoard(serverPlayer);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean clickMenuButton(Player player, int action) {
        if (!(player instanceof ServerPlayer server) || !HarborBoardService.isNearBoard(server)) {
            return false;
        }
        String command = commandFor(action);
        if (command == null) {
            return false;
        }
        if (action == ACTION_CONTRACTS) {
            server.closeContainer();
        }
        server.getServer().getCommands().performPrefixedCommand(server.createCommandSourceStack(), command);
        broadcastChanges();
        return true;
    }

    public int tides() {
        return data.get(0);
    }

    public boolean vesselUnlocked() {
        return data.get(1) != 0;
    }

    public int hullTier() {
        return data.get(2);
    }

    public int motorTier() {
        return data.get(3);
    }

    public int holdTier() {
        return data.get(4);
    }

    public int moduleSlots() {
        return data.get(5);
    }

    public boolean deployed() {
        return data.get(6) != 0;
    }

    public boolean repairAvailable() {
        return data.get(7) != 0;
    }

    public boolean refitAvailable() {
        return data.get(8) != 0;
    }

    private static String commandFor(int action) {
        return switch (action) {
            case ACTION_CLAIM -> "tidebound vessel claim";
            case ACTION_REGISTER -> "tidebound vessel register";
            case ACTION_DEPLOY -> "tidebound vessel deploy";
            case ACTION_COMPASS -> "tidebound vessel compass";
            case ACTION_REPAIR -> "tidebound vessel repair";
            case ACTION_CONTRACTS -> "tidebound contracts";
            case ACTION_REFIT -> "tidebound vessel refit";
            case ACTION_HULL -> "tidebound vessel purchase hull";
            case ACTION_MOTOR -> "tidebound vessel purchase motor";
            case ACTION_HOLD -> "tidebound vessel purchase hold";
            case ACTION_MODULE -> "tidebound vessel purchase module";
            default -> null;
        };
    }

    private static ContainerData createServerData(ServerPlayer player) {
        return new ContainerData() {
            @Override
            public int get(int index) {
                PlayerVessel vessel = VesselService.vessel(player);
                return switch (index) {
                    case 0 -> (int) Math.min(Integer.MAX_VALUE, TideEconomy.wallet(player).balance());
                    case 1 -> vessel.unlocked() ? 1 : 0;
                    case 2 -> vessel.hullTier();
                    case 3 -> vessel.motorTier();
                    case 4 -> vessel.holdTier();
                    case 5 -> vessel.moduleSlots();
                    case 6 -> VesselDeploymentService.deployment(player).active() ? 1 : 0;
                    case 7 -> VesselMaintenanceService.nearbyPhysicalVessel(player)
                            .filter(boat -> boat.getDamage() > 0.01F).isPresent() ? 1 : 0;
                    case 8 -> VesselMaintenanceService.nearbyPhysicalVessel(player)
                            .filter(boat -> !(boat instanceof TideboundVesselEntity)).isPresent() ? 1 : 0;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                // The server owns these values. Client writes are intentionally ignored.
            }

            @Override
            public int getCount() {
                return DATA_COUNT;
            }
        };
    }
}
