package com.sims.tracking.web.rest;

import com.sims.tracking.SimstrackingApp;

import com.sims.tracking.domain.Livetracking;
import com.sims.tracking.repository.LivetrackingRepository;
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
 * Test class for the LivetrackingResource REST controller.
 *
 * @see LivetrackingResource
 */
@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SimstrackingApp.class)
public class LivetrackingResourceIntTest {

    private static final Integer DEFAULT_TRACK_ID = 1;
    private static final Integer UPDATED_TRACK_ID = 2;

    private static final Double DEFAULT_LATITUDE = 1D;
    private static final Double UPDATED_LATITUDE = 2D;

    private static final Double DEFAULT_LONGITUDE = 1D;
    private static final Double UPDATED_LONGITUDE = 2D;

    private static final Instant DEFAULT_CREATED_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private LivetrackingRepository livetrackingRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restLivetrackingMockMvc;

    private Livetracking livetracking;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final LivetrackingResource livetrackingResource = new LivetrackingResource(livetrackingRepository);
        this.restLivetrackingMockMvc = MockMvcBuilders.standaloneSetup(livetrackingResource)
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
    public static Livetracking createEntity(EntityManager em) {
        Livetracking livetracking = new Livetracking()
            .trackId(DEFAULT_TRACK_ID)
            .latitude(DEFAULT_LATITUDE)
            .longitude(DEFAULT_LONGITUDE)
            .createdTime(DEFAULT_CREATED_TIME);
        return livetracking;
    }

    @Before
    public void initTest() {
        livetracking = createEntity(em);
    }

