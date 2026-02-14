package ee.zhan;

import ee.zhan.repository.AppUserRepository;
import ee.zhan.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractIntegrationTest {

    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:18-alpine");

    @Autowired TaskRepository taskRepository;
    @Autowired AppUserRepository appUserRepository;

    @BeforeEach
    void globalSetUp() {
        taskRepository.deleteAll();
        appUserRepository.deleteAll();
    }

    protected String generateUniqueEmail() {
        return "user-" + UUID.randomUUID() + "@test.com";
    }
}
