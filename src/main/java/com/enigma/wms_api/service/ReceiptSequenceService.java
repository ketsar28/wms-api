package com.enigma.wms_api.service;

import com.enigma.wms_api.model.request.NewReceiptSequenceRequest;

public interface ReceiptSequenceService {
    String generateReceipt(NewReceiptSequenceRequest request);
}
