package com.mediconnect.backend.feature.doctorprofile.entity;

import com.mediconnect.backend.feature.doctorembedding.entity.DoctorEmbedding;
import com.mediconnect.backend.feature.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "doctor_profile")
@Getter
@Setter
public class DoctorProfile implements Persistable<Long> {

    public DoctorProfile() {
    }

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Transient
    private boolean isNew = true;

    @Override
    public Long getId() {
        return userId;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    @PostLoad
    @PostPersist
    void markNotNew() {
        this.isNew = false;
    }

    @NotBlank
    @Column(name = "license_number", nullable = false, unique = true)
    private String licenseNumber;

    @NotBlank
    @Column(name = "qualification", nullable = false)
    private String qualification;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @NotNull
    @DecimalMin("0.0")
    @Column(name = "consultation_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal consultationFee;

    @NotBlank
    @Column(name = "verification_status", nullable = false)
    private String verificationStatus;

    @NotBlank
    @Column(name = "timezone", nullable = false)
    private String timezone;

    @OneToOne(mappedBy = "doctorProfile", fetch = FetchType.LAZY)
    private DoctorEmbedding doctorEmbedding;
}

