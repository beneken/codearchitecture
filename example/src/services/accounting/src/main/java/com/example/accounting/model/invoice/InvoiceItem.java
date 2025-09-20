package com.example.accounting.model.invoice;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class InvoiceItem {

    @EmbeddedId
    @JsonUnwrapped
    private InvoiceItemId id;

    @Size(max = 255)
    private String description;

    @Positive
    private Long amount;

    @PositiveOrZero
    private Long priceInCents;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("invoice")
    @JoinColumn(name = "invoice_id", nullable = false, updatable = false)
    @JsonIgnore
    private Invoice invoice;

}