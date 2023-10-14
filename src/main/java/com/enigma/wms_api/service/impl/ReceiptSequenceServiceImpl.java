package com.enigma.wms_api.service.impl;

import com.enigma.wms_api.entity.ReceiptSequence;
import com.enigma.wms_api.model.request.NewReceiptSequenceRequest;
import com.enigma.wms_api.repository.ReceiptSequenceRepository;
import com.enigma.wms_api.service.ReceiptSequenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReceiptSequenceServiceImpl implements ReceiptSequenceService {

    private final ReceiptSequenceRepository receiptSequenceRepository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String generateReceipt(NewReceiptSequenceRequest request) {
        Optional<ReceiptSequence> optionalReceiptSequence = receiptSequenceRepository.findFirstByBranchCodeAndYear(request.getBranchCode(), request.getYear());

        if (optionalReceiptSequence.isEmpty()) {
            ReceiptSequence newReceiptSequence = ReceiptSequence.builder()
                    .branchCode(request.getBranchCode())
                    .year(request.getYear())
                    .sequence(1L)
                    .build();
            receiptSequenceRepository.saveAndFlush(newReceiptSequence);
            return String.format("%s-%s-000%d", newReceiptSequence.getBranchCode(), newReceiptSequence.getYear(), newReceiptSequence.getSequence());
        }

        ReceiptSequence receiptSequence = optionalReceiptSequence.get();
        receiptSequence.setSequence(receiptSequence.getSequence() + 1L);

        if (receiptSequence.getSequence() < 10 && receiptSequence.getSequence() > 0) {
            return String.format("%s-%s-000%d", receiptSequence.getBranchCode(), receiptSequence.getYear(), receiptSequence.getSequence());
        } else if (receiptSequence.getSequence() < 100) {
            return String.format("%s-%s-00%d", receiptSequence.getBranchCode(), receiptSequence.getYear(), receiptSequence.getSequence());
        } else if (receiptSequence.getSequence() < 1000) {
            return String.format("%s-%s-00%d", receiptSequence.getBranchCode(), receiptSequence.getYear(), receiptSequence.getSequence());
        } else {
            return String.format("%s-%s-00%d", receiptSequence.getBranchCode(), receiptSequence.getYear(), receiptSequence.getSequence());
        }
    }
}
