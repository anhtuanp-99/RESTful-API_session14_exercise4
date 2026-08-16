package com.tuan.employee.controller;

import com.tuan.employee.dto.request.LoginRequest;
import com.tuan.employee.dto.request.RegisterRequest;
import com.tuan.employee.dto.response.JwtResponse;
import com.tuan.employee.security.UserPrincipal;
import com.tuan.employee.security.jwt.JwtProvider;
import com.tuan.employee.service.AuthService;
import com.tuan.employee.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final JwtProvider jwtProvider;

    // ===== ĐĂNG KÝ =====
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Đăng kí thành công");
    }

    // ===== ĐĂNG NHẬP =====
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request) {

        // Bước 1: Xác thực username/password
        Authentication authentication = authService.authenticate(request);

        // Bước 2: Lấy thông tin user từ Authentication
        UserPrincipal principal = (UserPrincipal)authentication.getPrincipal();
        String username = principal.getUsername();
        String role = principal.getUser().getRole();

        // Bước 3: Tạo JWT từ Authentication
        String token = jwtProvider.generateToken(authentication);

        // Bước 4: Trả về JSON response chuẩn
        JwtResponse response = new JwtResponse(token, username, role);
        return ResponseEntity.ok(response);
    }

}
