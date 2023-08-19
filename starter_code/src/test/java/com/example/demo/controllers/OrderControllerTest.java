package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    private OrderController orderController;
    private UserRepository userRepository = mock(UserRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setup(){
        orderController = new OrderController();
        TestUtils.injectObject(orderController, "userRepository", userRepository);
        TestUtils.injectObject(orderController, "orderRepository", orderRepository);

        User user = new User();
        Cart cart = new Cart();
        user.setId(0);
        user.setUsername("test");
        user.setPassword("pass12345");

        Item item = new Item();
        item.setId(1L);
        item.setName("item");
        BigDecimal price = BigDecimal.valueOf(5);
        item.setPrice(price);
        item.setDescription("first item");

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("item2");
        BigDecimal price2 = BigDecimal.valueOf(10);
        item2.setPrice(price2);
        item2.setDescription("Second item");

        List<Item> items = new ArrayList<>();
        items.add(item);
        items.add(item2);
        cart.setItems(items);
        BigDecimal sum;
        sum = price.add(price2);
        cart.setTotal(sum);
        user.setCart(cart);

        when(userRepository.findByUsername("test")).thenReturn(user);

    }

    @Test
    public void submit() throws Exception{
        final ResponseEntity<UserOrder> orderResponseEntity = orderController.submit("test");

        assertNotNull(orderResponseEntity);
        assertEquals(2, Objects.requireNonNull(orderResponseEntity.getBody()).getItems().size());
    }

    @Test
    public void submit_userNotFound() throws Exception{
        final ResponseEntity<UserOrder> orderResponseEntity = orderController.submit("test2");

        assertNotNull(orderResponseEntity);
        assertEquals(404, orderResponseEntity.getStatusCodeValue());
    }

    @Test
    public void getOrdersForUser() throws Exception{
        final ResponseEntity<List<UserOrder>> ordersResponseEntity = orderController.getOrdersForUser("test");
        assertNotNull(ordersResponseEntity);

        List<UserOrder> userOrders = ordersResponseEntity.getBody();
        assertNotNull(userOrders);
    }
    @Test
    public void getOrdersForUser_userNotFound() throws Exception{
        final ResponseEntity<List<UserOrder>> ordersResponseEntity = orderController.getOrdersForUser("test2");

        assertNotNull(ordersResponseEntity);
        assertEquals(404, ordersResponseEntity.getStatusCodeValue());
    }
}
