package warehouse_management.com.warehouse_management.repository.delivery_order.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import warehouse_management.com.warehouse_management.dto.delivery_order.response.DeliveryOrderPageDto;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.model.DeliveryOrder;
import warehouse_management.com.warehouse_management.repository.delivery_order.CustomDeliveryOrderRepository;
import warehouse_management.com.warehouse_management.utils.MongoRsqlUtils;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class CustomDeliveryOrderRepositoryImpl implements CustomDeliveryOrderRepository {

    @Override
    public Page<DeliveryOrderPageDto> findPageDeliveryOrder(PageOptionsDto optionsDto) {
        List<AggregationOperation> pipelines = List.of(
                Aggregation.lookup("user", "customerId", "_id", "user"),
                Aggregation.unwind("user", true),
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("deletedAt").isNull()
                )),
                Aggregation.project("deliveryOrderCode", "createdAt", "deliveryDate", "holdingDays", "status")
                        .and("user.fullName").as("customerName")
                        .and(
                                ArithmeticOperators.Add.valueOf("$createdAt")
                                        .add(
                                                ArithmeticOperators.Multiply.valueOf("$holdingDays")
                                                        .multiplyBy(TimeUnit.DAYS.toMillis(1))
                                        )
                        ).as("holdingDeadlineDate")
                        .and(ArrayOperators.Reduce.arrayOf("$inventoryItems")
                                .withInitialValue(0)
                                .reduce(
                                        ArithmeticOperators.Add.valueOf("$$value")
                                                .add(
                                                        ArithmeticOperators.Multiply.valueOf("$$this.pricing.purchasePrice")
                                                                .multiplyBy("$$this.quantity")
                                                )

                        )).as("totalPurchasePrice")
                        .and(ArrayOperators.Reduce.arrayOf("$inventoryItems")
                                .withInitialValue(0)
                                .reduce(
                                        ArithmeticOperators.Add.valueOf("$$value")
                                                .add(
                                                        ArithmeticOperators.Multiply.valueOf("$$this.pricing.actualSalePrice")
                                                                .multiplyBy("$$this.quantity")
                                                )

                        )).as("totalActualSalePrice")

        );
        Aggregation agg = Aggregation.newAggregation(pipelines);
        return MongoRsqlUtils.queryAggregatePage(DeliveryOrder.class, DeliveryOrderPageDto.class, agg, optionsDto);
    }
}
