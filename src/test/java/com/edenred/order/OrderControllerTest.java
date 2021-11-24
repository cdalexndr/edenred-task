package com.edenred.order;

import com.edenred.BaseDataTest;
import com.edenred.data.OrderService;
import com.edenred.data.ProductService;
import com.edenred.data.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest extends BaseDataTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Autowired OrderService orderService;
    @Autowired UserService userService;
    @Autowired ProductService productService;

    @Autowired TestOrderBuilderService orderBuilderService;

    @Test
    public void testOrderNotFound() throws Exception {
        mockMvc.perform( get( "/order/1111" ) )
                .andExpect( status().isNotFound() );
    }

    @Test
    public void testGetById() throws Exception {
        Order order = orderBuilderService.builder()
                .product( "product1", 1 )
                .product( "product2", 1 )
                .build();
        MvcResult result = mockMvc.perform( get( "/order/" + order.getId() ) )
                .andExpect( status().isOk() )
                .andReturn();
        Order resultOrder = objectMapper.readValue( result.getResponse().getContentAsString(), Order.class );
        assertEquals( order, resultOrder );
    }

    @Test
    public void testGetOrderHistory() throws Exception {
        Order order = orderBuilderService.builder()
                .product( "product", 1 )
                .finishedOrder()
                .build();
        User user = order.getUser();

        Order ignoreOrder = orderBuilderService.builder( user )
                .product( "product2", 1 )
                .build();

        MvcResult result = mockMvc.perform( get( "/orders/user/" + user.getId() ) )
                .andExpect( status().isOk() )
                .andReturn();
        List<Order> resultOrders = objectMapper.readValue( result.getResponse().getContentAsString(),
                new TypeReference<>() {} );
        assertEquals( 1, resultOrders.size() );
        assertEquals( order, resultOrders.get( 0 ) );
    }

    @Test
    public void testGetCurrentOrder() throws Exception {
        Order order = orderBuilderService.builder()
                .product( "product", 1 )
                .build();
        User user = order.getUser();
        MvcResult result = mockMvc.perform( get( "/order/current/user/" + user.getId() ) )
                .andExpect( status().isOk() )
                .andReturn();
        Order resultOrder = objectMapper.readValue( result.getResponse().getContentAsString(), Order.class );
        assertEquals( order, resultOrder );
    }

    @Test
    public void testFinishOrder() throws Exception {
        Order order = orderBuilderService.builder()
                .product( "product", 1 )
                .build();
        User user = order.getUser();
        mockMvc.perform( post( "/order/user/" + user.getId() ) )
                .andExpect( status().isOk() );
        List<Order> finishedOrders = orderService.getOrderHistoryForUser( user.getId() );
        assertEquals( finishedOrders.size(), 1 );
        assertEquals( order.getId(), finishedOrders.get( 0 ).getId() );
    }

    @Test
    public void testAddProduct() throws Exception {
        Order order = orderBuilderService.builder().build();
        User user = order.getUser();
        Product product = productService.create( "product", 1 );
        MvcResult result = mockMvc.perform( put( "/order/user/" + user.getId() + "/product/" + product.getId() ) )
                .andExpect( status().isOk() )
                .andReturn();
        Order resultOrder = objectMapper.readValue( result.getResponse().getContentAsString(), Order.class );
        assertEquals( resultOrder.getItems().size(), 1 );
        OrderItem item = resultOrder.getItems().get( 0 );
        assertEquals( product, item.getProduct() );
        assertEquals( 1, item.getQuantity() );
    }

    @Test
    public void testRemoveProduct() throws Exception {
        Order order = orderBuilderService.builder()
                .product( "product", 2 )
                .build();
        User user = order.getUser();
        Product product = order.getItems().iterator().next().getProduct();
        MvcResult result = mockMvc.perform( delete( "/order/user/" + user.getId() + "/product/" + product.getId() ) )
                .andExpect( status().isOk() )
                .andReturn();
        Order resultOrder = objectMapper.readValue( result.getResponse().getContentAsString(), Order.class );
        assertEquals( resultOrder.getItems().size(), 0 );
    }

    @Test
    public void testIncreaseQuantity() throws Exception {
        Order order = orderBuilderService.builder()
                .product( "product", 1, 2 )
                .build();
        User user = order.getUser();
        Product product = order.getItems().iterator().next().getProduct();
        MvcResult result = mockMvc.perform( put(
                        "/order/user/" + user.getId() + "/product/" + product.getId() + "/quantity" ) )
                .andExpect( status().isOk() )
                .andReturn();
        Order resultOrder = objectMapper.readValue( result.getResponse().getContentAsString(), Order.class );
        assertEquals( resultOrder.getItems().size(), 1 );
        OrderItem item = resultOrder.getItems().get( 0 );
        assertEquals( product, item.getProduct() );
        assertEquals( 2, item.getQuantity() );
    }

    @Test
    public void testDecreaseQuantity() throws Exception {
        Order order = orderBuilderService.builder()
                .product( "product", 2 )
                .build();
        User user = order.getUser();
        Product product = order.getItems().iterator().next().getProduct();
        MvcResult result = mockMvc.perform( delete(
                        "/order/user/" + user.getId() + "/product/" + product.getId() + "/quantity" ) )
                .andExpect( status().isOk() )
                .andReturn();
        Order resultOrder = objectMapper.readValue( result.getResponse().getContentAsString(), Order.class );
        assertEquals( resultOrder.getItems().size(), 1 );
        OrderItem item = resultOrder.getItems().get( 0 );
        assertEquals( product, item.getProduct() );
        assertEquals( 1, item.getQuantity() );
    }
}