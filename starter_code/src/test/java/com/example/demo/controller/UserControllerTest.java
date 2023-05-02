package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private PasswordEncoder passwordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {

        userController = new UserController(null, null,null);
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "passwordEncoder", passwordEncoder);

        User user = new User();
        Cart cart = new Cart();
        user.setId(1);
        user.setUsername("enispirli");
        user.setPassword("yumpas255");
        user.setCart(cart);
        when(userRepository.findByUsername("enispirli")).thenReturn(user);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("nothing")).thenReturn(null);
    }

    @Test
    public void verify_create_user(){
        when(passwordEncoder.encode("yumpas255")).thenReturn("encodedPassword");

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("enispirli");
        request.setPassword("yumpas255");
        request.setConfirmPassword("yumpas255");

        ResponseEntity<User> response = userController.createUser(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();

        assertNotNull(user);

        assertEquals(0, user.getId());
        assertEquals("enispirli", user.getUsername());
        assertEquals("encodedPassword", user.getPassword());
    }

    @Test
    public void verify_create_user_password_too_short() {
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("enispirli");
        userRequest.setPassword("pass");
        userRequest.setConfirmPassword("pass");
        final ResponseEntity<User> response = userController.createUser(userRequest);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void verify_create_user_password_not_match() {
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("enispirli");
        userRequest.setPassword("password");
        userRequest.setConfirmPassword("wrongpass");
        ResponseEntity<User> response = userController.createUser(userRequest);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }
    @Test
    public void verify_find_by_id(){
        final ResponseEntity<User> response = userController.findById(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User actualUser = response.getBody();

        assertNotNull(actualUser);

        assertEquals(1L, actualUser.getId());
        assertEquals("enispirli", actualUser.getUsername());
        assertEquals("yumpas255", actualUser.getPassword());
    }

    @Test
    public void verify_find_by_username(){
        ResponseEntity<User> response = userController.findByUserName("enispirli");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User actualUser = response.getBody();

        assertNotNull(actualUser);

        assertEquals(1L, actualUser.getId());
        assertEquals("enispirli", actualUser.getUsername());
        assertEquals("yumpas255", actualUser.getPassword());
    }

    @Test
    public void verify_find_by_name_not_exist() {
        final ResponseEntity<User> response = userController.findByUserName("nothing");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void verify_find_user_by_id_not_exist() {
        final ResponseEntity<User> response = userController.findById(2L);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
