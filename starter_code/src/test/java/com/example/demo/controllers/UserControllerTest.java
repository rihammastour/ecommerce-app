package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserController userController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setup(){
        userController = new UserController();
        TestUtils.injectObject(userController, "userRepository", userRepository);
        TestUtils.injectObject(userController, "cartRepository", cartRepository);
        TestUtils.injectObject(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
    }


    private ResponseEntity<User> getUserResponseEntity(String pass) {
        when(bCryptPasswordEncoder.encode(pass)).thenReturn("hashedPassword");
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("test");
        createUserRequest.setPassword(pass);
        createUserRequest.setConfirmPassword(pass);

        final ResponseEntity<User> response = userController.createUser(createUserRequest);
        return response;
    }

    @Test
    public void createUser() throws Exception{
        final ResponseEntity<User> response = getUserResponseEntity("password123456");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("hashedPassword", user.getPassword());
    }

    @Test
    public void createUser_BadRequest() throws Exception{
        final ResponseEntity<User> response = getUserResponseEntity("12345");
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());

    }

    @Test
    public void findById() throws Exception{
        final ResponseEntity<User> userResponseEntity = getUserResponseEntity("password123456");

        when(userRepository.findById(0L)).thenReturn(java.util.Optional.ofNullable(userResponseEntity.getBody()));

        final ResponseEntity<User> idResponseEntity = userController.findById(0L);
        User user = idResponseEntity.getBody();
        assertNotNull(user);
        assertEquals(0L, user.getId());;
    }

    @Test
    public void findByUserName() throws Exception{
        final ResponseEntity<User> userResponseEntity = getUserResponseEntity("password123456");

        when(userRepository.findByUsername("test")).thenReturn(userResponseEntity.getBody());

        final ResponseEntity<User> idResponseEntity = userController.findByUserName("test");
        User user = idResponseEntity.getBody();
        assertNotNull(user);
        assertEquals("test", user.getUsername());;
    }


}
