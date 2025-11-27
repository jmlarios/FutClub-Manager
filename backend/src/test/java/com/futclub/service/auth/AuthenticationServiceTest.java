package com.futclub.service.auth;

import com.futclub.database.BaseDAOTest;
import com.futclub.database.dao.UserDAO;
import com.futclub.database.dao.UserDAOImpl;
import com.futclub.model.AnalystUser;
import com.futclub.model.User;
import com.futclub.model.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

class AuthenticationServiceTest extends BaseDAOTest {

    private UserDAO userDAO;
    private AuthenticationService authenticationService;

    @BeforeEach
    void initService() {
        userDAO = new UserDAOImpl();
        authenticationService = new AuthenticationService(userDAO);
    }

    @Test
    void successfulLoginReturnsUser() {
        Optional<User> result = authenticationService.login("coach.smith", "password123");
        assertThat(result).isPresent();
        assertThat(result.get().getRole()).isEqualTo(UserRole.COACH);
    }

    @Test
    void invalidPasswordReturnsEmpty() {
        Optional<User> result = authenticationService.login("coach.smith", "wrong-password");
        assertThat(result).isEmpty();
    }

    @Test
    void registerHashesPassword() {
        AnalystUser newUser = new AnalystUser();
        newUser.setUsername("analytics.tester");
        newUser.setActive(true);

        authenticationService.register(newUser, "freshPassword!1");

        User persisted = userDAO.getByUsername("analytics.tester");
        assertThat(persisted).isNotNull();
        assertThat(persisted.getPasswordHash()).isNotBlank();
        assertThat(persisted.getPasswordHash()).isNotEqualTo("freshPassword!1");
        assertThat(authenticationService.login("analytics.tester", "freshPassword!1")).isPresent();
    }

    @Test
    void accessHelperMatchesRole() {
        Optional<User> result = authenticationService.login("admin.wilson", "password123");
        assertThat(result).isPresent();
        assertThat(authenticationService.hasAccess(result.get(), UserRole.ADMINISTRATOR)).isTrue();
        assertThat(authenticationService.hasAccess(result.get(), UserRole.COACH)).isFalse();
    }
}
