package com.qooco.boost.core.model.authentication;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;

@Builder
@AllArgsConstructor
public class AuthenticatedUser implements UserDetails {
    private final Long id; //User ID
    private final String username;
    @Getter
    private String token;
    @Getter
    private String appId;
    @Getter
    private String timezone;
    @Getter
    private String publicKey;
    @Getter
    private Long companyId;
    @Getter
    private String platform;
    @Getter
    private String appVersionName;
    @Getter
    private Integer appVersion;
    @Getter
    private boolean isSystemAdmin;
    @Getter
    private String locale;

    private final Collection<? extends GrantedAuthority> authorities;

    public AuthenticatedUser() {
        this.id = 0L;
        this.username = "";
        this.authorities = null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

    @JsonIgnore
    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthenticatedUser that = (AuthenticatedUser) o;
        return Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token);
    }

}
