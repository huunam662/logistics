package warehouse_management.com.warehouse_management.aspect;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import warehouse_management.com.warehouse_management.annotation.AuditAction;
import warehouse_management.com.warehouse_management.model.AuditLog;
import warehouse_management.com.warehouse_management.repository.AuditLogRepository;

import java.time.LocalDateTime;

@Aspect
@Component

public class AuditActionAspect {

    private final AuditLogRepository auditLogRepository;

    public AuditActionAspect(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Around("@annotation(auditAction)")
    public Object logAction(ProceedingJoinPoint joinPoint, AuditAction auditAction) throws Throwable {
        String action = auditAction.action();
        ObjectId userId = new ObjectId("64e4b8f8f1c2d3a4b5c6d7e8");
        String status = "SUCCESS";
        String errorMessage = null;

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            status = "FAIL";
            errorMessage = e.getMessage();
            throw e;
        } finally {
            String detail = AuditContext.getDetail();
            AuditContext.clear(); // tránh leak memory
            AuditLog log = new AuditLog(userId, action, LocalDateTime.now(), status, errorMessage, detail);
            auditLogRepository.save(log);
        }

        return result;
    }

    private String getCurrentUser() {
        return "Người dùng A";
    }
}