package warehouse_management.com.warehouse_management.utils;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import warehouse_management.com.warehouse_management.enumerate.WarehouseSubTranType;
import warehouse_management.com.warehouse_management.enumerate.WarehouseTranType;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;
import warehouse_management.com.warehouse_management.model.Container;
import warehouse_management.com.warehouse_management.model.Warehouse;

@Component
public class GeneralResource {
    //    CONSTANT
    public static final int PXK_PNK_DATASET_ROW_IDX = 17;
    public static final int PXKDCNB_DATASET_ROW_IDX = 18;

    public static Warehouse getWarehouseById(MongoTemplate mongoTemplate, ObjectId warehouseId) {
        Warehouse rs = mongoTemplate.findById(warehouseId, Warehouse.class);
        if (rs == null)
            throw LogicErrException.of("Kho không tồn tại").setHttpStatus(HttpStatus.NOT_FOUND);
        return rs;
    }

    public static String generateTranTicketCode(WarehouseTranType tranType, WarehouseSubTranType subTranType) {
        if (tranType == null) {
            throw new IllegalArgumentException("tranType cannot be null");
        }

        String prefix = switch (tranType) {
            case DEST_TO_DEST_TRANSFER -> "PNKDCNB";
            default -> "";
        };

        // Nếu không có prefix thì trả thẳng timestamp
        if (prefix.isEmpty()) {
            return String.valueOf(System.currentTimeMillis());
        }

        return prefix + "-" + System.currentTimeMillis();
    }

    public static String generateTranTitle(WarehouseTranType tranType, WarehouseSubTranType subTranType, Warehouse wh1, Warehouse wh2) {
        // Kiểm tra null cho wh1 và tranType
        if (wh1 == null) {
            throw new IllegalArgumentException("Primary warehouse cannot be null");
        }
        if (tranType == null) {
            throw new IllegalArgumentException("Transaction type cannot be null");
        }

        // Lấy tên kho chính, mặc định là "Unknown Warehouse" nếu name rỗng
        String wh1Name = wh1.getName() != null ? wh1.getName() : "Unknown Warehouse";

        // Lấy tiêu đề giao dịch, mặc định là ID nếu title rỗng
        String tranTypeTitle = tranType.getTitle() != null ? tranType.getTitle() : tranType.getId();

        // Lấy tiêu đề subTranType nếu có, nếu không thì để rỗng
        String subTranTypeTitle = subTranType != null
                ? (subTranType.getTitle() != null ? subTranType.getTitle() : subTranType.getId())
                : "";

        // Xây dựng tiêu đề
        StringBuilder title = new StringBuilder();
        title.append(wh1Name);

        // Thêm kho phụ nếu có
        if (wh2 != null) {
            String wh2Name = wh2.getName() != null ? wh2.getName() : "Unknown Warehouse";
            title.append(" → ").append(wh2Name);
        }

        // Thêm loại giao dịch
        title.append(" - ").append(tranTypeTitle);

        // Thêm loại phụ nếu có
        if (!subTranTypeTitle.isEmpty()) {
            title.append(" - ").append(subTranTypeTitle);
        }

        return title.toString();
    }

    public static String generateTranTitle(WarehouseTranType tranType, WarehouseSubTranType subTranType, Warehouse wh1, Warehouse wh2, Container cont) {
        // Kiểm tra null cho wh1 và tranType
        if (wh1 == null) {
            throw new IllegalArgumentException("Primary warehouse cannot be null");
        }
        if (tranType == null) {
            throw new IllegalArgumentException("Transaction type cannot be null");
        }

        // Lấy tên kho chính, mặc định là "Unknown Warehouse" nếu name rỗng
        String wh1Name = wh1.getName() != null ? wh1.getName() : "Unknown Warehouse";

        // Lấy tiêu đề giao dịch, mặc định là ID nếu title rỗng
        String tranTypeTitle = tranType.getTitle() != null ? tranType.getTitle() : tranType.getId();

        // Lấy tiêu đề subTranType nếu có, nếu không thì để rỗng
        String subTranTypeTitle = subTranType != null
                ? (subTranType.getTitle() != null ? subTranType.getTitle() : subTranType.getId())
                : "";

        // Xây dựng tiêu đề
        StringBuilder title = new StringBuilder();
        title.append(wh1Name);

        // Thêm kho phụ nếu có
        if (wh2 != null) {
            String wh2Name = wh2.getName() != null ? wh2.getName() : "Unknown Warehouse";
            title.append(" → ").append(wh2Name);
        }


        appendWhenNotBlank(title, cont.getContainerCode());
        appendWhenNotBlank(title, tranTypeTitle);
        appendWhenNotBlank(title, subTranTypeTitle);

        return title.toString();
    }


    private static void appendWhenNotBlank(StringBuilder sb, String input) {
        if (!input.isBlank() && !input.isEmpty()) {
            sb.append(" - " + input);
        }
    }
}
