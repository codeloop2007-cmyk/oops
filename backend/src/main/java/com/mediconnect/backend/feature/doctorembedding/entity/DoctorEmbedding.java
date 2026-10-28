package com.mediconnect.backend.feature.doctorembedding.entity;

import com.mediconnect.backend.feature.doctorembedding.converter.PGvectorUserType;
import com.mediconnect.backend.feature.doctorprofile.entity.DoctorProfile;
import com.pgvector.PGvector;
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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "doctor_embedding")
@Getter
@Setter
public class DoctorEmbedding implements Persistable<Long> {

    public DoctorEmbedding() {
    }

    @Id
    @Column(name = "doctor_user_id")
    private Long doctorUserId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "doctor_user_id")
    private DoctorProfile doctorProfile;

    @Transient
    private boolean isNew = true;

    @Override
    public Long getId() {
        return doctorUserId;
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

    @NotNull
    @Column(name = "embedding", columnDefinition = "vector(384)", nullable = false)
    @Type(PGvectorUserType.class)
    private PGvector embedding;

    @NotNull
    @Column(name = "is_up_to_date", nullable = false)
    private Boolean isUpToDate;

    @NotNull
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private Instant updatedAt;

    @NotBlank
    @Column(name = "embedded_model", nullable = false)
    private String embeddedModel;

    @NotBlank
    @Column(name = "source_hash", nullable = false)
    private String sourceHash;
}

