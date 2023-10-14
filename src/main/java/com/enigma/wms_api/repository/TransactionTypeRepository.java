package com.enigma.wms_api.repository;

import com.enigma.wms_api.constant.ETransactionType;
import com.enigma.wms_api.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionTypeRepository extends JpaRepository<TransactionType, String> {
    Optional<TransactionType> findFirstByDescription(ETransactionType description);
}
