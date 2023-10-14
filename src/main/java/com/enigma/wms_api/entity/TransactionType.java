package com.enigma.wms_api.entity;

import com.enigma.wms_api.constant.ETransactionType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "m_trans_type")
public class TransactionType extends BaseEntity{
    @Enumerated(EnumType.STRING)
    private ETransactionType description;
}
