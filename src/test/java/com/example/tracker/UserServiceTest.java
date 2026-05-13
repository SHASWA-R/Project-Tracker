package com.example.tracker.service;

import com.example.tracker.model.User;
import com.example.tracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUserRepository_Save() {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setFullName("Test User");

        when(userRepository.save(any(User.class))).thenReturn(user);
        User result = userRepository.save(user);

        assertNotNull(result);
        assertEquals("test@gmail.com", result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUserRepository_FindByEmail() {
        User user = new User();
        user.setEmail("test@gmail.com");

        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));
        Optional<User> result = userRepository.findByEmail("test@gmail.com");

        assertTrue(result.isPresent());
        assertEquals("test@gmail.com", result.get().getEmail());
    }

    @Test
    void testUserRepository_FindById() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@gmail.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Optional<User> result = userRepository.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void testUserRepository_DeleteById() {
        doNothing().when(userRepository).deleteById(1L);
        userRepository.deleteById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testUser_Creation() {
        User user = new User();
        user.setId(1L);
        user.setEmail("john@gmail.com");
        user.setFullName("John Doe");

        assertEquals(1L, user.getId());
        assertEquals("john@gmail.com", user.getEmail());
        assertEquals("John Doe", user.getFullName());
    }
}