package warehouse_management.com.warehouse_management.enumerate;

import org.springframework.lang.Nullable;

public enum TransferTicketStatus implements EnumClass<String> {

    PENDING_APPROVAL("PENDING_APPROVAL"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED"),
    COMPLETED("COMPLETED");

    private final String id;

    TransferTicketStatus(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static TransferTicketStatus fromId(String id) {
        for (TransferTicketStatus at : TransferTicketStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}