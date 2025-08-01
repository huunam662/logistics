package warehouse_management.com.warehouse_management.utils;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenExtractor {

    private static final String[] AUTH_HEADER_PREFIXES = {"Bearer ", "Token "};
    private static final String AUTH_HEADER = "Authorization";
    private static final String AUTH_PARAM = "token";

    /**
     * Extracts JWT token from request with multiple fallback options
     *
     * @param request HTTP request
     * @return JWT token or null if not found
     * @throws JwtExtractionException if malformed token found
     */
    public String parseJwt(jakarta.servlet.http.HttpServletRequest request) {
        // 1. Try Authorization header first (standard approach)
        String headerAuth = request.getHeader(AUTH_HEADER);
        if (StringUtils.isNotBlank(headerAuth)) {
            for (String prefix : AUTH_HEADER_PREFIXES) {
                if (headerAuth.startsWith(prefix)) {
                    String token = headerAuth.substring(prefix.length());
                    validateTokenFormat(token);
                    return token;
                }
            }
            throw new JwtExtractionException("Malformed authorization header");
        }

        // 2. Try token parameter (for websockets/SSE)
        String parameterToken = request.getParameter(AUTH_PARAM);
        if (StringUtils.isNotBlank(parameterToken)) {
            validateTokenFormat(parameterToken);
            return parameterToken;
        }

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    String token = cookie.getValue();
                    validateTokenFormat(token);
                    return token;
                }
            }
        }

        return null; // No token found
    }

    private void validateTokenFormat(String token) {
        if (StringUtils.isBlank(token)) {
            throw new JwtExtractionException("Empty token");
        }
        if (token.length() < 64) { // Minimum reasonable JWT size
            throw new JwtExtractionException("Suspiciously short token");
        }
        if (token.split("\\.").length != 3) {
            throw new JwtExtractionException("Malformed JWT structure");
        }
    }

    public static class JwtExtractionException extends RuntimeException {
        public JwtExtractionException(String message) {
            super(message);
        }
    }
}