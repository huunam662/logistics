package warehouse_management.com.warehouse_management.enumerate;

import org.springframework.lang.Nullable;

public enum ContainerStatus implements EnumClass<String> {

    PENDING("PENDING"),
    APPROVED("APPROVED"),
    HAD_DATE("HAD_DATE"),
    IN_TRANSIT("IN_TRANSIT"),
    UN_INSPECTED("UN_INSPECTED"),
    COMPLETED("COMPLETED");

    private final String id;

    ContainerStatus(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static ContainerStatus fromId(String id) {
        for (ContainerStatus status : ContainerStatus.values()) {
            if (status.getId().equals(id)) {
                return status;
            }
        }
        return null;
    }
}