package com.mediconnect.backend.feature.user.valueobject;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@Schema(description = "User physical address value object embedded in User")
public class Address {

    public Address() {
    }

    public Address(String label, String addressLine1, String addressLine2,
                   String city, String state, String postalCode, String country,
                   BigDecimal latitude, BigDecimal longitude, Boolean isDefault) {
        this.label = label;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isDefault = isDefault;
    }

    @Schema(description = "Address label (e.g. Home, Work, Clinic)", example = "Home")
    @Column(name = "label")
    private String label;

    @NotBlank(message = "Address line 1 is required")
    @Schema(description = "Street address line 1", example = "123 Main St", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "address_line1")
    private String addressLine1;

    @Schema(description = "Street address line 2", example = "Apt 4B")
    @Column(name = "address_line2")
    private String addressLine2;

    @NotBlank(message = "City is required")
    @Schema(description = "City name", example = "Springfield", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "city")
    private String city;

    @NotBlank(message = "State is required")
    @Schema(description = "State or province", example = "IL", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "state")
    private String state;

    @NotBlank(message = "Postal code is required")
    @Schema(description = "Postal or ZIP code", example = "62701", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "postal_code")
    private String postalCode;

    @NotBlank(message = "Country is required")
    @Schema(description = "Country name", example = "USA", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "country")
    private String country;

    @DecimalMin(value = "-90.0", message = "Latitude must be greater than or equal to -90.0")
    @DecimalMax(value = "90.0", message = "Latitude must be less than or equal to 90.0")
    @Schema(description = "Geographic latitude in decimal degrees", example = "39.7817", minimum = "-90.0", maximum = "90.0")
    @Column(name = "latitude", precision = 9, scale = 6)
    private BigDecimal latitude;

    @DecimalMin(value = "-180.0", message = "Longitude must be greater than or equal to -180.0")
    @DecimalMax(value = "180.0", message = "Longitude must be less than or equal to 180.0")
    @Schema(description = "Geographic longitude in decimal degrees", example = "-89.6501", minimum = "-180.0", maximum = "180.0")
    @Column(name = "longitude", precision = 9, scale = 6)
    private BigDecimal longitude;

    @Schema(description = "Whether this is the user's default address", example = "true")
    @Column(name = "is_default")
    private Boolean isDefault;
}

