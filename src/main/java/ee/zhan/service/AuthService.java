package ee.zhan.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ee.zhan.dto.Auth.RegistrationRequest;
import ee.zhan.entity.AppUserEntity;
import ee.zhan.exception.Auth.EmailAlreadyExists;
import ee.zhan.repository.AppUserRepository;
import ee.zhan.util.AuthUtils;

@RequiredArgsConstructor
@Service

public class AuthService {
    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void register(RegistrationRequest request) {
        String normalizedEmail = AuthUtils.normalizeEmail(request.getEmail());

        if (repository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyExists();
        }

        var user = new AppUserEntity();
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        try {
            repository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new EmailAlreadyExists();
        }
    }

}
