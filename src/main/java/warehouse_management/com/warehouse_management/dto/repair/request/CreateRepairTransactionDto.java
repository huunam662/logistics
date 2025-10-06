package warehouse_management.com.warehouse_management.dto.repair.request;

import lombok.Data;

import java.util.List;

@Data
public class CreateRepairTransactionDto {

    private String repairId;          // Đơn sửa chữa cha
    private List<RepairTransaction> repairTransactions;              // Lý do sửa chữa

    @Data
    public static class RepairTransaction {
        private String repairTransactionId;
        private String reason;
    }
}
