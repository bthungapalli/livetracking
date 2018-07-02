package com.sims.tracking.repository;

import com.sims.tracking.domain.Tracking;
import org.springframework.stereotype.Repository;
import org.hibernate.annotations.SQLUpdate;
import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Tracking entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TrackingRepository extends JpaRepository<Tracking, Long> {


}
