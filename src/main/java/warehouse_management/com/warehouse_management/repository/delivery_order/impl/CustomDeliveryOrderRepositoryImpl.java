package warehouse_management.com.warehouse_management.repository.delivery_order.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import warehouse_management.com.warehouse_management.dto.delivery_order.response.DeliveryOrderPageDto;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.report_inventory.request.ReportParamsDto;
import warehouse_management.com.warehouse_management.dto.report_inventory.response.ReportInventoryDto;
import warehouse_management.com.warehouse_management.enumerate.DeliveryOrderStatus;
import warehouse_management.com.warehouse_management.enumerate.InventoryType;
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
                Aggregation.lookup("client", "customerId", "_id", "client"),
                Aggregation.unwind("client", true),
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("deletedAt").isNull()
                )),
                Aggregation.project("deliveryOrderCode", "customerId", "createdAt", "updatedAt", "deliveryDate", "holdingDays", "status")
                        .and("client.name").as("customerName")
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
                                                        ArithmeticOperators.Multiply.valueOf(
                                                                        ConditionalOperators.ifNull("$$this.pricing.purchasePrice").then(0)
                                                                )
                                                                .multiplyBy("$$this.quantity")
                                                )

                        )).as("totalPurchasePrice")
                        .and(ArrayOperators.Reduce.arrayOf("$inventoryItems")
                                .withInitialValue(0)
                                .reduce(
                                        ArithmeticOperators.Add.valueOf("$$value")
                                                .add(
                                                        ArithmeticOperators.Multiply.valueOf(
                                                                        ConditionalOperators.ifNull("$$this.pricing.actualSalePrice").then(0)
                                                                )
                                                                .multiplyBy("$$this.quantity")
                                                )

                        )).as("totalActualSalePrice")

        );
        Aggregation agg = Aggregation.newAggregation(pipelines);
        return MongoRsqlUtils.queryAggregatePage(DeliveryOrder.class, DeliveryOrderPageDto.class, agg, optionsDto);
    }


    public Page<ReportInventoryDto> findPageReportItemUnDelivered(ReportParamsDto params){

        AggregationExpression nowDay = DateOperators.DateFromParts.dateFromParts()
                .year(DateOperators.Year.yearOf("$$NOW"))
                .month(DateOperators.Month.monthOf("$$NOW"))
                .day(DateOperators.DayOfMonth.dayOfMonth("$$NOW"));

        AggregationExpression deliveryDate = DateOperators.DateFromParts.dateFromParts()
                .year(DateOperators.Year.yearOf("deliveryDate"))
                .month(DateOperators.Month.monthOf("deliveryDate"))
                .day(DateOperators.DayOfMonth.dayOfMonth("deliveryDate"));

        ArithmeticOperators.Subtract dayLateOperatorsSubtract = ArithmeticOperators.Subtract.valueOf(nowDay).subtract(deliveryDate);

        Aggregation aggregation = Aggregation.newAggregation(

                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("deletedAt").isNull(),
                        Criteria.where("status").nin(DeliveryOrderStatus.COMPLETED.getValue(), DeliveryOrderStatus.REJECTED.getValue())
                )),

                Aggregation.unwind("inventoryItems"),

                Aggregation.match(Criteria.where("inventoryItems.isDelivered").is(false)),

                Aggregation.lookup("client", "customerId", "_id", "customer"),
                Aggregation.unwind("customer"),

                Aggregation.group("deliveryOrderCode", "customerId", "deliveryDate", "inventoryItems.model", "inventoryItems.poNumber")
                        .first("inventoryItems.logistics.orderDate").as("orderDate")
                        .first("customer.name").as("customerName")
                        .first(
                                ConditionalOperators.when(Criteria.where("deliveryDate").ne(null))
                                        .then(
                                                ConditionalOperators.when(ComparisonOperators.Gt.valueOf(dayLateOperatorsSubtract).greaterThanValue(0))
                                                        .then(ArithmeticOperators.Divide.valueOf(dayLateOperatorsSubtract).divideBy(TimeUnit.DAYS.toMillis(1)))
                                                        .otherwise(0)
                                        )
                                        .otherwise(0)
                        ).as("daysLate")
                        .sum(
                                ConditionalOperators.when(Criteria.where("inventoryItems.inventoryType").is(InventoryType.VEHICLE.getId()))
                                        .thenValueOf("inventoryItems.quantity")
                                        .otherwise(0)
                        ).as("totalVehicle")
                        .sum(
                                ConditionalOperators.when(Criteria.where("inventoryItems.inventoryType").is(InventoryType.ACCESSORY.getId()))
                                        .thenValueOf("inventoryItems.quantity")
                                        .otherwise(0)
                        ).as("totalAccessory")
                        .sum(
                                ConditionalOperators.when(Criteria.where("inventoryItems.inventoryType").is(InventoryType.SPARE_PART.getId()))
                                        .thenValueOf("inventoryItems.quantity")
                                        .otherwise(0)
                        ).as("totalSparePart"),

                Aggregation.project("deliveryOrderCode", "orderDate", "deliveryDate", "customerName", "model", "poNumber", "totalVehicle", "totalAccessory", "totalSparePart", "daysLate")
                        .andExpression("'" + params.getTypeReport() + "'").as("reportType")
        );

        return MongoRsqlUtils.queryAggregatePage(DeliveryOrder.class, ReportInventoryDto.class, aggregation, params);
    }
}
