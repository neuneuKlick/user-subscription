package com.example.user_subscription.integration;

import com.example.user_subscription.dto.SubscriptionDto;
import com.example.user_subscription.exception.exceptions.ConflictException;
import com.example.user_subscription.mapper.SubscriptionMapper;
import com.example.user_subscription.model.Subscription;
import com.example.user_subscription.model.User;
import com.example.user_subscription.repository.SubscriptionRepository;
import com.example.user_subscription.repository.UserRepository;
import com.example.user_subscription.service.SubscriptionService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class SubscriptionServiceIntegrationTest {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriptionMapper subscriptionMapper;

    @BeforeEach
    void setUp() {
        subscriptionRepository.deleteAll();
    }

    @Test
    void addSubscription_ShouldCreateSubscription_WhenDataValid() {
        User user = userRepository.save(
                new User(null,"Test User", "test@example.com", new ArrayList<>())
        );

        SubscriptionDto requestDto = new SubscriptionDto(
                null,
                "Netflix Premium",
                null,
                LocalDate.now().plusMonths(1),
                null
        );

        SubscriptionDto result = subscriptionService.addSubscription(user.getId(), requestDto);

        assertNotNull(result.getId());
        assertEquals("Netflix Premium", result.getServiceName());
        assertEquals(user.getId(), result.getUserId());

        Optional<Subscription> savedSubscription = subscriptionRepository.findById(result.getId());
        assertTrue(savedSubscription.isPresent());
        assertEquals(user.getId(), savedSubscription.get().getUser().getId());
    }

    @Test
    void addSubscription_ShouldThrow_WhenDuplicateService() {
        LocalDate endDate = LocalDate.now().plusMonths(1);
        User user = userRepository.save(
                new User(null,"Test User", "test@example.com", new ArrayList<>())
        );

        subscriptionService.addSubscription(user.getId(),
                new SubscriptionDto(null, "Netflix", null, endDate, null));

        assertThrows(ConflictException.class, () -> {
            subscriptionService.addSubscription(user.getId(),
                    new SubscriptionDto(null, "Netflix", null, endDate, null));
        });
    }

    @AfterEach
    void tearDown() {
        subscriptionRepository.deleteAll();
        userRepository.deleteAll();
    }
}
