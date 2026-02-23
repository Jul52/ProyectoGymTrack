package co.edu.sena.gymtrack.web.rest;

import static co.edu.sena.gymtrack.domain.IncidentAsserts.*;
import static co.edu.sena.gymtrack.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import co.edu.sena.gymtrack.IntegrationTest;
import co.edu.sena.gymtrack.domain.Incident;
import co.edu.sena.gymtrack.repository.IncidentRepository;
import co.edu.sena.gymtrack.service.dto.IncidentDTO;
import co.edu.sena.gymtrack.service.mapper.IncidentMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link IncidentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class IncidentResourceIT {

    private static final String DEFAULT_INCIDENT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_INCIDENT_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Instant DEFAULT_REPORTED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REPORTED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/incidents";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private IncidentMapper incidentMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restIncidentMockMvc;

    private Incident incident;

    private Incident insertedIncident;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Incident createEntity() {
        return new Incident().incidentType(DEFAULT_INCIDENT_TYPE).description(DEFAULT_DESCRIPTION).reportedDate(DEFAULT_REPORTED_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Incident createUpdatedEntity() {
        return new Incident().incidentType(UPDATED_INCIDENT_TYPE).description(UPDATED_DESCRIPTION).reportedDate(UPDATED_REPORTED_DATE);
    }

    @BeforeEach
    void initTest() {
        incident = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedIncident != null) {
            incidentRepository.delete(insertedIncident);
            insertedIncident = null;
        }
    }

    @Test
    @Transactional
    void createIncident() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Incident
        IncidentDTO incidentDTO = incidentMapper.toDto(incident);
        var returnedIncidentDTO = om.readValue(
            restIncidentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(incidentDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            IncidentDTO.class
        );

        // Validate the Incident in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedIncident = incidentMapper.toEntity(returnedIncidentDTO);
        assertIncidentUpdatableFieldsEquals(returnedIncident, getPersistedIncident(returnedIncident));

        insertedIncident = returnedIncident;
    }

    @Test
    @Transactional
    void createIncidentWithExistingId() throws Exception {
        // Create the Incident with an existing ID
        incident.setId(1L);
        IncidentDTO incidentDTO = incidentMapper.toDto(incident);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restIncidentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(incidentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Incident in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkIncidentTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        incident.setIncidentType(null);

        // Create the Incident, which fails.
        IncidentDTO incidentDTO = incidentMapper.toDto(incident);

        restIncidentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(incidentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllIncidents() throws Exception {
        // Initialize the database
        insertedIncident = incidentRepository.saveAndFlush(incident);

        // Get all the incidentList
        restIncidentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(incident.getId().intValue())))
            .andExpect(jsonPath("$.[*].incidentType").value(hasItem(DEFAULT_INCIDENT_TYPE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].reportedDate").value(hasItem(DEFAULT_REPORTED_DATE.toString())));
    }

    @Test
    @Transactional
    void getIncident() throws Exception {
        // Initialize the database
        insertedIncident = incidentRepository.saveAndFlush(incident);

        // Get the incident
        restIncidentMockMvc
            .perform(get(ENTITY_API_URL_ID, incident.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(incident.getId().intValue()))
            .andExpect(jsonPath("$.incidentType").value(DEFAULT_INCIDENT_TYPE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.reportedDate").value(DEFAULT_REPORTED_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingIncident() throws Exception {
        // Get the incident
        restIncidentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingIncident() throws Exception {
        // Initialize the database
        insertedIncident = incidentRepository.saveAndFlush(incident);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the incident
        Incident updatedIncident = incidentRepository.findById(incident.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedIncident are not directly saved in db
        em.detach(updatedIncident);
        updatedIncident.incidentType(UPDATED_INCIDENT_TYPE).description(UPDATED_DESCRIPTION).reportedDate(UPDATED_REPORTED_DATE);
        IncidentDTO incidentDTO = incidentMapper.toDto(updatedIncident);

        restIncidentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, incidentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(incidentDTO))
            )
            .andExpect(status().isOk());

        // Validate the Incident in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedIncidentToMatchAllProperties(updatedIncident);
    }

    @Test
    @Transactional
    void putNonExistingIncident() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        incident.setId(longCount.incrementAndGet());

        // Create the Incident
        IncidentDTO incidentDTO = incidentMapper.toDto(incident);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIncidentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, incidentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(incidentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Incident in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchIncident() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        incident.setId(longCount.incrementAndGet());

        // Create the Incident
        IncidentDTO incidentDTO = incidentMapper.toDto(incident);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIncidentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(incidentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Incident in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamIncident() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        incident.setId(longCount.incrementAndGet());

        // Create the Incident
        IncidentDTO incidentDTO = incidentMapper.toDto(incident);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIncidentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(incidentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Incident in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateIncidentWithPatch() throws Exception {
        // Initialize the database
        insertedIncident = incidentRepository.saveAndFlush(incident);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the incident using partial update
        Incident partialUpdatedIncident = new Incident();
        partialUpdatedIncident.setId(incident.getId());

        restIncidentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIncident.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedIncident))
            )
            .andExpect(status().isOk());

        // Validate the Incident in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertIncidentUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedIncident, incident), getPersistedIncident(incident));
    }

    @Test
    @Transactional
    void fullUpdateIncidentWithPatch() throws Exception {
        // Initialize the database
        insertedIncident = incidentRepository.saveAndFlush(incident);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the incident using partial update
        Incident partialUpdatedIncident = new Incident();
        partialUpdatedIncident.setId(incident.getId());

        partialUpdatedIncident.incidentType(UPDATED_INCIDENT_TYPE).description(UPDATED_DESCRIPTION).reportedDate(UPDATED_REPORTED_DATE);

        restIncidentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIncident.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedIncident))
            )
            .andExpect(status().isOk());

        // Validate the Incident in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertIncidentUpdatableFieldsEquals(partialUpdatedIncident, getPersistedIncident(partialUpdatedIncident));
    }

    @Test
    @Transactional
    void patchNonExistingIncident() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        incident.setId(longCount.incrementAndGet());

        // Create the Incident
        IncidentDTO incidentDTO = incidentMapper.toDto(incident);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIncidentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, incidentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(incidentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Incident in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchIncident() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        incident.setId(longCount.incrementAndGet());

        // Create the Incident
        IncidentDTO incidentDTO = incidentMapper.toDto(incident);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIncidentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(incidentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Incident in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamIncident() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        incident.setId(longCount.incrementAndGet());

        // Create the Incident
        IncidentDTO incidentDTO = incidentMapper.toDto(incident);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIncidentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(incidentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Incident in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteIncident() throws Exception {
        // Initialize the database
        insertedIncident = incidentRepository.saveAndFlush(incident);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the incident
        restIncidentMockMvc
            .perform(delete(ENTITY_API_URL_ID, incident.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return incidentRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Incident getPersistedIncident(Incident incident) {
        return incidentRepository.findById(incident.getId()).orElseThrow();
    }

    protected void assertPersistedIncidentToMatchAllProperties(Incident expectedIncident) {
        assertIncidentAllPropertiesEquals(expectedIncident, getPersistedIncident(expectedIncident));
    }

    protected void assertPersistedIncidentToMatchUpdatableProperties(Incident expectedIncident) {
        assertIncidentAllUpdatablePropertiesEquals(expectedIncident, getPersistedIncident(expectedIncident));
    }
}
