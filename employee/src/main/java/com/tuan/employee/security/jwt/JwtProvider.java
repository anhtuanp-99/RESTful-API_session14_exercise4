package com.tuan.employee.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * JwtProvider – Utility class xử lý JWT.
 * Lý do tạo: Tách biệt logic tạo/validate JWT ra khỏi Controller.
 * Các chức năng:
 * - generateToken(): tạo JWT từ Authentication object
 * - getUsernameFromToken(): trích xuất username từ token
 * - validateToken(): kiểm tra chữ ký và hạn của token
 */
@Component
public class JwtProvider {

    // Đọc secret key từ application.properties
    @Value("${jwt.secret}")
    private String jwtSecret;

    // Đọc thời gian hết hạn từ application.properties (milliseconds)
    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    /**
     * Tạo SecretKey từ chuỗi secret.
     * Keys.hmacShaKeyFor() sẽ tự động kiểm tra độ dài và báo lỗi nếu secret < 32 bytes.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Tạo JWT từ Authentication object sau khi user xác thực thành công.
     * Cấu trúc JWT:
     * - Header: alg=HS256, typ=JWT
     * - Payload: sub=username, role=role, iat=issuedAt, exp=expiration
     * - Signature: HMACSHA256(header + "." + payload, secret)
     *
     * @param authentication đối tượng Authentication từ Spring Security
     * @return chuỗi JWT (xxxxx.yyyyy.zzzzz)
     */
    public String generateToken(Authentication authentication) {
        // 1. Lấy username từ Authentication
        String username = authentication.getName();

        // 2. Lấy role của user (có thể có nhiều role, gộp lại bằng dấu phẩy)
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // 3. Xác định thời gian phát hành và hết hạn
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        // 4. Tạo JWT với builder của JJWT 0.12.6
        return Jwts.builder()
                .subject(username)                // sub = username
                .claim("role", role)        // thêm claim "role"
                .issuedAt(now)                    // iat = thời điểm phát hành
                .expiration(expiryDate)           // exp = thời điểm hết hạn
                .signWith(getSigningKey())        // Ký với SecretKey (HMAC-SHA256)
                .compact();                       // Tạo chuỗi JWT hoàn chỉnh
    }

    /**
     * Trích xuất username từ JWT.
     * Sử dụng parser() với verifyWith() và parseSignedClaims().
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())               // Cung cấp key để verify
                .build()
                .parseSignedClaims(token)                 // parseSignedClaims (mới)
                .getPayload();                             // getPayload() thay vì getBody()
        return claims.getSubject();
    }

    /**
     * Kiểm tra token có hợp lệ không.
     * - Đúng chữ ký (signature khớp với secret)
     * - Chưa hết hạn (exp > current time)
     * - Đúng định dạng JWT
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            // ExpiredJwtException, MalformedJwtException, SignatureException...
            return false;
        }
    }
}

