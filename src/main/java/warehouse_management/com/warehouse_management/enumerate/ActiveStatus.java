package warehouse_management.com.warehouse_management.enumerate;

import org.springframework.lang.Nullable;

public enum ActiveStatus {

    ACTIVE("ACTIVE"),   // HOẠT ĐỘNG
    INACTIVE("INACTIVE"); // KHÔNG HOẠT ĐỘNG

    private final String id;

    ActiveStatus(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static ActiveStatus fromId(String id) {
        for (ActiveStatus at : ActiveStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}