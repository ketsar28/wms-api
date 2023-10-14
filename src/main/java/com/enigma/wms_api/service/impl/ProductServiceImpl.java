package com.enigma.wms_api.service.impl;

import com.enigma.wms_api.constant.ResponseMessage;
import com.enigma.wms_api.entity.Branch;
import com.enigma.wms_api.entity.Product;
import com.enigma.wms_api.entity.ProductPrice;
import com.enigma.wms_api.model.request.NewProductRequest;
import com.enigma.wms_api.model.request.SearchProductPriceRequest;
import com.enigma.wms_api.model.request.SearchProductRequest;
import com.enigma.wms_api.model.request.UpdateProductRequest;
import com.enigma.wms_api.model.response.BranchResponse;
import com.enigma.wms_api.model.response.ProductResponse;
import com.enigma.wms_api.repository.ProductRepository;
import com.enigma.wms_api.service.BranchService;
import com.enigma.wms_api.service.ProductPriceService;
import com.enigma.wms_api.service.ProductService;
import com.enigma.wms_api.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final BranchService branchService;
    private final ProductPriceService productPriceService;
    private final ValidationUtil validationUtil;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ProductResponse create(NewProductRequest request) {
        validationUtil.validate(request);
        try {
            Optional<Product> existingProduct = productRepository.findFirstByProductCode(request.getProductCode());

            if (existingProduct.isPresent()) {
                for (ProductPrice price : existingProduct.get().getProductPrices()) {
                    if (price.getBranch().getId().equals(request.getBranchId())) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, ResponseMessage.getDuplicateResourceMessage(Product.class));
                    }
                }

                Branch branch = branchService.get(request.getBranchId());

                ProductPrice productPrice = ProductPrice.builder()
                        .price(request.getPrice())
                        .branch(branch)
                        .isActive(true)
                        .build();

                existingProduct.get().addProductPrice(productPrice);
                productPrice.setProduct(existingProduct.get());

                productRepository.save(existingProduct.get());

                return toProductResponse(existingProduct.get(), productPrice);
            }

            Branch branch = branchService.get(request.getBranchId());

            ProductPrice productPrice = ProductPrice.builder()
                    .price(request.getPrice())
                    .branch(branch)
                    .isActive(true)
                    .build();

            Product product = Product.builder()
                    .productCode(request.getProductCode())
                    .productName(request.getProductName())
                    .productPrices(Collections.singletonList(productPrice))
                    .build();

            productPrice.setProduct(product);

            productRepository.saveAndFlush(product);

            return toProductResponse(product, productPrice);

        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ResponseMessage.getDuplicateResourceMessage(Product.class));
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ProductResponse> getAll(SearchProductRequest request) {
        SearchProductPriceRequest searchProductPriceRequest = SearchProductPriceRequest.builder()
                .productCode(request.getProductCode())
                .productName(request.getProductName())
                .maxPrice(request.getMaxPrice())
                .minPrice(request.getMinPrice())
                .page(request.getPage())
                .size(request.getSize())
                .build();

        Page<ProductPrice> productPrices = productPriceService.getAllActiveProductPrice(searchProductPriceRequest);
        return productPrices.map(productPrice -> toProductResponse(productPrice.getProduct(), productPrice));
    }

    @Override
    public List<ProductResponse> getAllByBranchCode(String branchId) {
        Branch branch = branchService.get(branchId);
        return branch.getProductPrices().stream()
                .map(productPrice -> toProductResponse(productPrice.getProduct(), productPrice))
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ProductResponse update(UpdateProductRequest request) {
        validationUtil.validate(request);
        ProductPrice productPrice = productPriceService.getActivePriceByProductIdAndBranchId(request.getProductId(), request.getBranchId());

        Branch branch = branchService.get(request.getBranchId());
        Product product = productPrice.getProduct();
        product.setProductName(request.getProductName());
        product.setProductCode(request.getProductCode());

        if (!request.getPrice().equals(productPrice.getPrice())) {
            productPrice.setIsActive(false);
            ProductPrice newProductPrice = ProductPrice.builder()
                    .product(product)
                    .price(request.getPrice())
                    .branch(branch)
                    .isActive(true)
                    .build();
            product.getProductPrices().add(newProductPrice);
            productRepository.save(product);
            return toProductResponse(product, newProductPrice);
        }

        return toProductResponse(product, productPrice);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteById(String id) {
        Product product = findByIdOrThrowNotFound(id);
        productRepository.delete(product);
    }

    private Product findByIdOrThrowNotFound(String id) {
        return productRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.getNotFoundResourceMessage(Product.class)));
    }

    private ProductResponse toProductResponse(Product product, ProductPrice productPrice) {
        return ProductResponse.builder()
                .productId(product.getId())
                .productPriceId(productPrice.getId())
                .productCode(product.getProductCode())
                .productName(product.getProductName())
                .price(productPrice.getPrice())
                .branch(BranchResponse.builder()
                        .branchId(productPrice.getBranch().getId())
                        .branchCode(productPrice.getBranch().getBranchCode())
                        .branchName(productPrice.getBranch().getBranchName())
                        .address(productPrice.getBranch().getAddress())
                        .phoneNumber(productPrice.getBranch().getPhoneNumber())
                        .build())
                .build();
    }
}
