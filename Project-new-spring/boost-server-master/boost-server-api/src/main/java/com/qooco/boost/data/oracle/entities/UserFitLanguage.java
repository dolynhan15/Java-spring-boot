package com.qooco.boost.data.oracle.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "USER_FIT_LANGUAGE")
@Setter @Getter
public class UserFitLanguage extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_FIT_LANGUAGE_SEQUENCE")
    @SequenceGenerator(sequenceName = "USER_FIT_LANGUAGE_SEQ", allocationSize = 1, name = "USER_FIT_LANGUAGE_SEQUENCE")
    @Basic(optional = false)
    @Column(name = "USER_FIT_LANGUAGE_ID", nullable = false)
    private Long id;


    @Basic(optional = false)
    @Column(name = "IS_NATIVE", nullable = false)
    private boolean isNative;

    @JoinColumn(name = "LANGUAGE_ID", referencedColumnName = "LANGUAGE_ID")
    @ManyToOne(optional = false)
    private Language language;

    @JoinColumn(name = "USER_PROFILE_ID", referencedColumnName = "USER_PROFILE_ID")
    @ManyToOne(optional = false)
    private UserFit userFit;

    public UserFitLanguage() {
        super();
    }

    public UserFitLanguage(Long id) {
        this.id = id;
    }

    public UserFitLanguage(Boolean isNative, Language language, UserFit userFit) {
        super(userFit.getUserProfileId());
        this.isNative = isNative;
        this.language = language;
        this.userFit = userFit;
    }

    public UserFitLanguage(Boolean isNative, Language language, UserFit userFit, Long ownerId) {
        super(ownerId);
        this.isNative = isNative;
        this.language = language;
        this.userFit = userFit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserFitLanguage that = (UserFitLanguage) o;
        return isNative == that.isNative &&
                Objects.equals(language, that.language) &&
                Objects.equals(userFit, that.userFit);
    }

    @Override
    public int hashCode() {

        return Objects.hash(isNative, language, userFit);
    }

    @Override
    public String toString() {
        return userFit.toString() + ": " + language.toString();
    }

}