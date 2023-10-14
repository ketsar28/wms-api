package com.enigma.wms_api.service;

import com.enigma.wms_api.model.request.NewProductRequest;
import com.enigma.wms_api.model.request.SearchProductRequest;
import com.enigma.wms_api.model.request.UpdateProductRequest;
import com.enigma.wms_api.model.response.ProductResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    ProductResponse create(NewProductRequest request);
    Page<ProductResponse> getAll(SearchProductRequest request);
    List<ProductResponse> getAllByBranchCode(String branchCode);
    ProductResponse update(UpdateProductRequest request);
    void deleteById(String id);

}
