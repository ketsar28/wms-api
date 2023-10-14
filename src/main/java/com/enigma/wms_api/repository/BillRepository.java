package com.enigma.wms_api.repository;

import com.enigma.wms_api.constant.ETransactionType;
import com.enigma.wms_api.entity.Bill;
import com.enigma.wms_api.entity.TransactionType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface BillRepository extends JpaRepository<Bill, String>, JpaSpecificationExecutor<Bill> {
    @Query("SELECT SUM(pp.price * bd.quantity) FROM Bill b " +
            "JOIN b.billDetails bd JOIN bd.productPrice pp " +
            "WHERE b.transactionType.description = :transType or b.transDate between :startDate and :endDate " +
            "GROUP BY b.transactionType")
    BigDecimal calculateBillGroupByTransType(@Param("transType") ETransactionType transactionType,
                                             @Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);


}
