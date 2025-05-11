package com.example.user_subscription.service.impl;

import com.example.user_subscription.dto.SubscriptionDto;
import com.example.user_subscription.exception.exceptions.user.UserNotFoundException;
import com.example.user_subscription.mapper.SubscriptionMapper;
import com.example.user_subscription.model.Subscription;
import com.example.user_subscription.model.User;
import com.example.user_subscription.repository.SubscriptionRepository;
import com.example.user_subscription.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private SubscriptionMapper subscriptionMapper;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    @Test
    void addSubscription_ShouldCreateSubscription_WhenDataValid() {
        Long userId = 1L;
        User mockUser = new User(userId, "Test User", "test@example.com", null);
        SubscriptionDto requestDto = new SubscriptionDto(
                null, "Test service name", null, null, null);

        Subscription subscriptionToSave = new Subscription(
                null, "Test service name", LocalDate.now(), null, mockUser);

        Subscription savedSubscription = new Subscription(
                1L, "Test service name", LocalDate.now(), null, mockUser);

        SubscriptionDto expectedDto = new SubscriptionDto(
                1L, "Test service name", LocalDate.now(), null, userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(subscriptionRepository.existsByUserAndServiceName(mockUser, "Test service name"))
                .thenReturn(false);
        when(subscriptionMapper.toSubscription(requestDto)).thenReturn(subscriptionToSave);
        when(subscriptionMapper.toDto(any(Subscription.class))).thenReturn(expectedDto);
        when(subscriptionRepository.save(subscriptionToSave)).thenReturn(savedSubscription);

        SubscriptionDto result = subscriptionService.addSubscription(userId, requestDto);

        assertNotNull(result);
        assertEquals("Test service name", result.getServiceName());
        verify(subscriptionRepository).save(subscriptionToSave);
    }

    @Test
    void getUserSubscriptions_ShouldReturnList_WhenUserExists() {
        Long userId = 1L;
        List<Subscription> mockSubscriptions = Arrays.asList(
                new Subscription(1L, "Netflix", LocalDate.now(), null,
                        new User(userId, null, null, null)),
                new Subscription(2L, "Spotify", LocalDate.now(), null,
                        new User(userId, null, null, null))
        );

        when(userRepository.existsById(userId)).thenReturn(true);
        when(subscriptionRepository.findWithUserByUserId(userId)).thenReturn(mockSubscriptions);
        when(subscriptionMapper.toDto(any(Subscription.class)))
                .thenAnswer(inv -> {
                    Subscription s = inv.getArgument(0);
                    return new SubscriptionDto(s.getId(), s.getServiceName(), null, null, userId);
                });

        List<SubscriptionDto> result = subscriptionService.getUserSubscriptions(userId);

        assertEquals(2, result.size());
        verify(userRepository).existsById(userId);
        verify(subscriptionRepository).findWithUserByUserId(userId);
    }

    @Test
    void deleteSubscription_ShouldDelete_WhenSubscriptionBelongsToUser() {
        Long userId = 1L;
        Long subscriptionId = 10L;

        User user = new User(userId, "Test User", "test@example.com", null);
        user.setId(userId);
        Subscription subscription = new Subscription(
                subscriptionId, "Netflix", null, null, user);
        subscription.setId(subscriptionId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));
        when(subscriptionRepository.deleteByIdAndUserId(subscriptionId, userId))
                .thenReturn(1);
        subscriptionService.deleteSubscription(userId, subscriptionId);

        verify(subscriptionRepository).deleteByIdAndUserId(subscriptionId, userId);

        assertThat(subscription.getUser().getId()).isEqualTo(userId);
    }

    @Test
    void deleteSubscription_ShouldThrow_WhenSubscriptionNotFound() {
        Long userId = 1L;
        Long subscriptionId = 10L;

        User user = new User(userId, "Test User", "test@example.com", null);
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                subscriptionService.deleteSubscription(userId, subscriptionId));

        verify(subscriptionRepository).findById(subscriptionId);

        verify(subscriptionRepository, never()).delete(any());
        verify(subscriptionRepository, never()).deleteByIdAndUserId(any(), any());
    }

    @Test
    void deleteSubscription_ShouldThrow_WhenNotBelongsToUser() {
        Long userId = 1L;
        Long otherUserId = 2L;
        Long subscriptionId = 10L;

        User owner = new User(otherUserId, "Test User", "owner@example.com", null);
        Subscription subscription = new Subscription(
                subscriptionId, "Netflix", null, null, owner);

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User(
                userId, "Test User", "test@example.com", null)));
        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));

        assertThrows(ForbiddenException.class, () ->
                subscriptionService.deleteSubscription(userId, subscriptionId));
    }

    @Test
    void getTop3PopularSubscriptions_ShouldReturnSortedResults() {
        List<Object[]> mockResults = Arrays.asList(
                new Object[]{"Netflix", 150L},
                new Object[]{"Spotify", 100L},
                new Object[]{"YouTube Premium", 75L}
        );

        when(subscriptionRepository.findTop3PopularSubscriptions())
                .thenReturn(mockResults);

        List<Object[]> result = subscriptionService.getTop3PopularSubscriptions();

        assertEquals(3, result.size());
        assertEquals("Netflix", result.get(0)[0]);
        assertEquals(150L, result.get(0)[1]);

        verify(subscriptionRepository).findTop3PopularSubscriptions();
    }

}