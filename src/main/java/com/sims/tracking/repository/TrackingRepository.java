package com.sims.tracking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sims.tracking.domain.Tracking;


/**
 * Spring Data JPA repository for the Tracking entity.
 */
@Repository
public interface TrackingRepository extends JpaRepository<Tracking, Long> {

	@Transactional
	@Modifying(flushAutomatically = true , clearAutomatically = true)
	@Query("update Tracking t set t.is_live = false where t.requestId= :requestId")
	void updateLiveStatus(@Param("requestId") String requestId);

}
