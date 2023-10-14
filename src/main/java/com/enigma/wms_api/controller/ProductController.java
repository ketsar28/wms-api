package com.enigma.wms_api.controller;

import com.enigma.wms_api.model.request.NewProductRequest;
import com.enigma.wms_api.model.request.SearchProductRequest;
import com.enigma.wms_api.model.request.UpdateProductRequest;
import com.enigma.wms_api.model.response.CommonResponse;
import com.enigma.wms_api.model.response.PagingResponse;
import com.enigma.wms_api.model.response.ProductResponse;
import com.enigma.wms_api.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.model.PreparableMutationOperation;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> create(@RequestBody NewProductRequest request) {
        ProductResponse productResponse = productService.create(request);

        CommonResponse<?> response = CommonResponse.builder().data(productResponse).build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getAll(
            @RequestParam(name = "productCode", required = false) String productCode,
            @RequestParam(name = "productName", required = false) String productName,
            @RequestParam(name = "minPrice", required = false) Integer minPrice,
            @RequestParam(name = "maxPrice", required = false) Integer maxPrice,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size
    ) {
        SearchProductRequest request = SearchProductRequest.builder()
                .productCode(productCode)
                .productName(productName)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .page(page)
                .size(size)
                .build();
        Page<ProductResponse> responsePage = productService.getAll(request);
        CommonResponse<?> response = CommonResponse.builder()
                .data(responsePage.getContent())
                .paging(PagingResponse.builder()
                        .count(responsePage.getTotalElements())
                        .totalPage(responsePage.getTotalPages())
                        .page(page)
                        .size(size)
                        .build())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping(
            path = "/{branchId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getByBranchId(@PathVariable String branchId) {
        List<ProductResponse> productResponses = productService.getAllByBranchCode(branchId);
        CommonResponse<?> response = CommonResponse.builder()
                .data(productResponses)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> update(@RequestBody UpdateProductRequest request) {
        ProductResponse productResponse = productService.update(request);
        CommonResponse<?> response = CommonResponse.builder()
                .data(productResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> delete(@PathVariable String id) {
        productService.deleteById(id);
        CommonResponse<?> response = CommonResponse.builder()
                .data("OK")
                .build();
        return ResponseEntity.ok(response);
    }

}
