package com.example.accounting.model.supplier;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.example.accounting.model.invoice.Invoice;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "customerId")
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long supplierId;

    @NotBlank
    private String companyName;

    @NotBlank
    private String addressStreet;

    @NotBlank
    private String addressHouseNo;

    @NotBlank
    @Pattern(regexp = "\\d{5}", message = "Postcode in Germany must have 5 digigs")
    private String postcode;

    @NotBlank
    private String city;

    @Pattern(regexp = "^$|^\\+49\\s\\d{2,5}\\s\\d{6,12}$", message = "Ungültige Telefonnummer")
    private String phone;

    @Email
    private String mail;

    @JsonIgnore
    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Invoice> invoices;
}