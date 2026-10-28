package com.mediconnect.backend.feature.prescriptionitem.repository;

import com.mediconnect.backend.feature.prescriptionitem.entity.PrescriptionItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrescriptionItemRepository extends JpaRepository<PrescriptionItem, Long> {

    List<PrescriptionItem> findByPrescription_PrescriptionId(Long prescriptionId);
}

