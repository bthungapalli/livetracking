package com.sims.tracking.web.rest;

import com.sims.tracking.SimstrackingApp;

import com.sims.tracking.domain.PromoAudit;
import com.sims.tracking.repository.PromoAuditRepository;
import com.sims.tracking.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.sims.tracking.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the PromoAuditResource REST controller.
 *
 * @see PromoAuditResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SimstrackingApp.class)
public class PromoAuditResourceIntTest {

    private static final String DEFAULT_PROMOTION = "AAAAAAAAAA";
    private static final String UPDATED_PROMOTION = "BBBBBBBBBB";

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_STATUS = false;
    private static final Boolean UPDATED_STATUS = true;

    private static final Long DEFAULT_PROMOTION_ID = 1L;
    private static final Long UPDATED_PROMOTION_ID = 2L;

    private static final String DEFAULT_UPDATED_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_UPDATED_VALUE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_UPDATED_STATUS = false;
    private static final Boolean UPDATED_UPDATED_STATUS = true;

    @Autowired
    private PromoAuditRepository promoAuditRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restPromoAuditMockMvc;

    private PromoAudit promoAudit;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PromoAuditResource promoAuditResource = new PromoAuditResource(promoAuditRepository);
        this.restPromoAuditMockMvc = MockMvcBuilders.standaloneSetup(promoAuditResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PromoAudit createEntity(EntityManager em) {
        PromoAudit promoAudit = new PromoAudit()
            .promotion(DEFAULT_PROMOTION)
            .createdBy(DEFAULT_CREATED_BY)
            .createdAt(DEFAULT_CREATED_AT)
            .status(DEFAULT_STATUS)
            .promotionId(DEFAULT_PROMOTION_ID)
            .updatedValue(DEFAULT_UPDATED_VALUE)
            .updatedStatus(DEFAULT_UPDATED_STATUS);
        return promoAudit;
    }

    @Before
    public void initTest() {
        promoAudit = createEntity(em);
    }

    @Test
    @Transactional
    public void createPromoAudit() throws Exception {
        int databaseSizeBeforeCreate = promoAuditRepository.findAll().size();

        // Create the PromoAudit
        restPromoAuditMockMvc.perform(post("/api/promo-audits")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(promoAudit)))
            .andExpect(status().isCreated());

        // Validate the PromoAudit in the database
        List<PromoAudit> promoAuditList = promoAuditRepository.findAll();
        assertThat(promoAuditList).hasSize(databaseSizeBeforeCreate + 1);
        PromoAudit testPromoAudit = promoAuditList.get(promoAuditList.size() - 1);
        assertThat(testPromoAudit.getPromotion()).isEqualTo(DEFAULT_PROMOTION);
        assertThat(testPromoAudit.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testPromoAudit.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testPromoAudit.isStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testPromoAudit.getPromotionId()).isEqualTo(DEFAULT_PROMOTION_ID);
        assertThat(testPromoAudit.getUpdatedValue()).isEqualTo(DEFAULT_UPDATED_VALUE);
        assertThat(testPromoAudit.isUpdatedStatus()).isEqualTo(DEFAULT_UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void createPromoAuditWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = promoAuditRepository.findAll().size();

        // Create the PromoAudit with an existing ID
        promoAudit.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPromoAuditMockMvc.perform(post("/api/promo-audits")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(promoAudit)))
            .andExpect(status().isBadRequest());

        // Validate the PromoAudit in the database
        List<PromoAudit> promoAuditList = promoAuditRepository.findAll();
        assertThat(promoAuditList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkPromotionIsRequired() throws Exception {
        int databaseSizeBeforeTest = promoAuditRepository.findAll().size();
        // set the field null
        promoAudit.setPromotion(null);

        // Create the PromoAudit, which fails.

        restPromoAuditMockMvc.perform(post("/api/promo-audits")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(promoAudit)))
            .andExpect(status().isBadRequest());

        List<PromoAudit> promoAuditList = promoAuditRepository.findAll();
        assertThat(promoAuditList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCreatedByIsRequired() throws Exception {
        int databaseSizeBeforeTest = promoAuditRepository.findAll().size();
        // set the field null
        promoAudit.setCreatedBy(null);

        // Create the PromoAudit, which fails.

        restPromoAuditMockMvc.perform(post("/api/promo-audits")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(promoAudit)))
            .andExpect(status().isBadRequest());

        List<PromoAudit> promoAuditList = promoAuditRepository.findAll();
        assertThat(promoAuditList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = promoAuditRepository.findAll().size();
        // set the field null
        promoAudit.setStatus(null);

        // Create the PromoAudit, which fails.

        restPromoAuditMockMvc.perform(post("/api/promo-audits")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(promoAudit)))
            .andExpect(status().isBadRequest());

        List<PromoAudit> promoAuditList = promoAuditRepository.findAll();
        assertThat(promoAuditList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPromotionIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = promoAuditRepository.findAll().size();
        // set the field null
        promoAudit.setPromotionId(null);

        // Create the PromoAudit, which fails.

        restPromoAuditMockMvc.perform(post("/api/promo-audits")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(promoAudit)))
            .andExpect(status().isBadRequest());

        List<PromoAudit> promoAuditList = promoAuditRepository.findAll();
        assertThat(promoAuditList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkUpdatedValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = promoAuditRepository.findAll().size();
        // set the field null
        promoAudit.setUpdatedValue(null);

        // Create the PromoAudit, which fails.

        restPromoAuditMockMvc.perform(post("/api/promo-audits")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(promoAudit)))
            .andExpect(status().isBadRequest());

        List<PromoAudit> promoAuditList = promoAuditRepository.findAll();
        assertThat(promoAuditList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkUpdatedStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = promoAuditRepository.findAll().size();
        // set the field null
        promoAudit.setUpdatedStatus(null);

        // Create the PromoAudit, which fails.

        restPromoAuditMockMvc.perform(post("/api/promo-audits")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(promoAudit)))
            .andExpect(status().isBadRequest());

        List<PromoAudit> promoAuditList = promoAuditRepository.findAll();
        assertThat(promoAuditList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPromoAudits() throws Exception {
        // Initialize the database
        promoAuditRepository.saveAndFlush(promoAudit);

        // Get all the promoAuditList
        restPromoAuditMockMvc.perform(get("/api/promo-audits?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(promoAudit.getId().intValue())))
            .andExpect(jsonPath("$.[*].promotion").value(hasItem(DEFAULT_PROMOTION.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.booleanValue())))
            .andExpect(jsonPath("$.[*].promotionId").value(hasItem(DEFAULT_PROMOTION_ID.intValue())))
            .andExpect(jsonPath("$.[*].updatedValue").value(hasItem(DEFAULT_UPDATED_VALUE.toString())))
            .andExpect(jsonPath("$.[*].updatedStatus").value(hasItem(DEFAULT_UPDATED_STATUS.booleanValue())));
    }

    @Test
    @Transactional
    public void getPromoAudit() throws Exception {
        // Initialize the database
        promoAuditRepository.saveAndFlush(promoAudit);

        // Get the promoAudit
        restPromoAuditMockMvc.perform(get("/api/promo-audits/{id}", promoAudit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(promoAudit.getId().intValue()))
            .andExpect(jsonPath("$.promotion").value(DEFAULT_PROMOTION.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.booleanValue()))
            .andExpect(jsonPath("$.promotionId").value(DEFAULT_PROMOTION_ID.intValue()))
            .andExpect(jsonPath("$.updatedValue").value(DEFAULT_UPDATED_VALUE.toString()))
            .andExpect(jsonPath("$.updatedStatus").value(DEFAULT_UPDATED_STATUS.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingPromoAudit() throws Exception {
        // Get the promoAudit
        restPromoAuditMockMvc.perform(get("/api/promo-audits/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePromoAudit() throws Exception {
        // Initialize the database
        promoAuditRepository.saveAndFlush(promoAudit);
        int databaseSizeBeforeUpdate = promoAuditRepository.findAll().size();

        // Update the promoAudit
        PromoAudit updatedPromoAudit = promoAuditRepository.findOne(promoAudit.getId());
        // Disconnect from session so that the updates on updatedPromoAudit are not directly saved in db
        em.detach(updatedPromoAudit);
        updatedPromoAudit
            .promotion(UPDATED_PROMOTION)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT)
            .status(UPDATED_STATUS)
            .promotionId(UPDATED_PROMOTION_ID)
            .updatedValue(UPDATED_UPDATED_VALUE)
            .updatedStatus(UPDATED_UPDATED_STATUS);

        restPromoAuditMockMvc.perform(put("/api/promo-audits")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPromoAudit)))
            .andExpect(status().isOk());

        // Validate the PromoAudit in the database
        List<PromoAudit> promoAuditList = promoAuditRepository.findAll();
        assertThat(promoAuditList).hasSize(databaseSizeBeforeUpdate);
        PromoAudit testPromoAudit = promoAuditList.get(promoAuditList.size() - 1);
        assertThat(testPromoAudit.getPromotion()).isEqualTo(UPDATED_PROMOTION);
        assertThat(testPromoAudit.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testPromoAudit.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testPromoAudit.isStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testPromoAudit.getPromotionId()).isEqualTo(UPDATED_PROMOTION_ID);
        assertThat(testPromoAudit.getUpdatedValue()).isEqualTo(UPDATED_UPDATED_VALUE);
        assertThat(testPromoAudit.isUpdatedStatus()).isEqualTo(UPDATED_UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void updateNonExistingPromoAudit() throws Exception {
        int databaseSizeBeforeUpdate = promoAuditRepository.findAll().size();

        // Create the PromoAudit

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restPromoAuditMockMvc.perform(put("/api/promo-audits")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(promoAudit)))
            .andExpect(status().isCreated());

        // Validate the PromoAudit in the database
        List<PromoAudit> promoAuditList = promoAuditRepository.findAll();
        assertThat(promoAuditList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deletePromoAudit() throws Exception {
        // Initialize the database
        promoAuditRepository.saveAndFlush(promoAudit);
        int databaseSizeBeforeDelete = promoAuditRepository.findAll().size();

        // Get the promoAudit
        restPromoAuditMockMvc.perform(delete("/api/promo-audits/{id}", promoAudit.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<PromoAudit> promoAuditList = promoAuditRepository.findAll();
        assertThat(promoAuditList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PromoAudit.class);
        PromoAudit promoAudit1 = new PromoAudit();
        promoAudit1.setId(1L);
        PromoAudit promoAudit2 = new PromoAudit();
        promoAudit2.setId(promoAudit1.getId());
        assertThat(promoAudit1).isEqualTo(promoAudit2);
        promoAudit2.setId(2L);
        assertThat(promoAudit1).isNotEqualTo(promoAudit2);
        promoAudit1.setId(null);
        assertThat(promoAudit1).isNotEqualTo(promoAudit2);
    }
}
