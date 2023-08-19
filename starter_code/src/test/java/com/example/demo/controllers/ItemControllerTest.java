package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
    private ItemController itemController;
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setup(){
        itemController = new ItemController();
        TestUtils.injectObject(itemController, "itemRepository", itemRepository);

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


        when(itemRepository.findAll()).thenReturn(items);
        when(itemRepository.findById(2L)).thenReturn(Optional.of(item2));
        when(itemRepository.findByName("item2")).thenReturn(Collections.singletonList(item2));
    }

    @Test
    public void getItems() throws Exception{
        final ResponseEntity<List<Item>> itemsResponseEntity = itemController.getItems();

        assertNotNull(itemsResponseEntity);
        assertEquals(2, Objects.requireNonNull(itemsResponseEntity.getBody()).size());
    }

    @Test
    public void getItemById() throws Exception{
        final ResponseEntity<Item> itemResponseEntity = itemController.getItemById(2L);

        assertNotNull(itemResponseEntity);
        assertEquals("item2", itemResponseEntity.getBody().getName());
    }

    @Test
    public void  getItemsByName() throws Exception{
        final ResponseEntity<List<Item>> itemsResponseEntity = itemController.getItemsByName("item2");

        assertNotNull(itemsResponseEntity);
        assertEquals(1, Objects.requireNonNull(itemsResponseEntity.getBody()).size());
    }
}
