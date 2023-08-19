package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {
    private CartController cartController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setup(){
        cartController = new CartController();
        TestUtils.injectObject(cartController, "userRepository", userRepository);
        TestUtils.injectObject(cartController, "cartRepository", cartRepository);
        TestUtils.injectObject(cartController, "itemRepository", itemRepository);

        User user = new User();
        Cart cart = new Cart();
        user.setId(0);
        user.setUsername("test");
        user.setPassword("pass12345");
        user.setCart(cart);

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

        when(userRepository.findByUsername("test")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(java.util.Optional.of(item));
        when(itemRepository.findById(2L)).thenReturn(java.util.Optional.of(item2));
    }

    private ResponseEntity<Cart> getCartResponseEntity(String username, Long itemId) {
        ModifyCartRequest modifyCartRequest = getModifyCartRequest(username, itemId);
        final ResponseEntity<Cart> cartResponseEntity = cartController.addTocart(modifyCartRequest);
        return cartResponseEntity;
    }

    private static ModifyCartRequest getModifyCartRequest(String username, Long itemId) {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(itemId);
        modifyCartRequest.setUsername(username);
        modifyCartRequest.setQuantity(1);
        return modifyCartRequest;
    }

    @Test
    public void addToCart() throws Exception{
        final ResponseEntity<Cart> cartResponseEntity = getCartResponseEntity("test", 1L);

        assertNotNull(cartResponseEntity);
        assertEquals(BigDecimal.valueOf(20), Objects.requireNonNull(cartResponseEntity.getBody()).getTotal());
    }

    @Test
    public void addToCart_userNotFound() throws Exception{
        final ResponseEntity<Cart> cartResponseEntity = getCartResponseEntity("test2", 1L);

        assertNotNull(cartResponseEntity);
        assertEquals(404, cartResponseEntity.getStatusCodeValue());
    }

    @Test
    public void addToCart_itemNotFound() throws Exception{
        final ResponseEntity<Cart> cartResponseEntity = getCartResponseEntity("test", 5L);

        assertNotNull(cartResponseEntity);
        assertEquals(404, cartResponseEntity.getStatusCodeValue());
    }

    @Test
    public void removeFromCart() throws Exception{
        ModifyCartRequest cartRequest = getModifyCartRequest("test", 2L);

        final ResponseEntity<Cart> removedCartResponseEntity = cartController.removeFromcart(cartRequest);
        assertNotNull(removedCartResponseEntity);
        assertEquals(BigDecimal.valueOf(5), Objects.requireNonNull(removedCartResponseEntity.getBody()).getTotal());
    }

    @Test
    public void removeFromCart_userNotFound() throws Exception{
        ModifyCartRequest cartRequest = getModifyCartRequest("test2", 2L);

        final ResponseEntity<Cart> removedCartResponseEntity = cartController.removeFromcart(cartRequest);
        assertNotNull(removedCartResponseEntity);
        assertEquals(404, removedCartResponseEntity.getStatusCodeValue());
    }

    @Test
    public void removeFromCart_itemNotFound() throws Exception{
        ModifyCartRequest cartRequest = getModifyCartRequest("test", 5L);

        final ResponseEntity<Cart> removedCartResponseEntity = cartController.removeFromcart(cartRequest);
        assertNotNull(removedCartResponseEntity);
        assertEquals(404, removedCartResponseEntity.getStatusCodeValue());
    }
}
