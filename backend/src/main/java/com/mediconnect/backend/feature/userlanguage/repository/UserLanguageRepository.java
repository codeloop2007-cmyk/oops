package com.mediconnect.backend.feature.userlanguage.repository;

import com.mediconnect.backend.feature.userlanguage.entity.UserLanguage;
import com.mediconnect.backend.feature.userlanguage.entity.UserLanguageId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLanguageRepository extends JpaRepository<UserLanguage, UserLanguageId> {

    List<UserLanguage> findByUser_UserId(Long userId);

    boolean existsById_UserIdAndId_LanguageCode(Long userId, String languageCode);

    void deleteById_UserIdAndId_LanguageCode(Long userId, String languageCode);
}

