package warehouse_management.com.warehouse_management.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {


    @Value("${app.jwt.secret}")
    private String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                byte[] keyBytes;
                try {
                    keyBytes = Base64.getDecoder().decode(secretKey);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Khóa bí mật không phải là chuỗi Base64 hợp lệ: " + e.getMessage());
                }
                // Kiểm tra độ dài khóa
//                if (keyBytes.length < 32) {
//                    throw new IllegalArgumentException("Khóa bí mật quá ngắn: " + keyBytes.length + " byte, cần ít nhất 32 byte");
//                }
                Key signingKey = Keys.hmacShaKeyFor(keyBytes);
                Claims claims = Jwts.parser()
                        .setSigningKey(signingKey)
                        .parseClaimsJws(token)
                        .getBody();

                String email = claims.getSubject(); // lấy subject
                String id = claims.get("id", String.class); // lấy claim permissions
                List<String> permissions = claims.get("permissions", List.class); // lấy claim permissions
//                if (email == null || id == null || permissions == null || permissions.isEmpty()) {
//                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid JWT claims");
//                    return;
//                }
                // Giả sử CustomUserDetail của bạn đã implements UserDetails
                CustomUserDetail userDetails = new CustomUserDetail(email, id, permissions);
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);

            } catch (ExpiredJwtException e) {
                // Xử lý lỗi token hết hạn với phản hồi JSON tùy chỉnh
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Unauthorized\", \"message1\": \"" + e.getMessage() + "\"}");
                return;
            } catch (SignatureException e) {
                // Xử lý lỗi chữ ký không hợp lệ với phản hồi JSON tùy chỉnh
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Unauthorized\", \"message2\": \"" + e.getMessage() + "\"}");
                return;
            } catch (Exception e) {
                // Xử lý các lỗi JWT khác với phản hồi JSON tùy chỉnh
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Unauthorized\", \"message3\": \"" + e.getMessage() + "\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}