package com.enigma.wms_api.service;

import com.enigma.wms_api.model.request.NewBillRequest;
import com.enigma.wms_api.model.request.SearchBillRequest;
import com.enigma.wms_api.model.request.SearchSalesHistoryRequest;
import com.enigma.wms_api.model.response.BillResponse;
import com.enigma.wms_api.model.response.TotalSales;
import org.springframework.data.domain.Page;

public interface BillService {
    BillResponse create(NewBillRequest request);
    BillResponse getById(String id);
    Page<BillResponse> getAll(SearchBillRequest request);
    TotalSales getSalesHistory(SearchSalesHistoryRequest request);
}
