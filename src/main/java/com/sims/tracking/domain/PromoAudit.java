package com.sims.tracking.domain;


import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A PromoAudit.
 */
@Entity
@Table(name = "tbl_promo_audit")
public class PromoAudit implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "promotion", nullable = false)
    private String promotion;

    @NotNull
    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "created_at")
    private Instant createdAt;

    @NotNull
    @Column(name = "status", nullable = false)
    private Boolean status;

    @NotNull
    @Column(name = "promotion_id", nullable = false)
    private Long promotionId;

    @NotNull
    @Column(name = "updated_value", nullable = false)
    private String updatedValue;

    @NotNull
    @Column(name = "updated_status", nullable = false)
    private Boolean updatedStatus;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPromotion() {
        return promotion;
    }

    public PromoAudit promotion(String promotion) {
        this.promotion = promotion;
        return this;
    }

    public void setPromotion(String promotion) {
        this.promotion = promotion;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public PromoAudit createdBy(String createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public PromoAudit createdAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean isStatus() {
        return status;
    }

    public PromoAudit status(Boolean status) {
        this.status = status;
        return this;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Long getPromotionId() {
        return promotionId;
    }

    public PromoAudit promotionId(Long promotionId) {
        this.promotionId = promotionId;
        return this;
    }

    public void setPromotionId(Long promotionId) {
        this.promotionId = promotionId;
    }

    public String getUpdatedValue() {
        return updatedValue;
    }

    public PromoAudit updatedValue(String updatedValue) {
        this.updatedValue = updatedValue;
        return this;
    }

    public void setUpdatedValue(String updatedValue) {
        this.updatedValue = updatedValue;
    }

    public Boolean isUpdatedStatus() {
        return updatedStatus;
    }

    public PromoAudit updatedStatus(Boolean updatedStatus) {
        this.updatedStatus = updatedStatus;
        return this;
    }

    public void setUpdatedStatus(Boolean updatedStatus) {
        this.updatedStatus = updatedStatus;
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
        PromoAudit promoAudit = (PromoAudit) o;
        if (promoAudit.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), promoAudit.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "PromoAudit{" +
            "id=" + getId() +
            ", promotion='" + getPromotion() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", status='" + isStatus() + "'" +
            ", promotionId=" + getPromotionId() +
            ", updatedValue='" + getUpdatedValue() + "'" +
            ", updatedStatus='" + isUpdatedStatus() + "'" +
            "}";
    }
    
    
  public PromoAudit() {
    	
    }
    
    public PromoAudit(Promotions promotion) {
    	this.promotion = promotion.getPromotion();
    	this.promotionId = promotion.getId();
    	this.status = promotion.isStatus();
    	this.createdBy = promotion.getCreatedBy();
    }
}
