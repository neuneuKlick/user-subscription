package com.example.user_subscription.service;

import com.example.user_subscription.dto.SubscriptionDto;

import java.util.List;

public interface SubscriptionService {
    SubscriptionDto addSubscription(Long userId, SubscriptionDto subscriptionDto);
    List<SubscriptionDto> getUserSubscriptions(Long userId);
    void deleteSubscription(Long userId, Long subscriptionId);
    List<Object[]> getTop3PopularSubscriptions();
}
