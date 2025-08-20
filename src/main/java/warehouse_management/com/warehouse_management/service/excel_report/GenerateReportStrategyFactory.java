package warehouse_management.com.warehouse_management.service.excel_report;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class GenerateReportStrategyFactory {

    private List<GenerateReportStrategy> strategies;

    private static Map<String, GenerateReportStrategy> strategyMap;

    public GenerateReportStrategyFactory(List<GenerateReportStrategy> strategies) {
        this.strategies = strategies;
    }

    /**
     * Sau khi Factory được khởi tạo, tạo một Map để tra cứu nhanh.
     */
    @PostConstruct
    void init() {
        strategyMap = strategies.stream()
                .collect(Collectors.toMap(GenerateReportStrategy::getReportType, Function.identity()));
    }

    /**
     * Lấy ra strategy phù hợp dựa trên loại báo cáo.
     * @param type Loại báo cáo (ví dụ: "PNK").
     * @return Optional chứa strategy nếu tìm thấy.
     */
    public static Optional<GenerateReportStrategy> getStrategy(String type) {
        return Optional.ofNullable(strategyMap.get(type));
    }
}