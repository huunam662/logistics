package warehouse_management.com.warehouse_management.enumerate;

import org.springframework.lang.Nullable;

public enum WarrantyStatus implements EnumClass<String>  {
    IN_WARRANTY("IN_WARRANTY"),
    COMPLETE("COMPLETE"),
    EXPIRED("EXPIRED");

    private final String id;

    WarrantyStatus(String id) {
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
