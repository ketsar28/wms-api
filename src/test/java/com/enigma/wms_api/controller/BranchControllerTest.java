package com.enigma.wms_api.controller;

import com.enigma.wms_api.model.request.NewBranchRequest;
import com.enigma.wms_api.model.response.BranchResponse;
import com.enigma.wms_api.model.response.CommonResponse;
import com.enigma.wms_api.repository.BranchRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.print.attribute.standard.Media;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BranchControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private static String id;
    private static String branchCode;

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
                    assertNull(mapResponse.get("errors"));
                    assertNotNull(data.get("branchId"));
                    assertEquals(request.get("branchName"), data.get("branchName"));
                    assertEquals(request.get("address"), data.get("address"));
                    assertEquals(request.get("phoneNumber"), data.get("phoneNumber"));

                    id = data.get("branchId").toString();
                    branchCode = data.get("branchCode").toString();
                });
    }

    @Order(2)
    @Test
    public void testCreateBranchFailedBadRequest() throws Exception {
        Map<String, Object> request = new HashMap<>();

        mockMvc.perform(post("/api/branch")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                ).andExpectAll(status().isBadRequest())
                .andDo(result -> {
                    String jsonString = result.getResponse().getContentAsString();
                    Map<String, Object> mapResponse = objectMapper.readValue(jsonString, new TypeReference<>() {
                    });
                    Map<String, Object> data = (Map<String, Object>) mapResponse.get("data");
                    assertNotNull(mapResponse.get("errors"));
                    assertNull(data);
                });
    }

    @Order(3)
    @Test
    public void testCreateBranchFailedDataDuplicate() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("branchName", "Cilandak");
        request.put("branchCode", "0205");
        request.put("address", "Jl. Cilandak Raya No.XX");
        request.put("phoneNumber", "08123441234");

        mockMvc.perform(post("/api/branch")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                ).andExpectAll(status().isConflict())
                .andDo(result -> {
                    String jsonString = result.getResponse().getContentAsString();
                    Map<String, Object> mapResponse = objectMapper.readValue(jsonString, new TypeReference<>() {
                    });

                    Map<String, Object> data = (Map<String, Object>) mapResponse.get("data");
                    assertNotNull(mapResponse.get("errors"));
                    assertNull(data);
                });
    }

    @Order(4)
    @Test
    public void testGetAllBranch() throws Exception {
        String page = "0";
        String size = "10";

        mockMvc.perform(get("/api/branch")
                        .param("page", page)
                        .param("size", size)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpectAll(status().isOk())
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

    @Order(5)
    @Test
    public void testGetBranchById() throws Exception {
        mockMvc.perform(get("/api/branch/" + id).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpectAll(status().isOk())
                .andDo(result -> {
                    String jsonString = result.getResponse().getContentAsString();
                    Map<String, Object> mapResponse = objectMapper.readValue(jsonString, new TypeReference<>() {
                    });

                    Map<String, Object> data = (Map<String, Object>) mapResponse.get("data");
                    assertNull(mapResponse.get("errors"));
                    assertNotNull(data.get("branchId"));
                    assertNotNull(data.get("branchName"));
                    assertNotNull(data.get("address"));
                    assertNotNull(data.get("phoneNumber"));
                });
    }

    @Order(5)
    @Test
    public void testUpdateBranch() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("branchId", id);
        request.put("branchName", "Cilandak KKO");
        request.put("branchCode", branchCode);
        request.put("address", "Jl. Cilandak Raya No.XI");
        request.put("phoneNumber", "08123441234");

        mockMvc.perform(put("/api/branch")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(status().isOk())
                .andDo(result -> {
                    String jsonString = result.getResponse().getContentAsString();
                    Map<String, Object> mapResponse = objectMapper.readValue(jsonString, new TypeReference<>() {
                    });

                    Map<String, Object> data = (Map<String, Object>) mapResponse.get("data");
                    assertNull(mapResponse.get("errors"));
                    assertNotNull(data.get("branchId"));
                    assertEquals(request.get("branchName"), data.get("branchName"));
                    assertEquals(request.get("address"), data.get("address"));
                    assertEquals(request.get("phoneNumber"), data.get("phoneNumber"));
                });
    }

    @Order(5)
    @Test
    public void testUpdateBranchFailedNoId() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("branchName", "Cilandak KKO");
        request.put("branchCode", branchCode);
        request.put("address", "Jl. Cilandak Raya No.XI");
        request.put("phoneNumber", "08123441234");

        mockMvc.perform(put("/api/branch")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(status().isBadRequest())
                .andDo(result -> {
                    String jsonString = result.getResponse().getContentAsString();
                    Map<String, Object> mapResponse = objectMapper.readValue(jsonString, new TypeReference<>() {
                    });

                    Map<String, Object> data = (Map<String, Object>) mapResponse.get("data");
                    assertNotNull(mapResponse.get("errors"));
                    assertNull(data);
                });
    }

    @Order(6)
    @Test
    public void testDeleteBranch() throws Exception {
        mockMvc.perform(delete("/api/branch/" + id).accept(MediaType.APPLICATION_JSON_VALUE))
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

    @Order(7)
    @Test
    public void testDeleteBranchFailedNoId() throws Exception {
        mockMvc.perform(delete("/api/branch/" + id).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpectAll(status().isNotFound())
                .andDo(result -> {
                    String jsonString = result.getResponse().getContentAsString();
                    Map<String, Object> mapResponse = objectMapper.readValue(jsonString, new TypeReference<>() {
                    });
                    String data = (String) mapResponse.get("data");
                    assertNotNull(mapResponse.get("errors"));
                    assertNull(data);
                });
    }


}