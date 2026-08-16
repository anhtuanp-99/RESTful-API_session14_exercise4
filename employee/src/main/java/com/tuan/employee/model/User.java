package com.tuan.employee.model;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 20)
    private String role; // "ADMIN", "USER" – dùng để phân quyền

    @Column(nullable = false)
    private Boolean enabled = true; // Mặc định true, có thể khóa tài khoản sau
}