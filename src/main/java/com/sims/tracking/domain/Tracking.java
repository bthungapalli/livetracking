package com.sims.tracking.domain;


import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A Tracking.
 */
@Entity
@Table(name = "tbl_track")
public class Tracking implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "mco_id", nullable = false)
    private String mcoId;

    @NotNull
    @Column(name = "customer_id", nullable = false)
    private String customer_id;

    @NotNull
    @Column(name = "clong", nullable = false)
    private Double clong;

    @NotNull
    @Column(name = "clat", nullable = false)
    private Double clat;

    @NotNull
    @Column(name = "mlat", nullable = false)
    private Double mlat;

    @NotNull
    @Column(name = "mlong", nullable = false)
    private Double mlong;

    @NotNull
    @Column(name = "is_live", nullable = false)
    private Boolean is_live;

    @Column(name = "created_at")
    private Instant created_at;

    @Column(name = "cname")
    private String cname;

    @Column(name = "mname")
    private String mname;

    @NotNull
    @Column(name = "request_id", nullable = false)
    private String requestId;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMcoId() {
        return mcoId;
    }

    public Tracking mcoId(String mcoId) {
        this.mcoId = mcoId;
        return this;
    }

    public void setMcoId(String mcoId) {
        this.mcoId = mcoId;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public Tracking customer_id(String customer_id) {
        this.customer_id = customer_id;
        return this;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public Double getClong() {
        return clong;
    }

    public Tracking clong(Double clong) {
        this.clong = clong;
        return this;
    }

    public void setClong(Double clong) {
        this.clong = clong;
    }

    public Double getClat() {
        return clat;
    }

    public Tracking clat(Double clat) {
        this.clat = clat;
        return this;
    }

    public void setClat(Double clat) {
        this.clat = clat;
    }

    public Double getMlat() {
        return mlat;
    }

    public Tracking mlat(Double mlat) {
        this.mlat = mlat;
        return this;
    }

    public void setMlat(Double mlat) {
        this.mlat = mlat;
    }

    public Double getMlong() {
        return mlong;
    }

    public Tracking mlong(Double mlong) {
        this.mlong = mlong;
        return this;
    }

    public void setMlong(Double mlong) {
        this.mlong = mlong;
    }

    public Boolean isIs_live() {
        return is_live;
    }

    public Tracking is_live(Boolean is_live) {
        this.is_live = is_live;
        return this;
    }

    public void setIs_live(Boolean is_live) {
        this.is_live = is_live;
    }

    public Instant getCreated_at() {
        return created_at;
    }

    public Tracking created_at(Instant created_at) {
        this.created_at = created_at;
        return this;
    }

    public void setCreated_at(Instant created_at) {
        this.created_at = created_at;
    }

    public String getCname() {
        return cname;
    }

    public Tracking cname(String cname) {
        this.cname = cname;
        return this;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getMname() {
        return mname;
    }

    public Tracking mname(String mname) {
        this.mname = mname;
        return this;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    public String getRequestId() {
        return requestId;
    }

    public Tracking requestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Tracking tracking = (Tracking) o;
        if (tracking.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), tracking.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Tracking{" +
            "id=" + getId() +
            ", mcoId='" + getMcoId() + "'" +
            ", customer_id='" + getCustomer_id() + "'" +
            ", clong=" + getClong() +
            ", clat=" + getClat() +
            ", mlat=" + getMlat() +
            ", mlong=" + getMlong() +
            ", is_live='" + isIs_live() + "'" +
            ", created_at='" + getCreated_at() + "'" +
            ", cname='" + getCname() + "'" +
            ", mname='" + getMname() + "'" +
            ", requestId='" + getRequestId() + "'" +
            "}";
    }
}
