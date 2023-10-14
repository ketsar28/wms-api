package com.enigma.wms_api.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewBillDetailRequest {
    private String productPriceId;
    private Integer quantity;
}
