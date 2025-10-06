package warehouse_management.com.warehouse_management.repository.quotation_form;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.quotation_form.response.QuotationFormPageDto;

import java.util.List;

public interface CustomQuotationFormRepository {

    void softDelete(ObjectId id, String deletedBy);

    void bulkSoftDelete(List<ObjectId> ids, String deletedBy);

    Page<QuotationFormPageDto> findPageQuotationForm(PageOptionsDto optionsDto);
}
