package com.enigma.wms_api.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchBranchRequest {
    private String branchCode;
    private String branchName;
    private Integer page;
    private Integer size;
}
