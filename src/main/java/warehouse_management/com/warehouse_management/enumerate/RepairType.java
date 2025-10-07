package warehouse_management.com.warehouse_management.enumerate;

import lombok.Getter;

@Getter
public enum RepairType {

    DISASSEMBLE("DISASSEMBLE", "Tháo rời"),
    ASSEMBLE("ASSEMBLE", "Lắp ráp"),
    REPAIR("REPAIR", "Sửa chữa");

    private final String id;
    private final String value;

    RepairType(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public static RepairType fromString(String string) {
        for (RepairType repairType : RepairType.values()) {
            if (repairType.toString().equalsIgnoreCase(string)) {
                return repairType;
            }
        }
        return null;
    }

}
