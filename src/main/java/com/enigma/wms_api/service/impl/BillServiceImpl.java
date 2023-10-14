package com.enigma.wms_api.service.impl;

import com.enigma.wms_api.constant.ETransactionType;
import com.enigma.wms_api.constant.ResponseMessage;
import com.enigma.wms_api.entity.*;
import com.enigma.wms_api.model.request.*;
import com.enigma.wms_api.model.response.*;
import com.enigma.wms_api.repository.BillRepository;
import com.enigma.wms_api.service.BillService;
import com.enigma.wms_api.service.ProductPriceService;
import com.enigma.wms_api.service.ReceiptSequenceService;
import com.enigma.wms_api.service.TransactionTypeService;
import com.enigma.wms_api.util.WMSUtil;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService {
    private final BillRepository billRepository;
    private final ReceiptSequenceService receiptSequenceService;
    private final ProductPriceService productPriceService;
    private final TransactionTypeService transactionTypeService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public BillResponse create(NewBillRequest request) {
        List<BillDetail> billDetails = new ArrayList<>();

        String branchCode = null;
        for (NewBillDetailRequest newBillDetailRequest : request.getBillDetails()) {
            ProductPrice productPrice = productPriceService.getById(newBillDetailRequest.getProductPriceId());

            if (Objects.nonNull(branchCode) && !Objects.equals(productPrice.getBranch().getBranchCode(), branchCode)) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Can't Order with different branch");
            }

            branchCode = productPrice.getBranch().getBranchCode();

            billDetails.add(BillDetail.builder()
                    .productPrice(productPrice)
                    .quantity(newBillDetailRequest.getQuantity())
                    .build());
        }

        ETransactionType eType = ETransactionType.getTypeNumber(request.getTransactionType());
        TransactionType transactionType = transactionTypeService.getOrSave(eType);

        NewReceiptSequenceRequest receiptSequence = NewReceiptSequenceRequest.builder()
                .branchCode(branchCode)
                .year(String.valueOf(LocalDateTime.now().getYear()))
                .build();
        String receipt = receiptSequenceService.generateReceipt(receiptSequence);

        Bill bill = Bill.builder()
                .receiptNumber(receipt)
                .transactionType(transactionType)
                .transDate(LocalDateTime.now())
                .billDetails(billDetails)
                .build();
        billRepository.saveAndFlush(bill);

        for (BillDetail billDetail : billDetails) {
            billDetail.setBill(bill);
        }

        return toBillResponse(bill);
    }

    @Override
    public BillResponse getById(String id) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.getNotFoundResourceMessage(Bill.class)));
        return toBillResponse(bill);
    }

    @Override
    public Page<BillResponse> getAll(SearchBillRequest request) {
        Specification<Bill> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (Objects.nonNull(request.getReceiptNumber())) {
                predicates.add(criteriaBuilder.equal(root.get("receiptNumber"), request.getReceiptNumber()));
            }

            if (Objects.nonNull(request.getTransType())) {
                predicates.add(criteriaBuilder.equal(root.join("transactionType").get("description"), ETransactionType.getType(request.getTransType())));
            }

            if (Objects.nonNull(request.getProductName())) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.join("billDetails").join("productPrice").join("product").get("productName")), request.getProductName().toLowerCase()));
            }

            if (Objects.nonNull(request.getStartDate()) && Objects.nonNull(request.getEndDate())) {
                LocalDateTime startDate = WMSUtil.parseLocalDateTime(request.getStartDate());
                LocalDateTime endDate = WMSUtil.parseLocalDateTime(request.getEndDate());
                predicates.add(criteriaBuilder.between(root.get("transDate"), startDate, endDate));
            }

            if (Objects.nonNull(request.getStartDate()) && Objects.isNull(request.getEndDate())) {
                LocalDateTime startDate = WMSUtil.parseLocalDateTime(request.getStartDate());
                predicates.add(criteriaBuilder.between(root.get("transDate"), startDate, LocalDateTime.now()));
            }

            if (Objects.nonNull(request.getEndDate()) && Objects.isNull(request.getStartDate())) {
                LocalDateTime endDate = WMSUtil.parseLocalDateTime(request.getEndDate());
                predicates.add(criteriaBuilder.between(root.get("transDate"), LocalDateTime.now(), endDate));
            }

            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        Page<Bill> bills = billRepository.findAll(specification, pageable);
        return bills.map(BillServiceImpl::toBillResponse);
    }

    @Override
    public TotalSales getSalesHistory(SearchSalesHistoryRequest request) {
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        if (Objects.nonNull(request.getStartDate())) {
            startDate = WMSUtil.parseLocalDateTime(request.getStartDate());
        }

        if (Objects.nonNull(request.getStartDate())) {
            endDate = WMSUtil.parseLocalDateTime(request.getEndDate());
        }

        BigDecimal eatIn = billRepository.calculateBillGroupByTransType(ETransactionType.EAT_IN, Objects.nonNull(startDate) ? startDate : LocalDateTime.now(), Objects.nonNull(endDate) ? endDate : LocalDateTime.now());
        BigDecimal online = billRepository.calculateBillGroupByTransType(ETransactionType.ONLINE, Objects.nonNull(startDate) ? startDate : LocalDateTime.now(), Objects.nonNull(endDate) ? endDate : LocalDateTime.now());
        BigDecimal takeAway = billRepository.calculateBillGroupByTransType(ETransactionType.TAKE_AWAY, Objects.nonNull(startDate) ? startDate : LocalDateTime.now(), Objects.nonNull(endDate) ? endDate : LocalDateTime.now());

        return TotalSales.builder()
                .eatIn(eatIn)
                .online(online)
                .takeAway(takeAway)
                .build();
    }

    private static BillResponse toBillResponse(Bill bill) {
        List<BillDetailResponse> billDetailResponses = bill.getBillDetails().stream().map(billDetail -> {
            ProductPrice productPrice = billDetail.getProductPrice();

            Branch branch = productPrice.getBranch();
            BranchResponse branchResponse = BranchResponse.builder()
                    .branchId(branch.getId())
                    .branchCode(branch.getBranchCode())
                    .branchName(branch.getBranchName())
                    .address(branch.getAddress())
                    .phoneNumber(branch.getPhoneNumber())
                    .build();

            ProductResponse productResponse = ProductResponse.builder()
                    .productId(productPrice.getProduct().getId())
                    .productPriceId(productPrice.getId())
                    .productCode(productPrice.getProduct().getProductCode())
                    .productName(productPrice.getProduct().getProductName())
                    .price(productPrice.getPrice())
                    .branch(branchResponse)
                    .build();

            return BillDetailResponse.builder()
                    .billDetailId(billDetail.getId())
                    .billId(bill.getId())
                    .product(productResponse)
                    .quantity(billDetail.getQuantity())
                    .totalSales(new BigDecimal(billDetail.getQuantity()).multiply(productPrice.getPrice()))
                    .build();
        }).toList();

        return BillResponse.builder()
                .billId(bill.getId())
                .receiptNumber(bill.getReceiptNumber())
                .transDate(bill.getTransDate().toString())
                .transactionType(bill.getTransactionType().getDescription().name())
                .billDetails(billDetailResponses)
                .build();
    }
}
