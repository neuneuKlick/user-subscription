package com.example.user_subscription.controller;

import com.example.user_subscription.dto.SubscriptionDto;
import com.example.user_subscription.service.SubscriptionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{id}/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriptionDto addSubscription(@NotNull @PathVariable Long id,
                                           @Valid @RequestBody SubscriptionDto subscriptionDto) {
        return subscriptionService.addSubscription(id, subscriptionDto);
    }

    @GetMapping
    public List<SubscriptionDto> getUserSubscriptions(@NotNull @PathVariable Long id) {
        return subscriptionService.getUserSubscriptions(id);
    }

    @DeleteMapping("/{subId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSubscription(
            @NotNull @PathVariable Long id,
            @NotNull @PathVariable Long subId) {
        subscriptionService.deleteSubscription(id, subId);
    }

    @GetMapping("/top")
    public List<Object[]> getTop3PopularSubscriptions() {
        return subscriptionService.getTop3PopularSubscriptions();
    }
}
