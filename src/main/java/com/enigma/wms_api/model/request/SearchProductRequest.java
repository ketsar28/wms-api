package com.enigma.wms_api.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchProductRequest {
    private String productCode;
    private String productName;
    private Integer minPrice;
    private Integer maxPrice;
    private Integer page;
    private Integer size;

}
