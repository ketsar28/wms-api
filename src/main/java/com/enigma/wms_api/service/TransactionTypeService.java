package com.enigma.wms_api.service;

import com.enigma.wms_api.constant.ETransactionType;
import com.enigma.wms_api.entity.TransactionType;

public interface TransactionTypeService {
    TransactionType getOrSave(ETransactionType transactionType);
}
