package com.sims.tracking.web.rest;

import com.sims.tracking.SimstrackingApp;

import com.sims.tracking.domain.Tracking;
import com.sims.tracking.repository.TrackingRepository;
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
 * Test class for the TrackingResource REST controller.
 *
 * @see TrackingResource
 */
@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SimstrackingApp.class)
public class TrackingResourceIntTest {

    private static final String DEFAULT_MCO_ID = "AAAAAAAAAA";
    private static final String UPDATED_MCO_ID = "BBBBBBBBBB";

    private static final String DEFAULT_CUSTOMER_ID = "AAAAAAAAAA";
    private static final String UPDATED_CUSTOMER_ID = "BBBBBBBBBB";

    private static final Double DEFAULT_CLONG = 1D;
    private static final Double UPDATED_CLONG = 2D;

    private static final Double DEFAULT_CLAT = 1D;
    private static final Double UPDATED_CLAT = 2D;

    private static final Double DEFAULT_MLAT = 1D;
    private static final Double UPDATED_MLAT = 2D;

    private static final Double DEFAULT_MLONG = 1D;
    private static final Double UPDATED_MLONG = 2D;

    private static final Boolean DEFAULT_IS_LIVE = false;
    private static final Boolean UPDATED_IS_LIVE = true;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CNAME = "AAAAAAAAAA";
    private static final String UPDATED_CNAME = "BBBBBBBBBB";

    private static final String DEFAULT_MNAME = "AAAAAAAAAA";
    private static final String UPDATED_MNAME = "BBBBBBBBBB";

    private static final String DEFAULT_REQUEST_ID = "AAAAAAAAAA";
    private static final String UPDATED_REQUEST_ID = "BBBBBBBBBB";

    @Autowired
    private TrackingRepository trackingRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restTrackingMockMvc;

    private Tracking tracking;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final TrackingResource trackingResource = new TrackingResource(trackingRepository);
        this.restTrackingMockMvc = MockMvcBuilders.standaloneSetup(trackingResource)
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
    public static Tracking createEntity(EntityManager em) {
        Tracking tracking = new Tracking()
            .mcoId(DEFAULT_MCO_ID)
            .customer_id(DEFAULT_CUSTOMER_ID)
            .clong(DEFAULT_CLONG)
            .clat(DEFAULT_CLAT)
            .mlat(DEFAULT_MLAT)
            .mlong(DEFAULT_MLONG)
            .is_live(DEFAULT_IS_LIVE)
            .created_at(DEFAULT_CREATED_AT)
            .cname(DEFAULT_CNAME)
            .mname(DEFAULT_MNAME)
            .requestId(DEFAULT_REQUEST_ID);
        return tracking;
    }

    @Before
    public void initTest() {
        tracking = createEntity(em);
    }

