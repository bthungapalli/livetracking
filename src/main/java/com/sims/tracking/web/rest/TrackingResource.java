package com.sims.tracking.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.sims.tracking.domain.Tracking;
import com.sims.tracking.repository.TrackingRepository;
import com.sims.tracking.web.rest.errors.BadRequestAlertException;
import com.sims.tracking.web.rest.util.HeaderUtil;
import com.sims.tracking.web.rest.util.PaginationUtil;

import io.github.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing Tracking.
 */
@RestController
@RequestMapping("/api")
public class TrackingResource {

    private final Logger log = LoggerFactory.getLogger(TrackingResource.class);

    private static final String ENTITY_NAME = "tracking";

    private final TrackingRepository trackingRepository;

    public TrackingResource(TrackingRepository trackingRepository) {
        this.trackingRepository = trackingRepository;
    }

    /**
     * POST  /trackings : Create a new tracking.
     *
     * @param tracking the tracking to create
     * @return the ResponseEntity with status 201 (Created) and with body the new tracking, or with status 400 (Bad Request) if the tracking has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/trackings")
    @Timed
    public ResponseEntity<Tracking> createTracking(@Valid @RequestBody Tracking tracking) throws URISyntaxException {
        log.debug("REST request to save Tracking : {}", tracking);
        if (tracking.getId() != null) {
            throw new BadRequestAlertException("A new tracking cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Tracking tempTrack = tracking;
        tempTrack.setIs_live(true);
        Tracking result = trackingRepository.save(tempTrack);
        return ResponseEntity.created(new URI("/api/trackings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /trackings : Updates an existing tracking.
     *
     * @param tracking the tracking to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated tracking,
     * or with status 400 (Bad Request) if the tracking is not valid,
     * or with status 500 (Internal Server Error) if the tracking couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/trackings")
    @Timed
    public ResponseEntity<Tracking> updateTracking(@Valid @RequestBody Tracking tracking) throws URISyntaxException {
        log.debug("REST request to update Tracking : {}", tracking);
        if (tracking.getId() == null) {
            return createTracking(tracking);
        }
        Tracking result = trackingRepository.save(tracking);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, tracking.getId().toString()))
            .body(result);
    }

    /**
     * GET  /trackings : get all the trackings.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of trackings in body
     */
    @GetMapping("/webtrackings")
    @Timed
    public ResponseEntity<List<Tracking>> getAllTrackings(Pageable pageable) {
        log.debug("REST request to get a page of Trackings");
        Example<Tracking> isLiveTrackings = Example.of(new Tracking().is_live(Boolean.TRUE));
        Page<Tracking> page = trackingRepository.findAll(isLiveTrackings, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/trackings");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/trackings")
    @Timed
    public ResponseEntity<List<Tracking>> getAllCurrentTrackings(Pageable pageable) {
        log.debug("REST request to get a page of Trackings");
        Example<Tracking> isLiveTrackings = Example.of(new Tracking().is_live(Boolean.TRUE));
        Sort idSort = new Sort(Direction.DESC,"id");
        List<Tracking> trackings= trackingRepository.findAll(isLiveTrackings,idSort);
        return new ResponseEntity<>(trackings, HttpStatus.OK);
    }
    
    
    /**
     * GET  /trackings/:id : get the "id" tracking.
     *
     * @param id the id of the tracking to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the tracking, or with status 404 (Not Found)
     */
    @GetMapping("/trackings/track/{id}")
    @Timed
    public ResponseEntity<Tracking> getTracking(@PathVariable Long id) {
        log.debug("REST request to get Tracking : {}", id);
        Tracking tracking = trackingRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(tracking));
    }
    
    
    
    @GetMapping("/trackings/{requestId}")
    @Timed
    public ResponseEntity<Tracking> getTrackingByRequest(@PathVariable String requestId) {
        log.debug("REST request to get Tracking : {}", requestId);
        Example<Tracking> trackingCriteria = Example.of(new Tracking().requestId(requestId));
        Tracking tracking = trackingRepository.findOne(trackingCriteria);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(tracking));
    }

    /**
     * DELETE  /trackings/:id : delete the "id" tracking.
     *
     * @param id the id of the tracking to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/trackings/{id}")
    @Timed
    public ResponseEntity<Void> deleteTracking(@PathVariable Long id) {
        log.debug("REST request to delete Tracking : {}", id);
        trackingRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
    
    
    @PutMapping("/isLiveFalse")
    @Timed
    public ResponseEntity<?> updateIsLiveFalse(@Valid @RequestBody String requestId) throws URISyntaxException {
        log.debug("REST request to update Tracking : {}", requestId);
        if (requestId == null) {
        	return ResponseEntity.notFound().build();
        }
        Tracking tracking = trackingRepository.findOne(Example.of(new Tracking().requestId(requestId)));
        tracking.setIs_live(Boolean.FALSE);
        trackingRepository.save(tracking);
        return ResponseEntity.noContent().build();
    }
    
}
