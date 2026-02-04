package ee.zhan.service;

import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ee.zhan.dto.RegistrationRequest;
import ee.zhan.entity.AppUser;
import ee.zhan.exception.EmailAlreadyExists;
import ee.zhan.repository.AppUserRepository;
import ee.zhan.util.AuthUtils;

@Service

public class AuthService {
    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AppUserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(RegistrationRequest request) {
        String normalizedEmail = AuthUtils.normalizeEmail(request.getEmail());

        if (repository.existsAppUsersByEmail(normalizedEmail)) {
            throw new EmailAlreadyExists();
        }

        var user = new AppUser();
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        try {
            repository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new EmailAlreadyExists();
        }
    }

}
