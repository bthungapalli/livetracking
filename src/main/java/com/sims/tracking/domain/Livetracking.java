package com.sims.tracking.domain;


import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A Livetracking.
 */
@Entity
@Table(name = "tbl_live_track")
public class Livetracking implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "track_id", nullable = false)
    private Integer trackId;

    @NotNull
    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @NotNull
    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "created_time")
    private Instant createdTime;

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

    public Integer getTrackId() {
        return trackId;
    }

    public Livetracking trackId(Integer trackId) {
        this.trackId = trackId;
        return this;
    }

    public void setTrackId(Integer trackId) {
        this.trackId = trackId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Livetracking latitude(Double latitude) {
        this.latitude = latitude;
        return this;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Livetracking longitude(Double longitude) {
        this.longitude = longitude;
        return this;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Instant getCreatedTime() {
        return createdTime;
    }

    public Livetracking createdTime(Instant createdTime) {
        this.createdTime = createdTime;
        return this;
    }

    public void setCreatedTime(Instant createdTime) {
        this.createdTime = createdTime;
    }

    public String getRequestId() {
        return requestId;
    }

    public Livetracking requestId(String requestId) {
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
        Livetracking livetracking = (Livetracking) o;
        if (livetracking.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), livetracking.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Livetracking{" +
            "id=" + getId() +
            ", trackId=" + getTrackId() +
            ", latitude=" + getLatitude() +
            ", longitude=" + getLongitude() +
            ", createdTime='" + getCreatedTime() + "'" +
            ", requestId='" + getRequestId() + "'" +
            "}";
    }
}
