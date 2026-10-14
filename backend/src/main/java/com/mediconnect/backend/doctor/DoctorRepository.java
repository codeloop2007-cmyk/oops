package com.mediconnect.backend.doctor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    /**
     * Semantic (vector) search using pgvector cosine distance operator <=>.
     * :queryVector must be the string form "[0.1,0.2,...]" — cast to vector at query time.
     */
    @Query(value = """
            SELECT * FROM doctors
            WHERE embedding IS NOT NULL
            ORDER BY embedding <=> CAST(:queryVector AS vector)
            LIMIT :limit
            """, nativeQuery = true)
    List<Doctor> semanticSearch(@Param("queryVector") String queryVector,
                                @Param("limit") int limit);
}