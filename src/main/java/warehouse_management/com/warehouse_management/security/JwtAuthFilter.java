package warehouse_management.com.warehouse_management.security;

import io.jsonwebtoken.ExpiredJwtException;
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
                Claims claims = Jwts.parser()
                        .setSigningKey(secretKey)
                        .parseClaimsJws(token)
                        .getBody();

                String email = claims.getSubject(); // lấy subject
                String id = claims.get("id", String.class); // lấy claim permissions
                List<String> permissions = claims.get("permissions", List.class); // lấy claim permissions
                if (email == null || id == null || permissions == null || permissions.isEmpty()) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT claims");
                    return;
                }
                // Giả sử CustomUserDetail của bạn đã implements UserDetails
                CustomUserDetail userDetails = new CustomUserDetail(email, id, permissions);
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);

            } catch (ExpiredJwtException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token expired");
                return;
            } catch (SignatureException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT signature");
                return;
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                return;
            }

        }

        filterChain.doFilter(request, response);
    }
}