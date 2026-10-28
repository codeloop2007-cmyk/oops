package com.mediconnect.backend.feature.userlanguage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class UserLanguageId implements Serializable {

    public UserLanguageId() {
    }

    public UserLanguageId(Long userId, String languageCode) {
        this.userId = userId;
        this.languageCode = languageCode;
    }

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "language_code", nullable = false)
    private String languageCode;
}
