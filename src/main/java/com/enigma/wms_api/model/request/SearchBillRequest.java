package com.enigma.wms_api.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchBillRequest {
    private String receiptNumber;
    private String startDate;
    private String endDate;
    private String transType;
    private String productName;
    private Integer page;
    private Integer size;
}
