package com.enigma.wms_api.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductRequest {
    @NotBlank(message = "product id is required")
    private String productId;
    @NotBlank(message = "product code is required")
    private String productCode;
    @NotBlank(message = "product name is required")
    private String productName;
    @NotNull(message = "price is required")
    @Min(value = 0, message = "price must be greater than or equal to 0")
    private BigDecimal price;
    @NotBlank(message = "branch id is required")
    private String branchId;

}
