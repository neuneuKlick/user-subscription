package com.example.user_subscription.service.impl;

import com.example.user_subscription.dto.UserDto;
import com.example.user_subscription.exception.exceptions.user.UserNotFoundException;
import com.example.user_subscription.exception.exceptions.user.UserAlreadyExistsException;
import com.example.user_subscription.mapper.UserMapper;
import com.example.user_subscription.model.User;
import com.example.user_subscription.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_ShouldReturnUserDto_WhenEmailIsUnique() {
        UserDto userDto = new UserDto(null,"Test User", "test@example.com");
        User user = new User(null,"Test User", "test@example.com", null);
        User savedUser = new User(1L, "Test User", "test@example.com", null);

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userMapper.toUser(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(new UserDto(1L, "Test User", "test@example.com"));

        UserDto result = userService.createUser(userDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailExists() {
        UserDto userDto = new UserDto(null,"Existing User", "existing@example.com");
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(userDto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserById_ShouldReturnUser_WhenExists() {
        Long userId = 1L;
        User user = new User(userId, "Test User", "test@example.comr", null);
        UserDto expectedDto = new UserDto(userId, "Test User", "test@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(expectedDto);

        UserDto result = userService.getUserById(userId);

        assertEquals(expectedDto, result);
    }

    @Test
    void getUserById_ShouldThrow_WhenNotExists() {
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void updateUser_ShouldUpdate_WhenDataValid() {
        Long userId = 1L;
        UserDto updateDto = new UserDto(null, "newUsername", "new@email.com");
        User existingUser = new User(userId, "oldUsername", "old@email.com", null);
        User savedUser = new User(userId, "newUsername", "new@email.com", null);
        UserDto expectedDto = new UserDto(userId, "newUsername", "new@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("new@email.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(expectedDto);

        UserDto result = userService.updateUser(userId, updateDto);

        assertNotNull(result);
        assertEquals("newUsername", result.getName());
        assertEquals("new@email.com", result.getEmail());
        verify(userRepository).save(existingUser);
    }

    @Test
    void deleteUser_ShouldDelete_WhenUserExists() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);
        doNothing().when(userRepository).deleteById(userId);

        userService.deleteUser(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUser_ShouldThrow_WhenUserNotFound() {
        Long userId = 999L;
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> userService.deleteUser(userId));
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {

        List<User> mockUsers = Arrays.asList(
                new User(1L, "User 1", "user1@test.com", null),
                new User(2L, "User 2", "user2@test.com", null)
        );

        List<UserDto> mockDtos = Arrays.asList(
                new UserDto(1L, "User 1", "user1@test.com"),
                new UserDto(2L, "User 2", "user2@test.com")
        );

        when(userRepository.findAll()).thenReturn(mockUsers);
        when(userMapper.toDtoList(mockUsers)).thenReturn(mockDtos);

        List<UserDto> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("user1@test.com", result.get(0).getEmail());
    }

}