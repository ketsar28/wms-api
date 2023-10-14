package com.enigma.wms_api.repository;

import com.enigma.wms_api.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, String>, JpaSpecificationExecutor<Branch> {
    Optional<Branch> findFirstByBranchCode(String code);
}