    @Test
    @Transactional
    public void createTracking() throws Exception {
        int databaseSizeBeforeCreate = trackingRepository.findAll().size();

        // Create the Tracking
        restTrackingMockMvc.perform(post("/api/trackings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tracking)))
            .andExpect(status().isCreated());

        // Validate the Tracking in the database
        List<Tracking> trackingList = trackingRepository.findAll();
        assertThat(trackingList).hasSize(databaseSizeBeforeCreate + 1);
        Tracking testTracking = trackingList.get(trackingList.size() - 1);
        assertThat(testTracking.getMcoId()).isEqualTo(DEFAULT_MCO_ID);
        assertThat(testTracking.getCustomer_id()).isEqualTo(DEFAULT_CUSTOMER_ID);
        assertThat(testTracking.getClong()).isEqualTo(DEFAULT_CLONG);
        assertThat(testTracking.getClat()).isEqualTo(DEFAULT_CLAT);
        assertThat(testTracking.getMlat()).isEqualTo(DEFAULT_MLAT);
        assertThat(testTracking.getMlong()).isEqualTo(DEFAULT_MLONG);
        assertThat(testTracking.isIs_live()).isEqualTo(DEFAULT_IS_LIVE);
        assertThat(testTracking.getCreated_at()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testTracking.getCname()).isEqualTo(DEFAULT_CNAME);
        assertThat(testTracking.getMname()).isEqualTo(DEFAULT_MNAME);
        assertThat(testTracking.getRequestId()).isEqualTo(DEFAULT_REQUEST_ID);
    }

    @Test
    @Transactional
    public void createTrackingWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = trackingRepository.findAll().size();

        // Create the Tracking with an existing ID
        tracking.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTrackingMockMvc.perform(post("/api/trackings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tracking)))
            .andExpect(status().isBadRequest());

        // Validate the Tracking in the database
        List<Tracking> trackingList = trackingRepository.findAll();
        assertThat(trackingList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkMcoIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = trackingRepository.findAll().size();
        // set the field null
        tracking.setMcoId(null);

        // Create the Tracking, which fails.

        restTrackingMockMvc.perform(post("/api/trackings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tracking)))
            .andExpect(status().isBadRequest());

        List<Tracking> trackingList = trackingRepository.findAll();
        assertThat(trackingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCustomer_idIsRequired() throws Exception {
        int databaseSizeBeforeTest = trackingRepository.findAll().size();
        // set the field null
        tracking.setCustomer_id(null);

        // Create the Tracking, which fails.

        restTrackingMockMvc.perform(post("/api/trackings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tracking)))
            .andExpect(status().isBadRequest());

        List<Tracking> trackingList = trackingRepository.findAll();
        assertThat(trackingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkClongIsRequired() throws Exception {
        int databaseSizeBeforeTest = trackingRepository.findAll().size();
        // set the field null
        tracking.setClong(null);

        // Create the Tracking, which fails.

        restTrackingMockMvc.perform(post("/api/trackings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tracking)))
            .andExpect(status().isBadRequest());

        List<Tracking> trackingList = trackingRepository.findAll();
        assertThat(trackingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkClatIsRequired() throws Exception {
        int databaseSizeBeforeTest = trackingRepository.findAll().size();
        // set the field null
        tracking.setClat(null);

        // Create the Tracking, which fails.

        restTrackingMockMvc.perform(post("/api/trackings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tracking)))
            .andExpect(status().isBadRequest());

        List<Tracking> trackingList = trackingRepository.findAll();
        assertThat(trackingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkMlatIsRequired() throws Exception {
        int databaseSizeBeforeTest = trackingRepository.findAll().size();
        // set the field null
        tracking.setMlat(null);

        // Create the Tracking, which fails.

        restTrackingMockMvc.perform(post("/api/trackings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tracking)))
            .andExpect(status().isBadRequest());

        List<Tracking> trackingList = trackingRepository.findAll();
        assertThat(trackingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkMlongIsRequired() throws Exception {
        int databaseSizeBeforeTest = trackingRepository.findAll().size();
        // set the field null
        tracking.setMlong(null);

        // Create the Tracking, which fails.

        restTrackingMockMvc.perform(post("/api/trackings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tracking)))
            .andExpect(status().isBadRequest());

        List<Tracking> trackingList = trackingRepository.findAll();
        assertThat(trackingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkIs_liveIsRequired() throws Exception {
        int databaseSizeBeforeTest = trackingRepository.findAll().size();
        // set the field null
        tracking.setIs_live(null);

        // Create the Tracking, which fails.

        restTrackingMockMvc.perform(post("/api/trackings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tracking)))
            .andExpect(status().isBadRequest());

        List<Tracking> trackingList = trackingRepository.findAll();
        assertThat(trackingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkRequestIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = trackingRepository.findAll().size();
        // set the field null
        tracking.setRequestId(null);

        // Create the Tracking, which fails.

        restTrackingMockMvc.perform(post("/api/trackings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tracking)))
            .andExpect(status().isBadRequest());

        List<Tracking> trackingList = trackingRepository.findAll();
        assertThat(trackingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTrackings() throws Exception {
        // Initialize the database
        trackingRepository.saveAndFlush(tracking);

        // Get all the trackingList
        restTrackingMockMvc.perform(get("/api/trackings?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tracking.getId().intValue())))
            .andExpect(jsonPath("$.[*].mcoId").value(hasItem(DEFAULT_MCO_ID.toString())))
            .andExpect(jsonPath("$.[*].customer_id").value(hasItem(DEFAULT_CUSTOMER_ID.toString())))
            .andExpect(jsonPath("$.[*].clong").value(hasItem(DEFAULT_CLONG.doubleValue())))
            .andExpect(jsonPath("$.[*].clat").value(hasItem(DEFAULT_CLAT.doubleValue())))
            .andExpect(jsonPath("$.[*].mlat").value(hasItem(DEFAULT_MLAT.doubleValue())))
            .andExpect(jsonPath("$.[*].mlong").value(hasItem(DEFAULT_MLONG.doubleValue())))
            .andExpect(jsonPath("$.[*].is_live").value(hasItem(DEFAULT_IS_LIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].created_at").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].cname").value(hasItem(DEFAULT_CNAME.toString())))
            .andExpect(jsonPath("$.[*].mname").value(hasItem(DEFAULT_MNAME.toString())))
            .andExpect(jsonPath("$.[*].requestId").value(hasItem(DEFAULT_REQUEST_ID.toString())));
    }

    @Test
    @Transactional
    public void getTracking() throws Exception {
        // Initialize the database
        trackingRepository.saveAndFlush(tracking);

        // Get the tracking
        restTrackingMockMvc.perform(get("/api/trackings/{id}", tracking.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(tracking.getId().intValue()))
            .andExpect(jsonPath("$.mcoId").value(DEFAULT_MCO_ID.toString()))
            .andExpect(jsonPath("$.customer_id").value(DEFAULT_CUSTOMER_ID.toString()))
            .andExpect(jsonPath("$.clong").value(DEFAULT_CLONG.doubleValue()))
            .andExpect(jsonPath("$.clat").value(DEFAULT_CLAT.doubleValue()))
            .andExpect(jsonPath("$.mlat").value(DEFAULT_MLAT.doubleValue()))
            .andExpect(jsonPath("$.mlong").value(DEFAULT_MLONG.doubleValue()))
            .andExpect(jsonPath("$.is_live").value(DEFAULT_IS_LIVE.booleanValue()))
            .andExpect(jsonPath("$.created_at").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.cname").value(DEFAULT_CNAME.toString()))
            .andExpect(jsonPath("$.mname").value(DEFAULT_MNAME.toString()))
            .andExpect(jsonPath("$.requestId").value(DEFAULT_REQUEST_ID.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTracking() throws Exception {
        // Get the tracking
        restTrackingMockMvc.perform(get("/api/trackings/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTracking() throws Exception {
        // Initialize the database
        trackingRepository.saveAndFlush(tracking);
        int databaseSizeBeforeUpdate = trackingRepository.findAll().size();

        // Update the tracking
        Tracking updatedTracking = trackingRepository.findOne(tracking.getId());
        // Disconnect from session so that the updates on updatedTracking are not directly saved in db
        em.detach(updatedTracking);
        updatedTracking
            .mcoId(UPDATED_MCO_ID)
            .customer_id(UPDATED_CUSTOMER_ID)
            .clong(UPDATED_CLONG)
            .clat(UPDATED_CLAT)
            .mlat(UPDATED_MLAT)
            .mlong(UPDATED_MLONG)
            .is_live(UPDATED_IS_LIVE)
            .created_at(UPDATED_CREATED_AT)
            .cname(UPDATED_CNAME)
            .mname(UPDATED_MNAME)
            .requestId(UPDATED_REQUEST_ID);

        restTrackingMockMvc.perform(put("/api/trackings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedTracking)))
            .andExpect(status().isOk());

        // Validate the Tracking in the database
        List<Tracking> trackingList = trackingRepository.findAll();
        assertThat(trackingList).hasSize(databaseSizeBeforeUpdate);
        Tracking testTracking = trackingList.get(trackingList.size() - 1);
        assertThat(testTracking.getMcoId()).isEqualTo(UPDATED_MCO_ID);
        assertThat(testTracking.getCustomer_id()).isEqualTo(UPDATED_CUSTOMER_ID);
        assertThat(testTracking.getClong()).isEqualTo(UPDATED_CLONG);
        assertThat(testTracking.getClat()).isEqualTo(UPDATED_CLAT);
        assertThat(testTracking.getMlat()).isEqualTo(UPDATED_MLAT);
        assertThat(testTracking.getMlong()).isEqualTo(UPDATED_MLONG);
        assertThat(testTracking.isIs_live()).isEqualTo(UPDATED_IS_LIVE);
        assertThat(testTracking.getCreated_at()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testTracking.getCname()).isEqualTo(UPDATED_CNAME);
        assertThat(testTracking.getMname()).isEqualTo(UPDATED_MNAME);
        assertThat(testTracking.getRequestId()).isEqualTo(UPDATED_REQUEST_ID);
    }

    @Test
    @Transactional
    public void updateNonExistingTracking() throws Exception {
        int databaseSizeBeforeUpdate = trackingRepository.findAll().size();

        // Create the Tracking

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restTrackingMockMvc.perform(put("/api/trackings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tracking)))
            .andExpect(status().isCreated());

        // Validate the Tracking in the database
        List<Tracking> trackingList = trackingRepository.findAll();
        assertThat(trackingList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteTracking() throws Exception {
        // Initialize the database
        trackingRepository.saveAndFlush(tracking);
        int databaseSizeBeforeDelete = trackingRepository.findAll().size();

        // Get the tracking
        restTrackingMockMvc.perform(delete("/api/trackings/{id}", tracking.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Tracking> trackingList = trackingRepository.findAll();
        assertThat(trackingList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Tracking.class);
        Tracking tracking1 = new Tracking();
        tracking1.setId(1L);
        Tracking tracking2 = new Tracking();
        tracking2.setId(tracking1.getId());
        assertThat(tracking1).isEqualTo(tracking2);
        tracking2.setId(2L);
        assertThat(tracking1).isNotEqualTo(tracking2);
        tracking1.setId(null);
        assertThat(tracking1).isNotEqualTo(tracking2);
    }
}
