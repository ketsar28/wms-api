package com.enigma.wms_api.service;

import com.enigma.wms_api.entity.Branch;
import com.enigma.wms_api.model.request.NewBranchRequest;
import com.enigma.wms_api.model.request.SearchBranchRequest;
import com.enigma.wms_api.model.request.UpdateBranchRequest;
import com.enigma.wms_api.model.response.BranchResponse;
import org.springframework.data.domain.Page;

public interface BranchService {
    Branch get(String id);

    BranchResponse create(NewBranchRequest request);
    BranchResponse getById(String id);
    BranchResponse getByCode(String code);
    Page<BranchResponse> getAll(SearchBranchRequest request);
    BranchResponse update(UpdateBranchRequest request);
    void deleteById(String id);
}
