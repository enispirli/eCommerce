package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

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
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);

        User user = new User();
        Cart cart = new Cart();
        user.setId(1);
        user.setUsername("enispirli");
        user.setPassword("yumpas255");
        user.setCart(cart);
        when(userRepository.findByUsername("enispirli")).thenReturn(user);

        Item item = new Item();
        item.setId(1L);
        item.setName("Table");
        BigDecimal price = BigDecimal.valueOf(2.99);
        item.setPrice(price);
        item.setDescription("A white colored table from");
        when(itemRepository.findById(1L)).thenReturn(java.util.Optional.of(item));
    }

    @Test
    public void verify_add_to_cart() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(1);
        modifyCartRequest.setUsername("enispirli");
        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals(BigDecimal.valueOf(2.99), cart.getTotal());

    }

    @Test
    public void verify_add_to_cart_user_not_exists() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(1);
        modifyCartRequest.setUsername("wrong");
        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void verify_add_to_cart_item_not_exists() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(2L);
        modifyCartRequest.setQuantity(1);
        modifyCartRequest.setUsername("enispirli");
        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void verify_remove_from_cart() {
        ModifyCartRequest modifyCartRequest =  new ModifyCartRequest();
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(1);
        modifyCartRequest.setUsername("enispirli");
       ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Cart card = response.getBody();
        assertNotNull(card);
        assertEquals(BigDecimal.valueOf(-2.99), card.getTotal());

    }
}
