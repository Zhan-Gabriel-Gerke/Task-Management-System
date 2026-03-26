package ee.zhan.common.security;

import ee.zhan.user.entity.AppUserEntity;
import ee.zhan.user.repository.AppUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ee.zhan.auth.utils.AuthUtils;

@Service
public class AppUserDetailsServiceImpl implements UserDetailsService {
    private final AppUserRepository repository;
    
    public AppUserDetailsServiceImpl(AppUserRepository repository) {
        this.repository = repository;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String normalizedEmail = AuthUtils.normalizeEmail(username);
        AppUserEntity user = repository
                .findByEmail(normalizedEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

        return new AppUserAdapter(user);
    }
}
