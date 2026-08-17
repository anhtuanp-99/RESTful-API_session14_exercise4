package com.tuan.employee.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuan.employee.security.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * JwtAuthenticationFilter – bộ lọc xác thực JWT cho mỗi request.
 * Lý do cập nhật: Bắt các lỗi JWT cụ thể và trả về JSON thay vì để exception bắn ra ngoài.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            // 1. Lấy token từ Header
            String token = getTokenFromRequest(request);

            // 2. Nếu có token, validate và set authentication
            if (StringUtils.hasText(token)) {
                // Validate token – sẽ ném exception nếu lỗi
                jwtProvider.validateToken(token);

                String username = jwtProvider.getUsernameFromToken(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            // Tiếp tục filter chain nếu token hợp lệ hoặc không có token (sẽ bị entry point xử lý)
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException ex) {
            // Token hết hạn
            handleJwtException(response, "Token đã hết hạn. Vui lòng đăng nhập lại.", HttpServletResponse.SC_UNAUTHORIZED);
        } catch (MalformedJwtException ex) {
            // Token sai định dạng
            handleJwtException(response, "Token sai định dạng.", HttpServletResponse.SC_UNAUTHORIZED);
        } catch (SignatureException ex) {
            // Chữ ký không khớp
            handleJwtException(response, "Chữ ký token không hợp lệ.", HttpServletResponse.SC_UNAUTHORIZED);
        } catch (Exception ex) {
            // Các lỗi khác
            handleJwtException(response, "Lỗi xác thực: " + ex.getMessage(), HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    /**
     * Ghi JSON lỗi ra response.
     */
    private void handleJwtException(HttpServletResponse response, String message, int status) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status);
        body.put("error", "Unauthorized");
        body.put("message", message);
        body.put("timestamp", System.currentTimeMillis());

        objectMapper.writeValue(response.getOutputStream(), body);
    }

    /**
     * Trích xuất token từ Header Authorization.
     * Header có dạng: "Bearer <token>"
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}