package com.tuan.employee.security;

import com.tuan.employee.model.User;
import com.tuan.employee.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * CustomUserDetailsService – cầu nối giữa DB và Spring Security.
 * Lý do tạo: Spring Security cần một service để tải thông tin user từ DB.
 * Khi user đăng nhập, Spring Security sẽ gọi loadUserByUsername().
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;


    /**
     * Tải UserDetails từ username.
     * Spring Security gọi phương thức này khi cần xác thực user.
     * Nếu không tìm thấy, ném UsernameNotFoundException.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user: " + username));

        // Chuyển đổi User entity → UserPrincipal (UserDetails)
        return UserPrincipal.create(user);
    }

}
