package com.enigma.wms_api.repository;

import com.enigma.wms_api.entity.ReceiptSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReceiptSequenceRepository extends JpaRepository<ReceiptSequence, String> {
    Optional<ReceiptSequence> findFirstByBranchCodeAndYear(String branchCode, String year);
}
