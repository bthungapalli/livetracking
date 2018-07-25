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
import com.sims.tracking.domain.PromoAudit;
import com.sims.tracking.domain.Promotions;
import com.sims.tracking.repository.PromoAuditRepository;
import com.sims.tracking.web.rest.errors.BadRequestAlertException;
import com.sims.tracking.web.rest.util.HeaderUtil;
import com.sims.tracking.web.rest.util.PaginationUtil;

import io.github.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing PromoAudit.
 */
@RestController
@RequestMapping("/api")
public class PromoAuditResource {

    private final Logger log = LoggerFactory.getLogger(PromoAuditResource.class);

    private static final String ENTITY_NAME = "promoAudit";

    private final PromoAuditRepository promoAuditRepository;

    public PromoAuditResource(PromoAuditRepository promoAuditRepository) {
        this.promoAuditRepository = promoAuditRepository;
    }

    /**
     * POST  /promo-audits : Create a new promoAudit.
     *
     * @param promoAudit the promoAudit to create
     * @return the ResponseEntity with status 201 (Created) and with body the new promoAudit, or with status 400 (Bad Request) if the promoAudit has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/promo-audits")
    @Timed
    public ResponseEntity<PromoAudit> createPromoAudit(@Valid @RequestBody PromoAudit promoAudit) throws URISyntaxException {
        log.debug("REST request to save PromoAudit : {}", promoAudit);
        if (promoAudit.getId() != null) {
            throw new BadRequestAlertException("A new promoAudit cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PromoAudit result = promoAuditRepository.save(promoAudit);
        return ResponseEntity.created(new URI("/api/promo-audits/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /promo-audits : Updates an existing promoAudit.
     *
     * @param promoAudit the promoAudit to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated promoAudit,
     * or with status 400 (Bad Request) if the promoAudit is not valid,
     * or with status 500 (Internal Server Error) if the promoAudit couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/promo-audits")
    @Timed
    public ResponseEntity<PromoAudit> updatePromoAudit(@Valid @RequestBody PromoAudit promoAudit) throws URISyntaxException {
        log.debug("REST request to update PromoAudit : {}", promoAudit);
        if (promoAudit.getId() == null) {
            return createPromoAudit(promoAudit);
        }
        PromoAudit result = promoAuditRepository.save(promoAudit);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, promoAudit.getId().toString()))
            .body(result);
    }

    /**
     * GET  /promo-audits : get all the promoAudits.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of promoAudits in body
     */
    @GetMapping("/promo-audits")
    @Timed
    public ResponseEntity<List<PromoAudit>> getAllPromoAudits(Pageable pageable) {
        log.debug("REST request to get a page of PromoAudits");
        Page<PromoAudit> page = promoAuditRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/promo-audits");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /promo-audits/:id : get the "id" promoAudit.
     *
     * @param id the id of the promoAudit to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the promoAudit, or with status 404 (Not Found)
     */
    @GetMapping("/promo-audits/{id}")
    @Timed
    public ResponseEntity<PromoAudit> getPromoAudit(@PathVariable Long id) {
        log.debug("REST request to get PromoAudit : {}", id);
        PromoAudit promoAudit = promoAuditRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(promoAudit));
    }

    /**
     * DELETE  /promo-audits/:id : delete the "id" promoAudit.
     *
     * @param id the id of the promoAudit to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/promo-audits/{id}")
    @Timed
    public ResponseEntity<Void> deletePromoAudit(@PathVariable Long id) {
        log.debug("REST request to delete PromoAudit : {}", id);
        promoAuditRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
    /**
     * 
     * @param pageable
     * @return
     */
    
    @GetMapping("/promo-audits/promotion/{promotionId}")
    @Timed
    public ResponseEntity<List<PromoAudit>> getAuditPromotionsByPromoId(@PathVariable("promotionId") Long promotionId,  Pageable pageable) {
        log.debug("REST request to get a page of PromoAudits");
        PromoAudit queryPromoAudit = new PromoAudit();
        queryPromoAudit .setPromotionId(promotionId);
        Example<PromoAudit> promolog = Example.of(queryPromoAudit);
        Page<PromoAudit> page = promoAuditRepository.findAll(promolog,pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/promo-audits");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
}
