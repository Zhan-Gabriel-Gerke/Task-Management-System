package ee.zhan.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ee.zhan.entity.AppUser;

import java.util.Collection;
import java.util.Collections;

public class AppUserAdapter implements UserDetails {
    private final AppUser user;

    public AppUserAdapter(AppUser user) {
        this.user = user;
    }

    //just to fulfill the interface right now I don't need RBAC
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
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
        return true;
    }
}
