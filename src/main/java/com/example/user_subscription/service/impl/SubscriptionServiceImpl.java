package com.example.user_subscription.service.impl;

import com.example.user_subscription.dto.SubscriptionDto;
import com.example.user_subscription.exception.exceptions.subscription.SubscriptionConflictException;
import com.example.user_subscription.exception.exceptions.subscription.SubscriptionForbiddenException;
import com.example.user_subscription.exception.exceptions.user.UserIllegalArgumentException;
import com.example.user_subscription.exception.exceptions.user.UserNotFoundException;
import com.example.user_subscription.mapper.SubscriptionMapper;
import com.example.user_subscription.model.Subscription;
import com.example.user_subscription.model.User;
import com.example.user_subscription.repository.SubscriptionRepository;
import com.example.user_subscription.repository.UserRepository;
import com.example.user_subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final SubscriptionMapper subscriptionMapper;
    @Override
    @Transactional(timeout = 3, rollbackFor = {UserNotFoundException.class, SubscriptionConflictException.class})
    public SubscriptionDto addSubscription(Long userId, SubscriptionDto subscriptionDto) {
        if (userId == null || userId <= 0) {
            throw new UserIllegalArgumentException("ID пользователя должно быть положительным числом");
        }

        if (subscriptionDto == null) {
            throw new UserIllegalArgumentException("Данные подписки не могут быть null");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("Пользователь с ID %d не найден", userId)
                ));

        if (subscriptionRepository.existsByUserAndServiceName(user, subscriptionDto.getServiceName())) {
            throw new SubscriptionConflictException(
                    String.format("У пользователя уже есть подписка типа %s", subscriptionDto.getServiceName())
            );
        }

        Subscription subscription = subscriptionMapper.toSubscription(subscriptionDto);
        subscription.setUser(user);
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(subscriptionDto.getEndDate());
        subscription.setServiceName(subscriptionDto.getServiceName());

        subscriptionRepository.save(subscription);

        userRepository.save(user);

        return subscriptionMapper.toDto(subscription);
    }

    @Override
    @Transactional(readOnly = true, timeout = 3, rollbackFor = {UserNotFoundException.class})
    public List<SubscriptionDto> getUserSubscriptions(Long userId) {
        if (userId == null || userId <= 0) {
            throw new UserIllegalArgumentException("ID пользователя должно быть положительным числом");
        }

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Пользователь с ID " + userId + " не найден");
        }

        List<Subscription> subscriptions = subscriptionRepository.findWithUserByUserId(userId);

        return subscriptions.stream()
                .map(subscriptionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(timeout = 3, rollbackFor = {UserNotFoundException.class})
    public void deleteSubscription(Long userId, Long subscriptionId) {
        if (userId == null || userId <= 0 ) {
            throw new UserIllegalArgumentException("Неверный ID пользователя");
        }
        if (subscriptionId == null || subscriptionId <= 0) {
            throw new UserIllegalArgumentException("Неверный ID подписки");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("Пользователь с ID %d не найден", userId)
                ));

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("Подписка с ID %d не найдена", subscriptionId)
                ));

        if (!subscription.getUser().getId().equals(userId)) {
            throw new SubscriptionForbiddenException(
                    String.format("Подписка %d не принадлежит пользователю %d", subscriptionId, userId)
            );
        }

        subscriptionRepository.deleteByIdAndUserId(subscriptionId, userId);

        log.info("Удалена подписка ID {} пользователя ID {}", subscriptionId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getTop3PopularSubscriptions() {
        return subscriptionRepository.findTop3PopularSubscriptions();
    }
}
