package com.example.user_subscription.service.impl;
import com.example.user_subscription.dto.UserDto;
import com.example.user_subscription.exception.exceptions.user.*;
import com.example.user_subscription.mapper.UserMapper;
import com.example.user_subscription.model.User;
import com.example.user_subscription.repository.UserRepository;
import com.example.user_subscription.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional()
    public UserDto createUser(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new UserAlreadyExistsException("Пользователь с email " + userDto.getEmail() + " уже существует");
        }
        User user = userMapper.toUser(userDto);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public UserDto getUserById(Long id) {
        if (id == null || id <= 0) {
            throw new UserIllegalArgumentException("ID пользователя должно быть положительным числом");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id: " + id + " не найден"));

        return userMapper.toDto(user);
    }


    @Override
    @Transactional()
    public UserDto updateUser(Long id, UserDto userDto) {
        if (id == null || id <= 0) {
            throw new UserIllegalArgumentException("ID пользователя должно быть положительным числом");
        }

        if (userDto == null) {
            throw new UserIllegalArgumentException("Данные пользователя не могут быть null");
        }

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("Пользователь с ID %d не найден", id)
                ));

        if (userDto.getEmail() != null && !userDto.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(userDto.getEmail())) {
                throw new UserConflictException("Email " + userDto.getEmail() + " уже используется");
            }
        }

        if (userDto.getName() != null) {
            existingUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            existingUser.setEmail(userDto.getEmail());
        }

        User updatedUser = userRepository.save(existingUser);
        return userMapper.toDto(updatedUser);
    }


    @Override
    @Transactional()
    public void deleteUser(Long id) {
        if (id == null || id <= 0) {
            throw new UserIllegalArgumentException("ID пользователя должно быть положительным числом");
        }

        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("Пользователь с ID " + id + " не найден");
        }

        try {
            userRepository.deleteById(id);
            log.info("Пользователь с ID {} успешно удален", id);
        } catch (UserEmptyResultDataAccessException e) {
            log.error("Ошибка при удалении пользователя: {}", e.getMessage());
            throw new UserNotFoundException("Пользователь уже был удален");
        } catch (Exception e) {
            log.error("Неожиданная ошибка при удалении: {}", e.getMessage());
            throw new UserDataAccessException("Ошибка при удалении пользователя") {};
        }
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.toDtoList(users);
    }
}
