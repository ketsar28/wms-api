package com.enigma.wms_api.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "m_product")
public class Product extends BaseEntity {
    @Column(name = "product_code", unique = true)
    private String productCode;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private List<ProductPrice> productPrices;

    public void addProductPrice(ProductPrice productPrice) {
        productPrices.add(productPrice);
    }

}
