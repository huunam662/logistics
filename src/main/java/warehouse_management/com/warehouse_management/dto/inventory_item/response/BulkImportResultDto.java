package warehouse_management.com.warehouse_management.dto.inventory_item.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkImportResultDto {
    private int totalProcessed;
    private int successCount;
    private int skippedCount;
    private int updatedCount;
    private List<String> skippedItems;
    private List<String> updatedItems;
    
    public BulkImportResultDto(int totalProcessed, int successCount, int skippedCount, int updatedCount) {
        this.totalProcessed = totalProcessed;
        this.successCount = successCount;
        this.skippedCount = skippedCount;
        this.updatedCount = updatedCount;
        this.skippedItems = List.of();
        this.updatedItems = List.of();
    }
}
