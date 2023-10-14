package com.enigma.wms_api.repository;

import com.enigma.wms_api.entity.ProductPrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductPriceRepository extends JpaRepository<ProductPrice, String>, JpaSpecificationExecutor<ProductPrice> {
    Optional<ProductPrice> findFirstByIsActiveTrueAndProduct_IdAndBranch_Id(String productId, String branchId);
    Optional<ProductPrice> findFirstByIsActiveTrueAndProduct_ProductCode(String code);
    List<ProductPrice> findAllByProduct_ProductCode(String code);
}