    @Test
    @Transactional
    public void createLivetracking() throws Exception {
        int databaseSizeBeforeCreate = livetrackingRepository.findAll().size();

        // Create the Livetracking
        restLivetrackingMockMvc.perform(post("/api/livetrackings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(livetracking)))
            .andExpect(status().isCreated());

        // Validate the Livetracking in the database
        List<Livetracking> livetrackingList = livetrackingRepository.findAll();
        assertThat(livetrackingList).hasSize(databaseSizeBeforeCreate + 1);
        Livetracking testLivetracking = livetrackingList.get(livetrackingList.size() - 1);
        assertThat(testLivetracking.getTrackId()).isEqualTo(DEFAULT_TRACK_ID);
        assertThat(testLivetracking.getLatitude()).isEqualTo(DEFAULT_LATITUDE);
        assertThat(testLivetracking.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
        assertThat(testLivetracking.getCreatedTime()).isEqualTo(DEFAULT_CREATED_TIME);
    }

    @Test
    @Transactional
    public void createLivetrackingWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = livetrackingRepository.findAll().size();

        // Create the Livetracking with an existing ID
        livetracking.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLivetrackingMockMvc.perform(post("/api/livetrackings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(livetracking)))
            .andExpect(status().isBadRequest());

        // Validate the Livetracking in the database
        List<Livetracking> livetrackingList = livetrackingRepository.findAll();
        assertThat(livetrackingList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkTrackIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = livetrackingRepository.findAll().size();
        // set the field null
        livetracking.setTrackId(null);

        // Create the Livetracking, which fails.

        restLivetrackingMockMvc.perform(post("/api/livetrackings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(livetracking)))
            .andExpect(status().isBadRequest());

        List<Livetracking> livetrackingList = livetrackingRepository.findAll();
        assertThat(livetrackingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLatitudeIsRequired() throws Exception {
        int databaseSizeBeforeTest = livetrackingRepository.findAll().size();
        // set the field null
        livetracking.setLatitude(null);

        // Create the Livetracking, which fails.

        restLivetrackingMockMvc.perform(post("/api/livetrackings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(livetracking)))
            .andExpect(status().isBadRequest());

        List<Livetracking> livetrackingList = livetrackingRepository.findAll();
        assertThat(livetrackingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLongitudeIsRequired() throws Exception {
        int databaseSizeBeforeTest = livetrackingRepository.findAll().size();
        // set the field null
        livetracking.setLongitude(null);

        // Create the Livetracking, which fails.

        restLivetrackingMockMvc.perform(post("/api/livetrackings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(livetracking)))
            .andExpect(status().isBadRequest());

        List<Livetracking> livetrackingList = livetrackingRepository.findAll();
        assertThat(livetrackingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllLivetrackings() throws Exception {
        // Initialize the database
        livetrackingRepository.saveAndFlush(livetracking);

        // Get all the livetrackingList
        restLivetrackingMockMvc.perform(get("/api/livetrackings?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(livetracking.getId().intValue())))
            .andExpect(jsonPath("$.[*].trackId").value(hasItem(DEFAULT_TRACK_ID)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].createdTime").value(hasItem(DEFAULT_CREATED_TIME.toString())));
    }

    @Test
    @Transactional
    public void getLivetracking() throws Exception {
        // Initialize the database
        livetrackingRepository.saveAndFlush(livetracking);

        // Get the livetracking
        restLivetrackingMockMvc.perform(get("/api/livetrackings/{id}", livetracking.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(livetracking.getId().intValue()))
            .andExpect(jsonPath("$.trackId").value(DEFAULT_TRACK_ID))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE.doubleValue()))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE.doubleValue()))
            .andExpect(jsonPath("$.createdTime").value(DEFAULT_CREATED_TIME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingLivetracking() throws Exception {
        // Get the livetracking
        restLivetrackingMockMvc.perform(get("/api/livetrackings/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLivetracking() throws Exception {
        // Initialize the database
        livetrackingRepository.saveAndFlush(livetracking);
        int databaseSizeBeforeUpdate = livetrackingRepository.findAll().size();

        // Update the livetracking
        Livetracking updatedLivetracking = livetrackingRepository.findOne(livetracking.getId());
        // Disconnect from session so that the updates on updatedLivetracking are not directly saved in db
        em.detach(updatedLivetracking);
        updatedLivetracking
            .trackId(UPDATED_TRACK_ID)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .createdTime(UPDATED_CREATED_TIME);

        restLivetrackingMockMvc.perform(put("/api/livetrackings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedLivetracking)))
            .andExpect(status().isOk());

        // Validate the Livetracking in the database
        List<Livetracking> livetrackingList = livetrackingRepository.findAll();
        assertThat(livetrackingList).hasSize(databaseSizeBeforeUpdate);
        Livetracking testLivetracking = livetrackingList.get(livetrackingList.size() - 1);
        assertThat(testLivetracking.getTrackId()).isEqualTo(UPDATED_TRACK_ID);
        assertThat(testLivetracking.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testLivetracking.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
        assertThat(testLivetracking.getCreatedTime()).isEqualTo(UPDATED_CREATED_TIME);
    }

    @Test
    @Transactional
    public void updateNonExistingLivetracking() throws Exception {
        int databaseSizeBeforeUpdate = livetrackingRepository.findAll().size();

        // Create the Livetracking

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restLivetrackingMockMvc.perform(put("/api/livetrackings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(livetracking)))
            .andExpect(status().isCreated());

        // Validate the Livetracking in the database
        List<Livetracking> livetrackingList = livetrackingRepository.findAll();
        assertThat(livetrackingList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteLivetracking() throws Exception {
        // Initialize the database
        livetrackingRepository.saveAndFlush(livetracking);
        int databaseSizeBeforeDelete = livetrackingRepository.findAll().size();

        // Get the livetracking
        restLivetrackingMockMvc.perform(delete("/api/livetrackings/{id}", livetracking.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Livetracking> livetrackingList = livetrackingRepository.findAll();
        assertThat(livetrackingList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Livetracking.class);
        Livetracking livetracking1 = new Livetracking();
        livetracking1.setId(1L);
        Livetracking livetracking2 = new Livetracking();
        livetracking2.setId(livetracking1.getId());
        assertThat(livetracking1).isEqualTo(livetracking2);
        livetracking2.setId(2L);
        assertThat(livetracking1).isNotEqualTo(livetracking2);
        livetracking1.setId(null);
        assertThat(livetracking1).isNotEqualTo(livetracking2);
    }
}
