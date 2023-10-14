package com.enigma.wms_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "m_receipt_seq")
public class ReceiptSequence {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String branchCode;
    private String year;
    private Long sequence;
}
