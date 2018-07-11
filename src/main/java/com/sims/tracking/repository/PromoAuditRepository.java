package com.sims.tracking.repository;

import com.sims.tracking.domain.PromoAudit;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the PromoAudit entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PromoAuditRepository extends JpaRepository<PromoAudit, Long> {

}
