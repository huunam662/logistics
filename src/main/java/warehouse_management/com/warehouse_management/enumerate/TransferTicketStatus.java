package warehouse_management.com.warehouse_management.enumerate;

import org.springframework.lang.Nullable;

import java.util.Arrays;

public enum TransferTicketStatus implements EnumClass<String> {

    PENDING("PENDING"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED");

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

    public static boolean contains(String id) {
        TransferTicketStatus status = fromId(id);
        if(status == null) return false;
        return Arrays.stream(values()).toList().contains(status);
    }
}