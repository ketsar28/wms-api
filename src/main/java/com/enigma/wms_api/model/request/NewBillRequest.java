package com.enigma.wms_api.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewBillRequest {
    private String transactionType;
    private List<NewBillDetailRequest> billDetails;
}
