package com.tuan.employee.service;

import com.tuan.employee.dto.request.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * AuthService – xử lý logic xác thực.
 * Ủy quyền cho AuthenticationManager của Spring Security thay vì tự viết logic.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;

    /**
     * Xác thực username/password.
     * - Nếu đúng: trả về Authentication object (chứa user + roles).
     * - Nếu sai: ném BadCredentialsException.
     *
     * Lợi ích của việc dùng AuthenticationManager:
     * - Tận dụng cơ chế xác thực của Spring Security.
     * - Tự động kiểm tra password đã mã hóa BCrypt.
     * - Hỗ trợ các tính năng nâng cao (khóa tài khoản, audit log...).
     */
    public Authentication authenticate(LoginRequest request) {
        try {
            // Tạo token chứa username và password (chưa được xác thực)
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
            // Ủy quyền cho AuthenticationManager xác thực
            return authenticationManager.authenticate(token);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Tên đăng nhập hoặc mật khẩu không đúng");
        }
    }

}
