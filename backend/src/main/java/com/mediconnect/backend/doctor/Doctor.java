package com.mediconnect.backend.doctor;

import com.mediconnect.backend.vector.VectorType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;

@Entity
@Table(name = "doctors")
@Data
@NoArgsConstructor
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doctor_id")
    private Long doctorId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String specialization;

    @Column(length = 150)
    private String qualification;

    @Column(length = 100)
    private String language;

    @Column(length = 150)
    private String location;

    @Column(name = "consultation_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal consultationFee;

    /** 384-dim vector — matches the sentence-transformers MiniLM default. */
    @Type(VectorType.class)
    @Column(name = "embedding", columnDefinition = "vector(384)")
    private float[] embedding;
}