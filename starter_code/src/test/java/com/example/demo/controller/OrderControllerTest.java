package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.OrderController;
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
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;
    private OrderRepository orderRepository = mock(OrderRepository.class);
    private UserRepository userRepository = mock(UserRepository.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
        TestUtils.injectObjects(orderController, "userRepository", userRepository);

        when(userRepository.findByUsername("enispirli")).thenReturn(createUser());
        when(orderRepository.findByUser(any())).thenReturn(createOrders());

    }


    @Test
    public void verify_submit(){

        ResponseEntity<UserOrder> response = orderController.submit("enispirli");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        UserOrder order = response.getBody();

        assertEquals(createItems(), order.getItems());
        assertEquals(createUser().getId(), order.getUser().getId());

        assertEquals(1, order.getItems().size());
    }

    @Test
    public void verify_submit_order_user_not_found() {
        ResponseEntity<UserOrder> response = orderController.submit("wrong");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void verify_get_orders_for_user_name() {
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("enispirli");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<UserOrder> orders = response.getBody();
        assertNotNull(orders);

    }

    @Test
    public void verify_get_orders_for_user_not_found() {
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("someone");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

    }

    private List<UserOrder> createOrders() {
        List<UserOrder> orders = new ArrayList<>();

        IntStream.range(0, 2).forEach(i -> {
            UserOrder order = new UserOrder();
            User user = createUser();
            Cart cart = createCart(user);

            order.setItems(cart.getItems());
            order.setTotal(cart.getTotal());
            order.setUser(user);
            order.setId(Long.valueOf(i));

            orders.add(order);
        });
        return orders;
    }

    private User createUser() {
        User user = new User();
        user.setId(0L);
        user.setUsername("enispirli");
        user.setPassword("yumpas255");
        user.setCart(createCart(user));

        return user;
    }

    private Cart createCart(User user) {
        Cart cart = new Cart();
        cart.setId(0L);
        cart.setUser(user);
        cart.setItems(createItems());
        BigDecimal total = BigDecimal.valueOf(2.99);
        cart.setTotal(total);
        user.setCart(cart);
        return cart;
    }

    private List<Item> createItems() {

        Item item = new Item();
        item.setId(1L);
        item.setName("Table");
        BigDecimal price = BigDecimal.valueOf(2.99);
        item.setPrice(price);
        item.setDescription("A white colored table from");
        List<Item> items = new ArrayList<>();
        items.add(item);
        return items;
    }
}
