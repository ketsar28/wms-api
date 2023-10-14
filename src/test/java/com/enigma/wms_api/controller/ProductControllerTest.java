package com.enigma.wms_api.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private static String branchId;
    private static String productId;
    private static String productCode;

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
                    Map<String, Object> branch = (Map<String, Object>) data.get("branch");

                    assertNull(mapResponse.get("errors"));
                    assertNotNull(data.get("productPriceId"));
                    assertEquals(request.get("productCode"), data.get("productCode"));
                    assertEquals(request.get("productName"), data.get("productName"));
                    assertEquals(request.get("price"), data.get("price"));
                    assertInstanceOf(Map.class, branch);
                    assertEquals(request.get("branchId"), branch.get("branchId"));

                    productId = data.get("productId").toString();
                    productCode = data.get("productCode").toString();
                });
    }

    @Order(3)
    @Test
    public void testGetAllProduct() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                ).andExpectAll(status().isOk())
                .andDo(result -> {
                    String jsonString = result.getResponse().getContentAsString();
                    Map<String, Object> mapResponse = objectMapper.readValue(jsonString, new TypeReference<>() {
                    });
                    List<Object> data = (List<Object>) mapResponse.get("data");
                    assertNull(mapResponse.get("errors"));
                    assertInstanceOf(List.class, data);
                    assertNotNull(mapResponse.get("paging"));
                });
    }

    @Order(4)
    @Test
    public void testGetAllProductByBranchId() throws Exception {
        mockMvc.perform(get("/api/products/" + branchId)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                ).andExpectAll(status().isOk())
                .andDo(result -> {
                    String jsonString = result.getResponse().getContentAsString();
                    Map<String, Object> mapResponse = objectMapper.readValue(jsonString, new TypeReference<>() {
                    });
                    List<Object> data = (List<Object>) mapResponse.get("data");
                    assertNull(mapResponse.get("errors"));
                    assertInstanceOf(List.class, data);
                });
    }

    @Order(5)
    @Test
    public void testGetAllProductByBranchIdFailedInvalidBranchId() throws Exception {
        mockMvc.perform(get("/api/products/xxx")
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
    public void testUpdateProduct() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("productId", productId);
        request.put("productCode", productCode);
        request.put("productName", "Nasi Merah");
        request.put("price", 7000.00);
        request.put("branchId", branchId);

        mockMvc.perform(put("/api/products")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(status().isOk())
                .andDo(result -> {
                    String jsonString = result.getResponse().getContentAsString();
                    Map<String, Object> mapResponse = objectMapper.readValue(jsonString, new TypeReference<>() {
                    });
                    Map<String, Object> data = (Map<String, Object>) mapResponse.get("data");
                    assertEquals(request.get("productName"), data.get("productName"));
                    assertEquals(request.get("price"), data.get("price"));
                    assertNull(mapResponse.get("errors"));
                });
    }

    @Order(7)
    @Test
    public void testUpdateProductFailedInvalidId() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("productId", "xxx");
        request.put("productCode", productCode);
        request.put("productName", "Nasi Merah");
        request.put("price", 7000);
        request.put("branchId", branchId);

        mockMvc.perform(put("/api/products")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(status().isNotFound())
                .andDo(result -> {
                    String jsonString = result.getResponse().getContentAsString();
                    Map<String, Object> mapResponse = objectMapper.readValue(jsonString, new TypeReference<>() {
                    });
                    assertNotNull(mapResponse.get("errors"));
                    assertNull(mapResponse.get("data"));
                });
    }

    @Order(8)
    @Test
    public void testDeleteById() throws Exception {
        mockMvc.perform(delete("/api/products/" + productId)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpectAll(status().isOk())
                .andDo(result -> {
                    String jsonString = result.getResponse().getContentAsString();
                    Map<String, Object> mapResponse = objectMapper.readValue(jsonString, new TypeReference<>() {
                    });
                    assertNull(mapResponse.get("errors"));
                    assertEquals("OK", mapResponse.get("data"));
                });
    }

    @Order(9)
    @Test
    public void testDeleteByIdFailedNoId() throws Exception {
        mockMvc.perform(delete("/api/products/" + productId)
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

    @Order(10)
    @Test
    public void testDeleteBranch() throws Exception {
        mockMvc.perform(delete("/api/branch/" + branchId).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpectAll(status().isOk())
                .andDo(result -> {
                    String jsonString = result.getResponse().getContentAsString();
                    Map<String, Object> mapResponse = objectMapper.readValue(jsonString, new TypeReference<>() {
                    });
                    String data = (String) mapResponse.get("data");
                    assertNull(mapResponse.get("errors"));
                    assertEquals("OK", data);
                });
    }

}