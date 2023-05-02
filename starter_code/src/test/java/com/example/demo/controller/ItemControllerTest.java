package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
        Item item = new Item();
        item.setId(1L);
        item.setName("Table");
        BigDecimal price = BigDecimal.valueOf(2.99);
        item.setPrice(price);
        item.setDescription("A white colored table from");


        Item item2 = new Item();
        item2.setId(1L);
        item2.setName("Table");
        item.setPrice(BigDecimal.valueOf(2.99));
        item.setDescription("A white colored table from");

        when(itemRepository.findAll()).thenReturn(Arrays.asList(item, item2));
        when(itemRepository.findById(1L)).thenReturn(java.util.Optional.of(item));
        when(itemRepository.findByName("Table")).thenReturn(Arrays.asList(item, item2));

    }

    @Test
    public void verify_get_all_items() {
        ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<Item> items = response.getBody();
        assertNotNull(items);
        assertEquals(2, items.size());
    }

    @Test
    public void verify_get_item_by_id() {
        ResponseEntity<Item> response = itemController.getItemById(1L);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Item i = response.getBody();
        assertNotNull(i);
    }

    @Test
    public void verify_get_item_by_id_not_found() {
        ResponseEntity<Item> response = itemController.getItemById(3L);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void verify_get_items_by_name() {
        ResponseEntity<List<Item>> response = itemController.getItemsByName("Table");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<Item> items = response.getBody();
        assertNotNull(items);
        assertEquals(2, items.size());
    }

    @Test
    public void get_items_by_name_not_found() {
        ResponseEntity<List<Item>> response = itemController.getItemsByName("Wrong");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
