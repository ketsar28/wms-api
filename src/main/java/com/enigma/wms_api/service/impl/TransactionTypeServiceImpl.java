package com.enigma.wms_api.service.impl;

import com.enigma.wms_api.constant.ETransactionType;
import com.enigma.wms_api.entity.TransactionType;
import com.enigma.wms_api.repository.TransactionTypeRepository;
import com.enigma.wms_api.service.TransactionTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionTypeServiceImpl implements TransactionTypeService {
    private final TransactionTypeRepository transactionTypeRepository;

    @Override
    public TransactionType getOrSave(ETransactionType transactionType) {
        TransactionType type = TransactionType.builder()
                .description(transactionType)
                .build();
        return transactionTypeRepository
                .findFirstByDescription(transactionType)
                .orElseGet(() -> transactionTypeRepository.save(type));
    }
}
