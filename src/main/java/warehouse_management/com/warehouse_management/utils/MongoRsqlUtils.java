package warehouse_management.com.warehouse_management.utils;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.CountOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import warehouse_management.com.warehouse_management.common.pagination.req.PageOptionsReq;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MongoRsqlUtils {


    public static <T> Page<T> queryPage(Class<T> entityType, Query query, Map<String, String> propertyMapper, PageOptionsReq optionsReq){
        MongoTemplate mongoTemplate = SpringContext.getBean(MongoTemplate.class);
        String filter = optionsReq.getFilter();
        if(filter != null && !filter.isBlank()){
            Criteria filterCriteria = new RSQLParser().parse(filter).accept(new MongoRsqlVisitor(propertyMapper));
            query.addCriteria(filterCriteria);
        }
        long totalT = mongoTemplate.count(query, entityType);
        Pageable pageable = optionsReq.getPageable();
        query.with(pageable);
        List<T> tResultList = mongoTemplate.find(query, entityType);
        return new PageImpl<>(tResultList, pageable, totalT);
    }

    public static <T> Page<T> queryPage(Class<T> entityClass, PageOptionsReq optionsReq){
        return queryPage(entityClass, new Query(), Map.of(), optionsReq);
    }

    public static <T> Page<T> queryPage(Class<T> entityClass, Query query, PageOptionsReq optionsReq){
        return queryPage(entityClass, query, Map.of(), optionsReq);
    }

    public static <T> Page<T> queryPage(Class<T> entityClass, Map<String, String> propertyMapper, PageOptionsReq optionsReq){
        return queryPage(entityClass, new Query(), propertyMapper, optionsReq);
    }

    public static <T> Page<T> queryAggregatePage(Class<?> inputType, Class<T> outputType, Aggregation agg, Map<String, String> propertyMapper, PageOptionsReq optionsReq){
        MongoTemplate mongoTemplate = SpringContext.getBean(MongoTemplate.class);
        String filter = optionsReq.getFilter();
        List<AggregationOperation> aggOp = new ArrayList<>(agg.getPipeline().getOperations());
        if(filter != null && !filter.isBlank()){
            Criteria filterCriteria = new RSQLParser().parse(filter).accept(new MongoRsqlVisitor(propertyMapper));
            aggOp.add(Aggregation.match(filterCriteria));
        }
        CountOperation countOp = Aggregation.count().as("count");
        aggOp.addLast(countOp);
        Aggregation countAgg = Aggregation.newAggregation(aggOp);
        AggregationResults<Document> countResults = mongoTemplate.aggregate(countAgg, inputType, Document.class);
        List<Document> resultsCount = countResults.getMappedResults();
        long totalT = resultsCount.isEmpty() ? 0L : Optional.ofNullable(resultsCount.getFirst().get("count", Number.class)).orElse(0L).longValue();
        aggOp.remove(countOp);
        Pageable pageable = optionsReq.getPageable();
        if(!pageable.getSort().isEmpty()){
            aggOp.add(Aggregation.sort(pageable.getSort()));
        }
        aggOp.add(Aggregation.skip(pageable.getOffset()));
        aggOp.add(Aggregation.limit(pageable.getPageSize()));
        Aggregation pageAgg = Aggregation.newAggregation(aggOp);
        AggregationResults<T> aggregationResults = mongoTemplate.aggregate(pageAgg, inputType, outputType);
        List<T> results = aggregationResults.getMappedResults();
        return new PageImpl<>(results, pageable, totalT);
    }

    public static <T> Page<T> queryAggregatePage(Class<?> inputType, Class<T> outputType, Aggregation agg, PageOptionsReq optionsReq){
        return queryAggregatePage(inputType, outputType, agg, Map.of(), optionsReq);
    }

    public static class MongoRsqlVisitor implements RSQLVisitor<Criteria, Void> {

        private final Map<String, String> propertyMapper;

        public MongoRsqlVisitor(Map<String, String> propertyMapper) {
            this.propertyMapper = propertyMapper;
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
            if(propertyMapper.containsKey(field))
                field = propertyMapper.get(field);
            return switch (op) {
                case "==", "!=" -> {
                    String val = args.getFirst();
                    Criteria result = Criteria.where(field);
                    if(op.equals("!=")) result.not();

                    if (val.startsWith("*") && val.endsWith("*"))
                        yield result.regex(val.substring(0, val.length() - 1).substring(1), "i");
                    else if (val.startsWith("*"))
                        yield result.regex(val.replaceFirst("\\*", "^"), "i");
                    else if (val.endsWith("*"))
                        yield result.regex(val.substring(0, val.length() - 1) + "$", "i");
                    else {
                        if(op.equals("==")) yield result.is(parseTypeValue(val));
                        else yield Criteria.where(field).ne(parseTypeValue(val));
                    }
                }
                case "=gt=", ">" -> Criteria.where(field).gt(parseTypeValue(args.getFirst()));
                case "=lt=", "<" -> Criteria.where(field).lt(parseTypeValue(args.getFirst()));
                case "=ge=", ">=" -> Criteria.where(field).gte(parseTypeValue(args.getFirst()));
                case "=le=", "<=" -> Criteria.where(field).lte(parseTypeValue(args.getFirst()));
                case "=in=", "=out=" -> {
                    List<Object> argsParsed = args.stream().map(this::parseTypeValue).toList();
                    if(op.equals("=in=")) yield Criteria.where(field).in(argsParsed);
                    else yield Criteria.where(field).nin(argsParsed);
                }
                default -> throw new IllegalArgumentException("Unsupported operator: " + op);
            };
        }

        private Object parseTypeValue(String value){
            if(value == null) return null;
            if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"))
                try{return Boolean.parseBoolean(value);} catch(Exception ignored){}
            if(value.contains(".")) try{return Double.parseDouble(value);} catch(Exception ignored){}
            else try{return Long.parseLong(value);} catch(Exception ignored){}
            try{return LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);} catch(Exception ignored){}
            return value;
        }
    }
}
