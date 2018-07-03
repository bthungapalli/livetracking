package com.sims.tracking.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.sims.tracking.domain.Livetracking;

import com.sims.tracking.repository.LivetrackingRepository;
import com.sims.tracking.web.rest.errors.BadRequestAlertException;
import com.sims.tracking.web.rest.util.HeaderUtil;
import com.sims.tracking.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Livetracking.
 */
@RestController
@RequestMapping("/api")
public class LivetrackingResource {

    private final Logger log = LoggerFactory.getLogger(LivetrackingResource.class);

    private static final String ENTITY_NAME = "livetracking";

    private final LivetrackingRepository livetrackingRepository;

    public LivetrackingResource(LivetrackingRepository livetrackingRepository) {
        this.livetrackingRepository = livetrackingRepository;
    }

    /**
     * POST  /livetrackings : Create a new livetracking.
     *
     * @param livetracking the livetracking to create
     * @return the ResponseEntity with status 201 (Created) and with body the new livetracking, or with status 400 (Bad Request) if the livetracking has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/livetrackings")
    @Timed
    public ResponseEntity<Livetracking> createLivetracking(@Valid @RequestBody Livetracking livetracking) throws URISyntaxException {
        log.debug("REST request to save Livetracking : {}", livetracking);
        if (livetracking.getId() != null) {
            throw new BadRequestAlertException("A new livetracking cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Livetracking result = livetrackingRepository.save(livetracking);
        return ResponseEntity.created(new URI("/api/livetrackings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /livetrackings : Updates an existing livetracking.
     *
     * @param livetracking the livetracking to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated livetracking,
     * or with status 400 (Bad Request) if the livetracking is not valid,
     * or with status 500 (Internal Server Error) if the livetracking couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/livetrackings")
    @Timed
    public ResponseEntity<Livetracking> updateLivetracking(@Valid @RequestBody Livetracking livetracking) throws URISyntaxException {
        log.debug("REST request to update Livetracking : {}", livetracking);
        if (livetracking.getId() == null) {
            return createLivetracking(livetracking);
        }
        Livetracking result = livetrackingRepository.save(livetracking);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, livetracking.getId().toString()))
            .body(result);
    }

    /**
     * GET  /livetrackings : get all the livetrackings.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of livetrackings in body
     */
    @GetMapping("/weblivetrackings")
    @Timed
    public ResponseEntity<List<Livetracking>> getAllLivetrackingsWithPagination(Pageable pageable) {
        log.debug("REST request to get a page of Livetrackings");
        Page<Livetracking> page = livetrackingRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/livetrackings");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/livetrackings")
    @Timed
    public ResponseEntity<List<Livetracking>> getAllLivetrackings() {
        log.debug("REST request to get a page of Livetrackings");
        List<Livetracking> livetrackings = livetrackingRepository.findAll();
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/livetrackings");
        return new ResponseEntity<>(livetrackings, HttpStatus.OK);
    }
    
    
    /**
     * GET  /livetrackings/:id : get the "id" livetracking.
     *
     * @param id the id of the livetracking to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the livetracking, or with status 404 (Not Found)
     */
    @GetMapping("/livetrackings/{id}")
    @Timed
    public ResponseEntity<Livetracking> getLivetracking(@PathVariable Long id) {
        log.debug("REST request to get Livetracking : {}", id);
        Livetracking livetracking = livetrackingRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(livetracking));
    }

    /**
     * DELETE  /livetrackings/:id : delete the "id" livetracking.
     *
     * @param id the id of the livetracking to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/livetrackings/{id}")
    @Timed
    public ResponseEntity<Void> deleteLivetracking(@PathVariable Long id) {
        log.debug("REST request to delete Livetracking : {}", id);
        livetrackingRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
    
    @GetMapping("/livetrackings/track/{id}")
    @Timed
    public ResponseEntity<List<Livetracking>> getLiveTrackingsByTrackId(@PathVariable Integer id) {
        log.debug("REST request to get Tracking : {}", id);
        Example<Livetracking> livetracksByTrackId = Example.of(new Livetracking().trackId(id));
       // Sort sort = new Sort(Sort.Direction.DESC , "created_time");
        List<Livetracking> livetrackings = livetrackingRepository.findAll(livetracksByTrackId);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(livetrackings));
    }
    
    
    @GetMapping("/livetrackings/requestId/{id}")
    @Timed
    public ResponseEntity<List<Livetracking>> getLiveTrackingsByTrackId(@PathVariable String id) {
        log.debug("REST request to get Tracking : {}", id);
        Example<Livetracking> livetracksByRequestId = Example.of(new Livetracking().requestId(id));
       // Sort sort = new Sort(Sort.Direction.DESC , "created_time");
        List<Livetracking> livetrackings = livetrackingRepository.findAll(livetracksByRequestId);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(livetrackings));
    }
}
