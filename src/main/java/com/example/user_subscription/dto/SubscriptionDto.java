package com.example.user_subscription.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionDto {

    private Long id;

    @NotBlank(message = "service name cannot be null")
    @Size(min = 2, max = 30, message = "service name must be between 2 and 30 characters")
    private String serviceName;

    private LocalDate startDate;

    private LocalDate endDate;

    private Long userId;
}
