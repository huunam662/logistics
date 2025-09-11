package warehouse_management.com.warehouse_management.enumerate;

import org.springframework.lang.Nullable;

public enum RepairStatus {
    IN_REPAIR("IN_REPAIR"),
    COMPLETE("COMPLETE"),
    EXPIRED("EXPIRED");

    private final String id;

    RepairStatus(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static RepairStatus fromId(String id) {
        for (RepairStatus at : RepairStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}
