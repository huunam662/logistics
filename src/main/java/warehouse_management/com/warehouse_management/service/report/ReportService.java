package warehouse_management.com.warehouse_management.service.report;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
@Component
public class ReportService {
    public byte[] getReport(String ticketId, String type) {
        GenerateReportStrategy strategy = GenerateReportStrategyFactory.getStrategy(type)
                .orElseThrow(() -> LogicErrException.of("Loại báo cáo không hợp lệ: " + type));

        Map<String, Object> contextMap = strategy.prepareContext(ticketId);

        String templateFileName = strategy.getTemplateFileName();


        try (InputStream fis = new ClassPathResource("report_templates/" + templateFileName).getInputStream();
             Workbook workbook = new XSSFWorkbook(fis);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            strategy.preprocessWorkbook(workbook, contextMap);

            workbook.write(bos);

            try (InputStream templateStream = new ByteArrayInputStream(bos.toByteArray());
                 ByteArrayOutputStream os = new ByteArrayOutputStream()) {

                org.jxls.common.Context context = new org.jxls.common.Context(contextMap);
                org.jxls.util.JxlsHelper.getInstance().processTemplate(templateStream, os, context);

                return os.toByteArray();
            }
        } catch (IOException e) {
            throw LogicErrException.of("Failed to generate report: " + e.getMessage());
        }
    }
}
