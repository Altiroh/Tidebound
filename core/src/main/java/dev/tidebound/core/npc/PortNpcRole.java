package dev.tidebound.core.npc;

/** Stable role ids shared by entity factories, menus and client presentation. */
public enum PortNpcRole {
    INTENDANT(0, "intendant"),
    SHIPWRIGHT(1, "shipwright"),
    FISHMONGER(2, "fishmonger"),
    NATURALIST(3, "naturalist"),
    LIGHTHOUSE_KEEPER(4, "lighthouse_keeper");

    private final int networkId;
    private final String id;

    PortNpcRole(int networkId, String id) {
        this.networkId = networkId;
        this.id = id;
    }

    public int networkId() {
        return networkId;
    }

    public String id() {
        return id;
    }

    public String translationKey() {
        return "entity.tidebound.port_npc." + id;
    }

    public static PortNpcRole fromNetworkId(int id) {
        for (PortNpcRole role : values()) {
            if (role.networkId == id) {
                return role;
            }
        }
        return INTENDANT;
    }
}
