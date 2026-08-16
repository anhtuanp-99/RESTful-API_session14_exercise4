package com.tuan.employee.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO trả về cho client sau khi đăng nhập thành công.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {

    private String accessToken; // Chuỗi JWT
    private String tokenType = "Bearer"; // Theo chuẩn Bearer Token
    private String username;
    private String role;

    /**
     * Constructor tiện lợi, tự động set tokenType = "Bearer".
     */
    public JwtResponse(String accessToken, String username, String role) {
        this.accessToken = accessToken;
        this.username = username;
        this.role = role;
    }
}
