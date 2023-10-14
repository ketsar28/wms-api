package com.enigma.wms_api.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillResponse {
    private String billId;
    private String receiptNumber;
    private String transDate;
    private String transactionType;
    private List<BillDetailResponse> billDetails;
}
