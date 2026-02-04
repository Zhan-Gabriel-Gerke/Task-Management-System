package ee.zhan.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ee.zhan.entity.AppUser;
import ee.zhan.repository.AppUserRepository;
import ee.zhan.security.AppUserAdapter;
import ee.zhan.util.AuthUtils;

@Service
public class AppUserDetailsServiceImpl implements UserDetailsService {
    private final AppUserRepository repository;
    
    public AppUserDetailsServiceImpl(AppUserRepository repository) {
        this.repository = repository;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String normalizedEmail = AuthUtils.normalizeEmail(username);
        AppUser user = repository
                .findAppUserByEmail(normalizedEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

        return new AppUserAdapter(user);
    }
}
