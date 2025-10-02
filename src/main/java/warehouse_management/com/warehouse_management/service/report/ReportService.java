package warehouse_management.com.warehouse_management.service.report;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jxls.util.JxlsHelper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import warehouse_management.com.warehouse_management.enumerate.TransactionModule;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ReportService {
    private final ReportTemplateCache templateCache;

    public byte[] getReport(String transactionModule, String ticketId, String docType) {
        try {
            GenerateReportStrategy strategy = GenerateReportStrategyFactory.getStrategy(docType)
                    .orElseThrow(() -> LogicErrException.of("Loại báo cáo không hợp lệ: " + docType));

            Map<String, Object> contextMap = strategy.prepareContext(TransactionModule.fromId(transactionModule), ticketId);

            String templateFileName = strategy.getTemplateFileName();
            byte[] templateBytes = templateCache.getTemplate(templateFileName);

            byte[] result = processTemplate(templateBytes, strategy, contextMap);

            return result;
        } catch (IOException e) {
            throw LogicErrException.of("Failed to generate report: " + e.getMessage());
        }
    }

    private byte[] processTemplate(
            byte[] templateBytes,
            GenerateReportStrategy strategy,
            Map<String, Object> contextMap) throws IOException {

        try (InputStream templateIs = new ByteArrayInputStream(templateBytes);
             Workbook workbook = new XSSFWorkbook(templateIs);
             ByteArrayOutputStream preprocessedBos = new ByteArrayOutputStream()) {

            strategy.preprocessWorkbook(workbook, contextMap);

            workbook.write(preprocessedBos);

            try (InputStream jxlsInputStream = new ByteArrayInputStream(preprocessedBos.toByteArray());
                 ByteArrayOutputStream finalBos = new ByteArrayOutputStream()) {

                org.jxls.common.Context context = new org.jxls.common.Context(contextMap);

                JxlsHelper jxlsHelper = JxlsHelper.getInstance();
                jxlsHelper.setUseFastFormulaProcessor(true);          // Fast formula evaluation
                jxlsHelper.setProcessFormulas(true);                  // Enable formula processing
                jxlsHelper.setDeleteTemplateSheet(true);              // Remove template sheet

                jxlsHelper.processTemplate(jxlsInputStream, finalBos, context);

                return finalBos.toByteArray();
            }
        }
    }
}
