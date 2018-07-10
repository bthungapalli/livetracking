package com.sims.tracking.web.rest;

import com.sims.tracking.SimstrackingApp;

import com.sims.tracking.domain.Promotions;
import com.sims.tracking.repository.PromotionsRepository;
import com.sims.tracking.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Ignore;
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
 * Test class for the PromotionsResource REST controller.
 *
 * @see PromotionsResource
 */
@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SimstrackingApp.class)
public class PromotionsResourceIntTest {

    private static final String DEFAULT_PROMOTION = "AAAAAAAAAA";
    private static final String UPDATED_PROMOTION = "BBBBBBBBBB";

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_STATUS = false;
    private static final Boolean UPDATED_STATUS = true;

    @Autowired
    private PromotionsRepository promotionsRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restPromotionsMockMvc;

    private Promotions promotions;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PromotionsResource promotionsResource = new PromotionsResource(promotionsRepository);
        this.restPromotionsMockMvc = MockMvcBuilders.standaloneSetup(promotionsResource)
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
    public static Promotions createEntity(EntityManager em) {
        Promotions promotions = new Promotions()
            .promotion(DEFAULT_PROMOTION)	
            .createdBy(DEFAULT_CREATED_BY)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT)
            .status(DEFAULT_STATUS);
        return promotions;
    }

    @Before
    public void initTest() {
        promotions = createEntity(em);
    }

    @Test
    @Transactional
    public void createPromotions() throws Exception {
        int databaseSizeBeforeCreate = promotionsRepository.findAll().size();

        // Create the Promotions
        restPromotionsMockMvc.perform(post("/api/promotions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(promotions)))
            .andExpect(status().isCreated());

        // Validate the Promotions in the database
        List<Promotions> promotionsList = promotionsRepository.findAll();
        assertThat(promotionsList).hasSize(databaseSizeBeforeCreate + 1);
        Promotions testPromotions = promotionsList.get(promotionsList.size() - 1);
        assertThat(testPromotions.getPromotion()).isEqualTo(DEFAULT_PROMOTION);
        assertThat(testPromotions.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testPromotions.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testPromotions.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
        assertThat(testPromotions.isStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    public void createPromotionsWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = promotionsRepository.findAll().size();

        // Create the Promotions with an existing ID
        promotions.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPromotionsMockMvc.perform(post("/api/promotions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(promotions)))
            .andExpect(status().isBadRequest());

        // Validate the Promotions in the database
        List<Promotions> promotionsList = promotionsRepository.findAll();
        assertThat(promotionsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkPromotionIsRequired() throws Exception {
        int databaseSizeBeforeTest = promotionsRepository.findAll().size();
        // set the field null
        promotions.setPromotion(null);

        // Create the Promotions, which fails.

        restPromotionsMockMvc.perform(post("/api/promotions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(promotions)))
            .andExpect(status().isBadRequest());

        List<Promotions> promotionsList = promotionsRepository.findAll();
        assertThat(promotionsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCreatedByIsRequired() throws Exception {
        int databaseSizeBeforeTest = promotionsRepository.findAll().size();
        // set the field null
        promotions.setCreatedBy(null);

        // Create the Promotions, which fails.

        restPromotionsMockMvc.perform(post("/api/promotions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(promotions)))
            .andExpect(status().isBadRequest());

        List<Promotions> promotionsList = promotionsRepository.findAll();
        assertThat(promotionsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = promotionsRepository.findAll().size();
        // set the field null
        promotions.setStatus(null);

        // Create the Promotions, which fails.

        restPromotionsMockMvc.perform(post("/api/promotions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(promotions)))
            .andExpect(status().isBadRequest());

        List<Promotions> promotionsList = promotionsRepository.findAll();
        assertThat(promotionsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPromotions() throws Exception {
        // Initialize the database
        promotionsRepository.saveAndFlush(promotions);

        // Get all the promotionsList
        restPromotionsMockMvc.perform(get("/api/promotions?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(promotions.getId().intValue())))
            .andExpect(jsonPath("$.[*].promotion").value(hasItem(DEFAULT_PROMOTION.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.booleanValue())));
    }

    @Test
    @Transactional
    public void getPromotions() throws Exception {
        // Initialize the database
        promotionsRepository.saveAndFlush(promotions);

        // Get the promotions
        restPromotionsMockMvc.perform(get("/api/promotions/{id}", promotions.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(promotions.getId().intValue()))
            .andExpect(jsonPath("$.promotion").value(DEFAULT_PROMOTION.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingPromotions() throws Exception {
        // Get the promotions
        restPromotionsMockMvc.perform(get("/api/promotions/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePromotions() throws Exception {
        // Initialize the database
        promotionsRepository.saveAndFlush(promotions);
        int databaseSizeBeforeUpdate = promotionsRepository.findAll().size();

        // Update the promotions
        Promotions updatedPromotions = promotionsRepository.findOne(promotions.getId());
        // Disconnect from session so that the updates on updatedPromotions are not directly saved in db
        em.detach(updatedPromotions);
        updatedPromotions
            .promotion(UPDATED_PROMOTION)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .status(UPDATED_STATUS);

        restPromotionsMockMvc.perform(put("/api/promotions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPromotions)))
            .andExpect(status().isOk());

        // Validate the Promotions in the database
        List<Promotions> promotionsList = promotionsRepository.findAll();
        assertThat(promotionsList).hasSize(databaseSizeBeforeUpdate);
        Promotions testPromotions = promotionsList.get(promotionsList.size() - 1);
        assertThat(testPromotions.getPromotion()).isEqualTo(UPDATED_PROMOTION);
        assertThat(testPromotions.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testPromotions.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testPromotions.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testPromotions.isStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void updateNonExistingPromotions() throws Exception {
        int databaseSizeBeforeUpdate = promotionsRepository.findAll().size();

        // Create the Promotions

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restPromotionsMockMvc.perform(put("/api/promotions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(promotions)))
            .andExpect(status().isCreated());

        // Validate the Promotions in the database
        List<Promotions> promotionsList = promotionsRepository.findAll();
        assertThat(promotionsList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deletePromotions() throws Exception {
        // Initialize the database
        promotionsRepository.saveAndFlush(promotions);
        int databaseSizeBeforeDelete = promotionsRepository.findAll().size();

        // Get the promotions
        restPromotionsMockMvc.perform(delete("/api/promotions/{id}", promotions.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Promotions> promotionsList = promotionsRepository.findAll();
        assertThat(promotionsList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Promotions.class);
        Promotions promotions1 = new Promotions();
        promotions1.setId(1L);
        Promotions promotions2 = new Promotions();
        promotions2.setId(promotions1.getId());
        assertThat(promotions1).isEqualTo(promotions2);
        promotions2.setId(2L);
        assertThat(promotions1).isNotEqualTo(promotions2);
        promotions1.setId(null);
        assertThat(promotions1).isNotEqualTo(promotions2);
    }
}
