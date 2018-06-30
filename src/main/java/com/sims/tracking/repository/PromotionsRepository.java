package com.sims.tracking.repository;

import com.sims.tracking.domain.Promotions;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Promotions entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PromotionsRepository extends JpaRepository<Promotions, Long> {

}
