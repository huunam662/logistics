package warehouse_management.com.warehouse_management.repository.quotation_form;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import warehouse_management.com.warehouse_management.dto.quotation_form.response.QuotationFormDto;
import warehouse_management.com.warehouse_management.model.QuotationForm;

import java.util.Optional;

public interface QuotationFormRepository extends MongoRepository<QuotationForm, ObjectId>, CustomQuotationFormRepository {

    @Query(value = "{quotationCode: ?0, deletedAt: null}", exists = true)
    boolean existsByQuotationCode(String code);

    @Aggregation(pipeline = {
            "{$match: {_id: ?0, deletedAt: null}}",
            "{$project: {id: '$_id', quotationCode: 1, customerId: 1, customerName: 1, customerPhone: 1, customerAddress: 1, customerEmail: 1, customerLevel: 1, createdBy: 1, createdAt: 1}}"
    })
    Optional<QuotationFormDto> findQuotationFormDtoById(ObjectId id);

}
