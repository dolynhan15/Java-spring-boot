package com.qooco.boost.data.oracle.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Getter @Setter @NoArgsConstructor
@Table(name = "USER_WALLET")
public class UserWallet extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "USER_PROFILE_ID")
    private Long owner;

    @Basic(optional = false)
    @NotNull
    @Column(name = "BOOST_COINS")
    private int boostCoins;

    public UserWallet(Long owner) {
        super(owner);
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserWallet that = (UserWallet) o;
        return Objects.equals(owner, that.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner);
    }

    @Override
    public String toString() {
        return Integer.toString(boostCoins);
    }
}