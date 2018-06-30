package com.sims.tracking.repository;

import com.sims.tracking.domain.Livetracking;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;


/**
 * Spring Data JPA repository for the Livetracking entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LivetrackingRepository extends JpaRepository<Livetracking, Long> {

}
