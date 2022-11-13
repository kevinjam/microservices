package com.thinkdevs.OrderService.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.thinkdevs.OrderService.entity.Order;
import com.thinkdevs.OrderService.model.OrderRequest;
import com.thinkdevs.OrderService.model.PaymentMode;
import com.thinkdevs.OrderService.repository.OrderRepository;
import com.thinkdevs.OrderService.service.OrderService;
import com.thinkdevs.OrderServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.util.StreamUtils.copyToString;

@SpringBootTest({"server.port=0"})
@EnableConfigurationProperties
@AutoConfigureMockMvc
@ContextConfiguration(classes = {OrderServiceConfig.class})
@Slf4j
class OrderControllerTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MockMvc mockMvc;

    @RegisterExtension
    static WireMockExtension wireMockServer
            = WireMockExtension.newInstance()
            .options(WireMockConfiguration
                    .wireMockConfig()
                    .port(8080))
            .build();

    private ObjectMapper objectMapper
            = new ObjectMapper()
            .findAndRegisterModules()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @BeforeEach
    void setUp() throws IOException {
        getProductDetailsResponse();
        doPayment();
        getPaymentDetails();
        reduceQuantity();
    }

    private void reduceQuantity() {
        stubFor(put(urlMatching("/product/reduceQuantity/.*"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));

    }

    private void getPaymentDetails() throws IOException {
        stubFor(get(urlMatching("/payment/.*"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(copyToString(OrderControllerTest.class
                                        .getClassLoader()
                                        .getResourceAsStream("mock/GetPayment.json"),
                                Charset.defaultCharset()))));
    }

    private void doPayment() {
        stubFor(post(urlEqualTo("/payment"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));

    }

    private void getProductDetailsResponse() throws IOException {
        //get
        wireMockServer
                .stubFor(get("/product/1")
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.OK.value())
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(copyToString(
                                        OrderControllerTest.class
                                                .getClassLoader()
                                                .getResourceAsStream("mock/Getproduct.json"),
                                        Charset.defaultCharset()
                                )))
                );


    }

    @Test
     void test_When_Place_Order_DoPayment_Success() throws Exception {
        //first place order

        //get order by id from db

        //checkout

        OrderRequest orderRequest = getMockOrderRequest();
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/order/placeOrder")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("Customer")))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(orderRequest))
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        var orderId = mvcResult.getResponse().getContentAsString();
        log.info("order id ---- {} ",orderId);
        Optional<Order> order = orderRepository.findById(Integer.valueOf(orderId));

        assertTrue(order.isPresent());
        Order o = order.get();
        assertEquals(Integer.parseInt(orderId),o.getId());
        assertEquals("PLACED", o.getOrderStatus());
        assertEquals(orderRequest.getTotalAmount(),o.getAmount());



    }

    private OrderRequest getMockOrderRequest() {
        return OrderRequest.builder()
                .productId(1)
                .paymentMode(PaymentMode.CASH)
                .quantity(10)
                .totalAmount(200)
                .build();
    }
}