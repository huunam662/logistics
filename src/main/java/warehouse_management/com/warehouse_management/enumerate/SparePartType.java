package warehouse_management.com.warehouse_management.enumerate;


import org.springframework.lang.Nullable;

public enum SparePartType {
    ENGINE("ENGINE"),       // Động cơ
    FORK("FORK"),         // Càng nâng
    VALVE("VALVE"),        // Van
    SIDESHIFT("SIDESHIFT");// Side shift
    private final String id;

    SparePartType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static SparePartType fromId(String id) {
        for (SparePartType at : SparePartType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}

