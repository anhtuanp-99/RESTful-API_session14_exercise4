package com.tuan.employee.security;

import com.tuan.employee.model.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * UserPrincipal – Wrapper của User entity, implement UserDetails.
 */
public class UserPrincipal implements UserDetails {

    @Getter
    private final User user; // Entity từ DB

    private final Collection<? extends GrantedAuthority> authorities; // Danh sách quyền

    /**
     * Constructor nhận User và danh sách quyền.
     */
    public UserPrincipal(User user, Collection<? extends GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities;
    }

    /**
     * Factory method: tạo UserPrincipal từ User entity.
     * Tự động tạo GrantedAuthority từ role của user (prefix "ROLE_").
     */
    public static UserPrincipal create(User user) {
        Collection<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole())
        );
        return new UserPrincipal(user, authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities; // Trả về danh sách quyền của user
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getEnabled(); // Trả về trạng thái enabled từ DB
    }

}
