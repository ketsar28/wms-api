package com.enigma.wms_api.controller;

import com.enigma.wms_api.model.request.NewBranchRequest;
import com.enigma.wms_api.model.request.SearchBranchRequest;
import com.enigma.wms_api.model.request.UpdateBranchRequest;
import com.enigma.wms_api.model.response.BranchResponse;
import com.enigma.wms_api.model.response.CommonResponse;
import com.enigma.wms_api.model.response.PagingResponse;
import com.enigma.wms_api.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/branch")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> create(@RequestBody NewBranchRequest request) {
        BranchResponse branchResponse = branchService.create(request);
        CommonResponse<?> response = CommonResponse.builder()
                .data(branchResponse)
                .build();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getById(@PathVariable String id) {
        BranchResponse branchResponse = branchService.getById(id);
        CommonResponse<?> response = CommonResponse.builder()
                .data(branchResponse)
                .build();
        return ResponseEntity
                .ok(response);
    }

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getAll(
            @RequestParam(name = "branchName", required = false) String branchName,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size
    ) {
        SearchBranchRequest request = SearchBranchRequest.builder()
                .branchName(branchName)
                .page(page)
                .size(size)
                .build();
        Page<BranchResponse> responsePage = branchService.getAll(request);
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

    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> update(@RequestBody UpdateBranchRequest request) {
        BranchResponse branchResponse = branchService.update(request);
        CommonResponse<?> response = CommonResponse.builder()
                .data(branchResponse)
                .build();
        return ResponseEntity
                .ok(response);
    }

    @DeleteMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> deleteById(@PathVariable String id) {
        branchService.deleteById(id);
        CommonResponse<?> response = CommonResponse.builder()
                .data("OK")
                .build();
        return ResponseEntity
                .ok(response);
    }

}
