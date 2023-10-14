package com.enigma.wms_api.controller;

import com.enigma.wms_api.model.request.NewBillRequest;
import com.enigma.wms_api.model.request.SearchBillRequest;
import com.enigma.wms_api.model.request.SearchSalesHistoryRequest;
import com.enigma.wms_api.model.response.BillResponse;
import com.enigma.wms_api.model.response.CommonResponse;
import com.enigma.wms_api.model.response.PagingResponse;
import com.enigma.wms_api.model.response.TotalSales;
import com.enigma.wms_api.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final BillService billService;

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> create(@RequestBody NewBillRequest request) {
        BillResponse billResponse = billService.create(request);
        CommonResponse<?> response = CommonResponse.builder()
                .data(billResponse)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getById(@PathVariable String id) {
        BillResponse billResponse = billService.getById(id);
        CommonResponse<?> response = CommonResponse.builder()
                .data(billResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getAll(
            @RequestParam(name = "productName", required = false) String productName,
            @RequestParam(name = "transType", required = false) String transType,
            @RequestParam(name = "receiptNumber", required = false) String receiptNumber,
            @RequestParam(name = "startDate", required = false) String startDate,
            @RequestParam(name = "endDate", required = false) String endDate,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size
    ) {
        SearchBillRequest request = SearchBillRequest.builder()
                .productName(productName)
                .transType(transType)
                .receiptNumber(receiptNumber)
                .startDate(startDate)
                .endDate(endDate)
                .page(page)
                .size(size)
                .build();
        Page<BillResponse> billResponses = billService.getAll(request);
        PagingResponse pagingResponse = PagingResponse.builder()
                .count(billResponses.getTotalElements())
                .totalPage(billResponses.getTotalPages())
                .page(page)
                .size(size)
                .build();
        CommonResponse<?> response = CommonResponse.builder()
                .data(billResponses.getContent())
                .paging(pagingResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping(
            path = "/total-sales",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getTotalSales(
            @RequestParam(name = "startDate", required = false) String startDate,
            @RequestParam(name = "endDate", required = false) String endDate
    ) {
        SearchSalesHistoryRequest request = SearchSalesHistoryRequest.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();
        TotalSales totalSales = billService.getSalesHistory(request);
        CommonResponse<?> response = CommonResponse.builder()
                .data(totalSales)
                .build();
        return ResponseEntity.ok(response);
    }

}
