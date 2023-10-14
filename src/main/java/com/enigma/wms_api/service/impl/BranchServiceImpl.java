package com.enigma.wms_api.service.impl;

import com.enigma.wms_api.constant.ResponseMessage;
import com.enigma.wms_api.entity.Branch;
import com.enigma.wms_api.model.request.NewBranchRequest;
import com.enigma.wms_api.model.request.SearchBranchRequest;
import com.enigma.wms_api.model.request.UpdateBranchRequest;
import com.enigma.wms_api.model.response.BranchResponse;
import com.enigma.wms_api.repository.BranchRepository;
import com.enigma.wms_api.service.BranchService;
import com.enigma.wms_api.util.ValidationUtil;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final ValidationUtil validationUtil;

    @Transactional(readOnly = true)
    @Override
    public Branch get(String id) {
        return branchRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ResponseMessage.getNotFoundResourceMessage(Branch.class)));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public BranchResponse create(NewBranchRequest request) {
        validationUtil.validate(request);
        try {
            Branch branch = Branch.builder()
                    .branchCode(request.getBranchCode())
                    .branchName(request.getBranchName())
                    .address(request.getAddress())
                    .phoneNumber(request.getPhoneNumber())
                    .build();
            branchRepository.saveAndFlush(branch);
            return toBranchResponse(branch);
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ResponseMessage.getDuplicateResourceMessage(Branch.class));
        }
    }

    @Transactional(readOnly = true)
    @Override
    public BranchResponse getById(String id) {
        return toBranchResponse(branchRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.getNotFoundResourceMessage(Branch.class))));
    }

    @Override
    public BranchResponse getByCode(String code) {
        return toBranchResponse(branchRepository.findFirstByBranchCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.getNotFoundResourceMessage(Branch.class))));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<BranchResponse> getAll(SearchBranchRequest request) {
        Specification<Branch> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (Objects.nonNull(request.getBranchName())) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("branchName")), "%" + request.getBranchName() + "%"));
            }

            if (Objects.nonNull(request.getBranchCode())) {
                predicates.add(criteriaBuilder.equal(root.get("branchCode"), request.getBranchCode()));
            }

            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return branchRepository.findAll(specification, pageable).map(this::toBranchResponse);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public BranchResponse update(UpdateBranchRequest request) {
        validationUtil.validate(request);
        Branch branch = get(request.getBranchId());
        branch.setBranchCode(request.getBranchCode());
        branch.setBranchName(request.getBranchName());
        branch.setAddress(request.getAddress());
        branch.setPhoneNumber(request.getPhoneNumber());
        return toBranchResponse(branchRepository.save(branch));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteById(String id) {
        Branch branch = get(id);
        branchRepository.delete(branch);
    }

    private BranchResponse toBranchResponse(Branch branch) {
        return BranchResponse.builder()
                .branchId(branch.getId())
                .branchCode(branch.getBranchCode())
                .branchName(branch.getBranchName())
                .address(branch.getAddress())
                .phoneNumber(branch.getPhoneNumber())
                .build();
    }
}
