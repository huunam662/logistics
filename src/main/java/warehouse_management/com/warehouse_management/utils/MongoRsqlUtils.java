package warehouse_management.com.warehouse_management.utils;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.*;
import lombok.NonNull;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.CountOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MongoRsqlUtils {

    public static <T> Page<T> queryPage(
            @NonNull Class<?> inputType,
            @NonNull Class<T> outputType,
            @NonNull Query query,
            @NonNull Map<String, String> rsqlPropertyMapper,
            @NonNull PageOptionsDto optionsReq
    ) {
        MongoTemplate mongoTemplate = SpringContext.getBean(MongoTemplate.class);
        String filter = optionsReq.getFilter();
        if (filter != null && !filter.isBlank()) {
            Criteria filterCriteria = new RSQLParser().parse(filter).accept(new MongoRsqlVisitor(rsqlPropertyMapper));
            query.addCriteria(filterCriteria);
        }
        long totalT = mongoTemplate.count(query, inputType);
        Pageable pageable = optionsReq.getPageable();
        query.with(pageable);
        List<T> tResultList = mongoTemplate.find(query, outputType, mongoTemplate.getCollectionName(inputType));
        return new PageImpl<>(tResultList, pageable, totalT);
    }

    public static <T> Page<T> queryPage(
            @NonNull Class<?> inputType,
            @NonNull Class<T> outputType,
            @NonNull PageOptionsDto optionsReq
    ) {
        return queryPage(inputType, outputType, new Query(), Map.of(), optionsReq);
    }

    public static <T> Page<T> queryPage(
            @NonNull Class<?> inputType,
            @NonNull Class<T> outputType,
            @NonNull Query query,
            @NonNull PageOptionsDto optionsReq
    ) {
        return queryPage(inputType, outputType, query, Map.of(), optionsReq);
    }

    public static <T> Page<T> queryPage(
            @NonNull Class<?> inputType,
            @NonNull Class<T> outputType,
            @NonNull Map<String, String> propertyMapper,
            @NonNull PageOptionsDto optionsReq
    ) {
        return queryPage(inputType, outputType, new Query(), propertyMapper, optionsReq);
    }

    public static <T> Page<T> queryAggregatePage(
            @NonNull Class<?> inputType,
            @NonNull Class<T> outputType,
            @NonNull Aggregation agg,
            @NonNull Map<String, String> rsqlPropertyMapper,
            @NonNull PageOptionsDto optionsReq
    ) {
        MongoTemplate mongoTemplate = SpringContext.getBean(MongoTemplate.class);
        String filter = optionsReq.getFilter();
        List<AggregationOperation> aggOp = new ArrayList<>(agg.getPipeline().getOperations());
        if (filter != null && !filter.isBlank()) {
            Criteria filterCriteria = new RSQLParser().parse(filter).accept(new MongoRsqlVisitor(rsqlPropertyMapper));
            aggOp.add(Aggregation.match(filterCriteria));
        }
        CountOperation countOp = Aggregation.count().as("count");
        aggOp.addLast(countOp);
        Aggregation countAgg = Aggregation.newAggregation(aggOp);
        AggregationResults<Document> countResults = mongoTemplate.aggregate(countAgg, inputType, Document.class);
        List<Document> resultsCount = countResults.getMappedResults();
        long totalT = resultsCount.isEmpty() ? 0L : Optional.ofNullable(resultsCount.getFirst().get("count", Number.class)).orElse(0L).longValue();
        aggOp.remove(countOp);
        Pageable pageable = rsqlPropertyMapper.isEmpty() ? optionsReq.getPageable() : buildSortProperty(optionsReq.getPageable(), rsqlPropertyMapper);
        if (!pageable.getSort().isEmpty()) {
            aggOp.add(Aggregation.sort(pageable.getSort()));
        }
        aggOp.add(Aggregation.skip(pageable.getOffset()));
        aggOp.add(Aggregation.limit(pageable.getPageSize()));
        Aggregation pageAgg = Aggregation.newAggregation(aggOp);
        AggregationResults<T> aggregationResults = mongoTemplate.aggregate(pageAgg, inputType, outputType);
        List<T> results = aggregationResults.getMappedResults();
        return new PageImpl<>(results, pageable, totalT);
    }

    public static <T> Page<T> queryAggregatePage(
            @NonNull Class<?> inputType,
            @NonNull Class<T> outputType,
            @NonNull Aggregation agg,
            @NonNull PageOptionsDto optionsReq
    ) {
        return queryAggregatePage(inputType, outputType, agg, Map.of(), optionsReq);
    }

    public static Pageable buildSortProperty(
            @NonNull Pageable pageable,
            @NonNull Map<String, String> propertyMapper
    ) {
        if (pageable.getSort().isEmpty()) return pageable;
        Sort newSort = Sort.by(pageable.getSort().stream()
                .map(order -> {
                    String newProperty = order.getProperty();
                    if (propertyMapper.containsKey(newProperty))
                        newProperty = propertyMapper.get(newProperty);
                    return new Sort.Order(
                            order.getDirection(),
                            newProperty,
                            order.getNullHandling()
                    );
                })
                .toList());

        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                newSort
        );

    }

    public static class MongoRsqlVisitor implements RSQLVisitor<Criteria, Void> {

        private final Map<String, String> rsqlPropertyMapper;

        public MongoRsqlVisitor(Map<String, String> rsqlPropertyMapper) {
            this.rsqlPropertyMapper = rsqlPropertyMapper;
        }

        @Override
        public Criteria visit(AndNode node, Void param) {
            return new Criteria().andOperator(
                    node.getChildren().stream()
                            .map(n -> n.accept(this, param))
                            .toArray(Criteria[]::new)
            );
        }

        @Override
        public Criteria visit(OrNode node, Void param) {
            return new Criteria().orOperator(
                    node.getChildren().stream()
                            .map(n -> n.accept(this, param))
                            .toArray(Criteria[]::new)
            );
        }

        @Override
        public Criteria visit(ComparisonNode node, Void param) {
            String field = node.getSelector();
            List<String> args = node.getArguments();
            String op = node.getOperator().getSymbol();
            if (rsqlPropertyMapper.containsKey(field))
                field = rsqlPropertyMapper.get(field);
            return switch (op) {
                case "==", "!=" -> {
                    String val = args.getFirst();
                    Criteria result = Criteria.where(field);

                    if (val.startsWith("*") || val.endsWith("*")){

                        if (op.equals("!=")) result.not();

                        if (val.startsWith("*") && val.endsWith("*"))
                            yield result.regex(val.substring(0, val.length() - 1).substring(1), "i");
                        else if (val.startsWith("*"))
                            yield result.regex(val.replaceFirst("\\*", "") + "$", "i");
                        else yield result.regex("^" + val.substring(0, val.length() - 1), "i");
                    }
                    else {
                        if (op.equals("==")) {
                            if(val.equalsIgnoreCase("is_null"))
                                yield result.is(null);
                            else if (val.equalsIgnoreCase("not_null"))
                                yield result.ne(null);
                            else yield result.is(parseTypeValue(val));
                        }
                        else yield result.ne(parseTypeValue(val));
                    }
                }
                case "=gt=", ">" -> Criteria.where(field).gt(parseTypeValue(args.getFirst()));
                case "=lt=", "<" -> Criteria.where(field).lt(parseTypeValue(args.getFirst()));
                case "=ge=", ">=" -> Criteria.where(field).gte(parseTypeValue(args.getFirst()));
                case "=le=", "<=" -> Criteria.where(field).lte(parseTypeValue(args.getFirst()));
                case "=in=", "=out=" -> {
                    List<Object> argsParsed = args.stream().map(this::parseTypeValue).toList();
                    if (op.equals("=in=")) yield Criteria.where(field).in(argsParsed);
                    else yield Criteria.where(field).nin(argsParsed);
                }
                default -> throw new IllegalArgumentException("Unsupported operator: " + op);
            };
        }

        private Object parseTypeValue(String value) {
            if (value == null) return null;
            // Ké 1 line, k ảnh hưởng đâu :)))
            if (ObjectId.isValid(value)) return new ObjectId(value);
            // ___ Ké 1 line, k ảnh hưởng đâu :)))
            if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"))
                try {
                    return Boolean.parseBoolean(value);
                } catch (Exception ignored) {
                }
            if (value.contains(".")) try {
                return Double.parseDouble(value);
            } catch (Exception ignored) {
            }
            else try {
                return Long.parseLong(value);
            } catch (Exception ignored) {
            }
            try {
                return LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (Exception ignored) {
            }
            return value;
        }
    }


    public static <T> List<T> queryAggregateList(
            @NonNull Class<?> inputType,
            @NonNull Class<T> outputType,
            @NonNull Aggregation agg,
            @NonNull Map<String, String> rsqlPropertyMapper,
            @NonNull PageOptionsDto optionsReq
    ) {
        MongoTemplate mongoTemplate = SpringContext.getBean(MongoTemplate.class);

        List<AggregationOperation> aggOp = new ArrayList<>(agg.getPipeline().getOperations());

        // Apply filter từ frontend (nếu có)
        String filter = optionsReq.getFilter();
        if (filter != null && !filter.isBlank()) {
            Criteria filterCriteria = new RSQLParser().parse(filter).accept(new MongoRsqlVisitor(rsqlPropertyMapper));
            aggOp.add(Aggregation.match(filterCriteria));
        }

        // Apply sort (nếu có)
        Pageable pageable = rsqlPropertyMapper.isEmpty()
                ? optionsReq.getPageable()
                : buildSortProperty(optionsReq.getPageable(), rsqlPropertyMapper);

        if (!pageable.getSort().isEmpty()) {
            aggOp.add(Aggregation.sort(pageable.getSort()));
        }

        // KHÔNG skip/limit => trả hết list
        Aggregation listAgg = Aggregation.newAggregation(aggOp);

        return mongoTemplate.aggregate(listAgg, inputType, outputType).getMappedResults();
    }

    public static Criteria parse(String filter){
        return new RSQLParser().parse(filter).accept(new MongoRsqlVisitor(Map.of()));
    }

}
