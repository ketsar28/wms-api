package com.enigma.wms_api.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "t_bill")
public class Bill extends BaseEntity {

    @Column(name = "receipt_number", unique = true, nullable = false)
    private String receiptNumber;

    private LocalDateTime transDate;

    @ManyToOne
    @JoinColumn(name = "trans_type_id")
    private TransactionType transactionType;

    @OneToMany(mappedBy = "bill", cascade = {CascadeType.PERSIST})
    @JsonManagedReference
    private List<BillDetail> billDetails;

}
