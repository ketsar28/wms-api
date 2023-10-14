package com.enigma.wms_api.service.impl;

import com.enigma.wms_api.constant.ResponseMessage;
import com.enigma.wms_api.entity.Product;
import com.enigma.wms_api.entity.ProductPrice;
import com.enigma.wms_api.model.request.SearchProductPriceRequest;
import com.enigma.wms_api.repository.ProductPriceRepository;
import com.enigma.wms_api.service.ProductPriceService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductPriceServiceImpl implements ProductPriceService {

    private final ProductPriceRepository productPriceRepository;

    @Override
    public List<ProductPrice> getAllProductPriceByProductCode(String code) {
        return productPriceRepository.findAllByProduct_ProductCode(code);
    }

    @Override
    public ProductPrice getActivePriceByProductIdAndBranchId(String productId, String branchId) {
        return productPriceRepository.findFirstByIsActiveTrueAndProduct_IdAndBranch_Id(productId, branchId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                                ResponseMessage.getNotFoundResourceMessage(ProductPrice.class)));
    }

    @Override
    public ProductPrice getById(String id) {
        return productPriceRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ResponseMessage.getNotFoundResourceMessage(ProductPrice.class)));
    }


    @Override
    public ProductPrice getActivePriceByProductCode(String productCode) {
        return productPriceRepository.findFirstByIsActiveTrueAndProduct_ProductCode(productCode)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                                ResponseMessage.getNotFoundResourceMessage(ProductPrice.class)));
    }

    @Override
    public Page<ProductPrice> getAllActiveProductPrice(SearchProductPriceRequest request) {
        Specification<ProductPrice> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (Objects.nonNull(request.getProductCode())) {
                predicates.add(criteriaBuilder.equal(root.join("product").get("productCode"), request.getProductCode()));
            }

            if (Objects.nonNull(request.getProductName())) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.join("product").get("productName")), "%" + request.getProductName() + "%"));
            }

            if (Objects.nonNull(request.getMinPrice())) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), request.getMinPrice()));
            }

            if (Objects.nonNull(request.getMaxPrice())) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), request.getMaxPrice()));
            }

            Predicate isActive = criteriaBuilder.equal(root.get("isActive"), true);
            predicates.add(isActive);

            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        return productPriceRepository.findAll(specification, pageable);
//        return productPriceRepository.findAllByIsActiveTrue(specification, pageable);
    }
}
