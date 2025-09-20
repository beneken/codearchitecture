package com.example.accounting.model.invoice;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.accounting.model.supplier.Supplier;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "invoiceId")
public class Invoice {

    private static final Logger log = LoggerFactory.getLogger(Invoice.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    private Long invoiceId;

    @NotBlank
    private String invoiceNumber;

    @NotNull
    @PastOrPresent
    private LocalDate invoiceDate;

    @NotNull
    @PositiveOrZero
    private Long invoiceTotalSumCent;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate isChecked;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean isValid = false;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supplier_id")
    @JsonIdentityReference(alwaysAsId = true)
    @NotNull
    private Supplier supplier;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @ToString.Exclude
    @NotNull
    private List<InvoiceItem> invoiceItems;

    public String toJsonString() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        try {
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            log.error("Error converting to JSON: {}", this.invoiceNumber, e);
            return null;
        }
    }
}