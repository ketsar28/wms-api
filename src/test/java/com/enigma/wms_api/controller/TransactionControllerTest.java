package com.enigma.wms_api.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private static String branchId;
    private static String productPriceId;
    private static String productPriceId2;
    private static String billId;


    @Order(1)
    @Test
    public void testCreateBranch() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("branchName", "Cilandak");
        request.put("branchCode", "0205");
        request.put("address", "Jl. Cilandak Raya No.XX");
        request.put("phoneNumber", "08123441234");

        mockMvc.perform(post("/api/branch")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                ).andExpectAll(status().isCreated())
                .andDo(result -> {
                    String jsonString = result.getResponse().getContentAsString();
                    Map<String, Object> mapResponse = objectMapper.readValue(jsonString, new TypeReference<>() {
                    });

                    Map<String, Object> data = (Map<String, Object>) mapResponse.get("data");
                    branchId = data.get("branchId").toString();
                });
    }

    @Order(2)
    @Test
    public void testCreateProduct() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("productCode", "01-001");
        request.put("productName", "Nasi Putih");
        request.put("price", 5000);
        request.put("branchId", branchId);

        mockMvc.perform(post("/api/products")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(status().isCreated())
                .andDo(result -> {
                    String jsonString = result.getResponse().getContentAsString();
                    Map<String, Object> mapResponse = objectMapper.readValue(jsonString, new TypeReference<>() {
                    });
                    Map<String, Object> data = (Map<String, Object>) mapResponse.get("data");

                    productPriceId = data.get("productPriceId").toString();
                });

        Map<String, Object> request2 = new HashMap<>();
        request2.put("productCode", "01-002");
        request2.put("productName", "Aneka Tumisan");
        request2.put("price", 2000);
        request2.put("branchId", branchId);

        mockMvc.perform(post("/api/products")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpectAll(status().isCreated())
                .andDo(result -> {
                    String jsonString = result.getResponse().getContentAsString();
                    Map<String, Object> mapResponse = objectMapper.readValue(jsonString, new TypeReference<>() {
                    });
                    Map<String, Object> data = (Map<String, Object>) mapResponse.get("data");

                    productPriceId2 = data.get("productPriceId").toString();
                });

    }

    @Order(3)
    @Test
    public void testCreateTransaction() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("transactionType", "1");

        Map<String, Object> billDetailRequest1 = new HashMap<>();
        billDetailRequest1.put("productPriceId", productPriceId);
        billDetailRequest1.put("quantity", 1);

        Map<String, Object> billDetailRequest2 = new HashMap<>();
        billDetailRequest2.put("productPriceId", productPriceId2);
        billDetailRequest2.put("quantity", 1);

        List<Map<String, Object>> billDetails = List.of(billDetailRequest1, billDetailRequest2);
        request.put("billDetails", billDetails);

        mockMvc.perform(post("/api/transactions")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(status().isCreated())
                .andDo(result -> {
                    String jsonString = result.getResponse().getContentAsString();
                    Map<String, Object> mapResponse = objectMapper.readValue(jsonString, new TypeReference<>() {
                    });
                    Map<String, Object> data = (Map<String, Object>) mapResponse.get("data");

                    assertNotNull(data.get("billId"));
                    assertNotNull(data.get("receiptNumber"));
                    assertEquals("EAT_IN", data.get("transactionType"));
                    assertNotNull(data.get("transDate"));

                    assertInstanceOf(List.class, data.get("billDetails"));
                    List<Map<String, Object>> billDetailsResponses = (List<Map<String, Object>>) data.get("billDetails");
                    assertEquals(2, billDetailsResponses.size());
                    billId = data.get("billId").toString();
                });
    }

    @Test
    @Order(4)
    public void testGetTransactionById() throws Exception {
        mockMvc.perform(get("/api/transactions/" + billId)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpectAll(status().isOk())
                .andDo(result -> {
                    String jsonString = result.getResponse().getContentAsString();
                    Map<String, Object> mapResponse = objectMapper.readValue(jsonString, new TypeReference<>() {
                    });
                    Map<String, Object> data = (Map<String, Object>) mapResponse.get("data");

                    assertNotNull(data.get("billId"));
                    assertNotNull(data.get("receiptNumber"));
                    assertEquals("EAT_IN", data.get("transactionType"));
                    assertNotNull(data.get("transDate"));
                    assertInstanceOf(List.class, data.get("billDetails"));
                });
    }

    @Order(5)
    @Test
    public void testGetTransactionByIdFailedInvalidId() throws Exception {
        mockMvc.perform(get("/api/transactions/xxx")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpectAll(status().isNotFound())
                .andDo(result -> {
                    String jsonString = result.getResponse().getContentAsString();
                    Map<String, Object> mapResponse = objectMapper.readValue(jsonString, new TypeReference<>() {
                    });

                    assertNotNull(mapResponse.get("errors"));
                    assertNull(mapResponse.get("data"));
                });
    }

    @Order(6)
    @Test
    public void testGetAllTransaction() throws Exception {
        mockMvc.perform(get("/api/transactions")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                ).andExpectAll(status().isOk())
                .andDo(result -> {
                    String jsonString = result.getResponse().getContentAsString();
                    Map<String, Object> mapResponse = objectMapper.readValue(jsonString, new TypeReference<>() {
                    });

                    assertNull(mapResponse.get("errors"));
                    assertInstanceOf(List.class, mapResponse.get("data"));
                    assertNotNull(mapResponse.get("paging"));
                });
    }

    @Order(7)
    @Test
    public void testGetTotalSales() throws Exception {
        mockMvc.perform(get("/api/transactions/total-sales"))
                .andExpectAll(status().isOk())
                .andDo(result -> {
                    String jsonString = result.getResponse().getContentAsString();
                    Map<String, Object> mapResponse = objectMapper.readValue(jsonString, new TypeReference<>() {
                    });

                    assertNull(mapResponse.get("errors"));
                    assertInstanceOf(Map.class, mapResponse.get("data"));
                });
    }
}