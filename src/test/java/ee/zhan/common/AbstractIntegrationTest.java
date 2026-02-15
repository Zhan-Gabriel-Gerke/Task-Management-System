package ee.zhan.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.zhan.task.TaskService;
import ee.zhan.user.AppUserRepository;
import ee.zhan.task.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest {

    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:18-alpine");

    @Autowired protected TaskService taskService;
    @Autowired protected TaskRepository taskRepository;
    @Autowired protected AppUserRepository appUserRepository;
    @Autowired protected TransactionTemplate transactionTemplate;
    @Autowired protected MockMvc mockMvc;
    @Autowired protected ObjectMapper objectMapper;

    @BeforeEach
    void globalSetUp() {
        taskRepository.deleteAll();
        appUserRepository.deleteAll();
    }

    protected String generateUniqueEmail() {
        return "user-" + UUID.randomUUID() + "@test.com";
    }

    protected String generateUniquePassword() {
        return "Test!" + UUID.randomUUID().toString().substring(0, 10);
    }
}
