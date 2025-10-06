package warehouse_management.com.warehouse_management.mapper;

import org.bson.types.ObjectId;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import warehouse_management.com.warehouse_management.dto.quotation_form.request.CreateQuotationFormDto;
import warehouse_management.com.warehouse_management.dto.quotation_form.request.FillProductForQuotationDto;
import warehouse_management.com.warehouse_management.dto.quotation_form.request.FillSparePartForQuotationDto;
import warehouse_management.com.warehouse_management.dto.quotation_form.request.UpdateQuotationFormDto;
import warehouse_management.com.warehouse_management.dto.quotation_form.response.QuotationFormDto;
import warehouse_management.com.warehouse_management.dto.quotation_form.response.QuotationFormProductDto;
import warehouse_management.com.warehouse_management.dto.quotation_form.response.QuotationFormSparePartDto;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.model.QuotationForm;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface QuotationFormMapper {

    QuotationForm toQuotationForm(CreateQuotationFormDto dto);

    default ObjectId map(String id) {
        if (id == null) return null;
        return new ObjectId(id);
    }

    QuotationForm.InventoryItemQuotation toInventoryItemQuotation(InventoryItem inventoryItem);

    QuotationForm.InventoryItemQuotation toInventoryItemQuotation(FillProductForQuotationDto.QuotationProductManual dto);

    QuotationForm.InventoryItemQuotation toInventoryItemQuotation(FillSparePartForQuotationDto.QuotationSparePartManual dto);

    QuotationFormProductDto toQuotationProductDto(QuotationForm.InventoryItemQuotation item);

    QuotationFormSparePartDto toQuotationSparePartDto(QuotationForm.InventoryItemQuotation item);

    QuotationForm toQuotationForm(UpdateQuotationFormDto dto);
}
