package com.enigma.wms_api.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateBranchRequest {
    @NotBlank(message = "branch id is required")
    private String branchId;
    @NotBlank(message = "branch code is required")
    private String branchCode;
    @NotBlank(message = "branch name is required")
    private String branchName;
    @NotBlank(message = "address is required")
    private String address;
    @NotBlank(message = "phone number is required")
    private String phoneNumber;
}
