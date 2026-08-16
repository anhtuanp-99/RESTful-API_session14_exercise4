package com.tuan.employee.service;

import com.tuan.employee.dto.request.RegisterRequest;
import com.tuan.employee.model.User;
import com.tuan.employee.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserService – xử lý logic liên quan đến user.
 * Lý do tạo: Tách biệt logic đăng ký khỏi Controller.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Đăng ký tài khoản mới.
     * - Kiểm tra username đã tồn tại chưa.
     * - Mã hóa password bằng BCrypt.
     * - Gán role mặc định "USER", enabled = true.
     */
    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username đã tồn tại: " + request.getUsername());
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword())) // BCrypt Hash
                .role("USER")
                .enabled(true)
                .build();

        userRepository.save(user);
    }


}
