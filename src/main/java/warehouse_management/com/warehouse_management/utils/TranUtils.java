package warehouse_management.com.warehouse_management.utils;

import org.springframework.stereotype.Component;
import warehouse_management.com.warehouse_management.enumerate.WarehouseSubTranType;
import warehouse_management.com.warehouse_management.enumerate.WarehouseTranType;
import warehouse_management.com.warehouse_management.enumerate.WarehouseTransactionStatus;
import warehouse_management.com.warehouse_management.enumerate.WarehouseType;
import warehouse_management.com.warehouse_management.model.Container;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.model.Warehouse;
import warehouse_management.com.warehouse_management.model.WarehouseTransaction;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class TranUtils {
    public String generateTranTitle(WarehouseTranType tranType, WarehouseSubTranType subTranType, Warehouse wh1, Warehouse wh2) {
        if (wh1 == null) {
            throw new IllegalArgumentException("Primary warehouse cannot be null");
        }
        if (tranType == null) {
            throw new IllegalArgumentException("Transaction type cannot be null");
        }

        String wh1Name = (wh1.getName() != null && !wh1.getName().isBlank()) ? wh1.getName() : "Unknown Warehouse";
        String tranTypeTitle = (tranType.getTitle() != null && !tranType.getTitle().isBlank()) ? tranType.getTitle() : tranType.getId();
        String subTranTypeTitle = (subTranType != null)
                ? ((subTranType.getTitle() != null && !subTranType.getTitle().isBlank()) ? subTranType.getTitle() : subTranType.getId())
                : "";

        StringBuilder title = new StringBuilder();
        title.append(wh1Name);

        if (wh2 != null) {
            String wh2Name = (wh2.getName() != null && !wh2.getName().isBlank()) ? wh2.getName() : "Unknown Warehouse";
            title.append(" → ").append(wh2Name);
        }

        // dùng appendWhenNotBlank thay cho lặp lại điều kiện
        appendWhenNotBlank(title, tranTypeTitle);
        appendWhenNotBlank(title, subTranTypeTitle);

        return title.toString();
    }

    private void appendWhenNotBlank(StringBuilder sb, String input) {
        if (input != null && !input.isBlank()) {
            sb.append(" - ").append(input);
        }
    }


}
