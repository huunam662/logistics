package warehouse_management.com.warehouse_management.repository.quotation_form.impl;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.quotation_form.response.QuotationFormPageDto;
import warehouse_management.com.warehouse_management.model.QuotationForm;
import warehouse_management.com.warehouse_management.repository.quotation_form.CustomQuotationFormRepository;
import warehouse_management.com.warehouse_management.utils.MongoRsqlUtils;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomQuotationFormRepositoryImpl implements CustomQuotationFormRepository {

    private final MongoTemplate mongoTemplate;

    @Transactional
    @Override
    public void softDelete(ObjectId id, String deletedBy) {
        Query query = new Query(Criteria.where("_id").is(id));

        Update update = new Update()
                .set("deletedAt", LocalDateTime.now())
                .set("deletedBy", deletedBy);

        mongoTemplate.updateFirst(query, update, QuotationForm.class);
    }

    @Transactional
    @Override
    public void bulkSoftDelete(List<ObjectId> ids, String deletedBy) {

        Query query = new Query(Criteria.where("_id").in(ids));

        Update update = new Update()
                .set("deletedAt", LocalDateTime.now())
                .set("deletedBy", deletedBy);

        mongoTemplate.updateMulti(query, update, QuotationForm.class);
    }

    @Override
    public Page<QuotationFormPageDto> findPageQuotationForm(PageOptionsDto optionsDto) {

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("deletedAt").isNull()),

                Aggregation.project("quotationCode", "customerId", "customerName", "customerPhone", "customerAddress", "customerEmail", "customerLevel", "createdBy", "createdAt")
                        .and("_id").as("id")
                        .and(
                                ArrayOperators.Reduce.arrayOf("$quotationInventoryItems")
                                        .withInitialValue(0)
                                        .reduce(
                                                ArithmeticOperators.Add.valueOf("$$value")
                                                        .add(
                                                                ArithmeticOperators.Multiply.valueOf(
                                                                                ConditionalOperators.ifNull("$$this.salePrice").then(0)
                                                                        )
                                                                        .multiplyBy("$$this.quantity")
                                                        )

                                        )
                        ).as("totalSalePrices")
        );

        return MongoRsqlUtils.queryAggregatePage(QuotationForm.class, QuotationFormPageDto.class, aggregation, optionsDto);
    }

}
