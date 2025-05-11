package com.example.user_subscription.exception.handler.subscription;

import com.example.user_subscription.controller.SubscriptionController;
import com.example.user_subscription.exception.ErrorResponse;
import com.example.user_subscription.exception.exceptions.subscription.SubscriptionConflictException;
import com.example.user_subscription.exception.exceptions.subscription.SubscriptionForbiddenException;
import com.example.user_subscription.exception.exceptions.subscription.SubscriptionIllegalArgumentException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = SubscriptionController.class)
public class SubscriptionControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(SubscriptionIllegalArgumentException.class)
    public ErrorResponse handleSubscriptionIllegalArgument(SubscriptionIllegalArgumentException ex) {
        return new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(SubscriptionConflictException.class)
    public ErrorResponse handleSubscriptionConflict(SubscriptionConflictException ex) {
        return new ErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(SubscriptionForbiddenException.class)
    public ErrorResponse handleSubscriptionForbidden(SubscriptionForbiddenException ex) {
        return new ErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN);
    }
}
