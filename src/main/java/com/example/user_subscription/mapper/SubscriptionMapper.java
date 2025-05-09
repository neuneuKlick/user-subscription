package com.example.user_subscription.mapper;

import com.example.user_subscription.dto.SubscriptionDto;
import com.example.user_subscription.model.Subscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    Subscription toSubscription(SubscriptionDto subscriptionDto);

    @Mapping(source = "user.id", target = "userId")
    SubscriptionDto toDto(Subscription subscription);
}
