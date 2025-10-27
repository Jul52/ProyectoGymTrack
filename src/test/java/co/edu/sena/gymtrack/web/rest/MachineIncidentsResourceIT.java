package co.edu.sena.gymtrack.web.rest;

import static co.edu.sena.gymtrack.domain.MachineIncidentsAsserts.*;
import static co.edu.sena.gymtrack.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import co.edu.sena.gymtrack.IntegrationTest;
import co.edu.sena.gymtrack.domain.Incident;
import co.edu.sena.gymtrack.domain.Machine;
import co.edu.sena.gymtrack.domain.MachineIncidents;
import co.edu.sena.gymtrack.repository.MachineIncidentsRepository;
import co.edu.sena.gymtrack.service.MachineIncidentsService;
import co.edu.sena.gymtrack.service.dto.MachineIncidentsDTO;
import co.edu.sena.gymtrack.service.mapper.MachineIncidentsMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link MachineIncidentsResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class MachineIncidentsResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_VIDEO = "AAAAAAAAAA";
    private static final String UPDATED_VIDEO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/machine-incidents";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MachineIncidentsRepository machineIncidentsRepository;

    @Mock
    private MachineIncidentsRepository machineIncidentsRepositoryMock;

    @Autowired
    private MachineIncidentsMapper machineIncidentsMapper;

    @Mock
    private MachineIncidentsService machineIncidentsServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMachineIncidentsMockMvc;

    private MachineIncidents machineIncidents;

    private MachineIncidents insertedMachineIncidents;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MachineIncidents createEntity(EntityManager em) {
        MachineIncidents machineIncidents = new MachineIncidents()
            .description(DEFAULT_DESCRIPTION)
            .image(DEFAULT_IMAGE)
            .imageContentType(DEFAULT_IMAGE_CONTENT_TYPE)
            .video(DEFAULT_VIDEO);
        // Add required entity
        Incident incident;
        if (TestUtil.findAll(em, Incident.class).isEmpty()) {
            incident = IncidentResourceIT.createEntity();
            em.persist(incident);
            em.flush();
        } else {
            incident = TestUtil.findAll(em, Incident.class).get(0);
        }
        machineIncidents.setIncident(incident);
        // Add required entity
        Machine machine;
        if (TestUtil.findAll(em, Machine.class).isEmpty()) {
            machine = MachineResourceIT.createEntity(em);
            em.persist(machine);
            em.flush();
        } else {
            machine = TestUtil.findAll(em, Machine.class).get(0);
        }
        machineIncidents.setMachine(machine);
        return machineIncidents;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MachineIncidents createUpdatedEntity(EntityManager em) {
        MachineIncidents updatedMachineIncidents = new MachineIncidents()
            .description(UPDATED_DESCRIPTION)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .video(UPDATED_VIDEO);
        // Add required entity
        Incident incident;
        if (TestUtil.findAll(em, Incident.class).isEmpty()) {
            incident = IncidentResourceIT.createUpdatedEntity();
            em.persist(incident);
            em.flush();
        } else {
            incident = TestUtil.findAll(em, Incident.class).get(0);
        }
        updatedMachineIncidents.setIncident(incident);
        // Add required entity
        Machine machine;
        if (TestUtil.findAll(em, Machine.class).isEmpty()) {
            machine = MachineResourceIT.createUpdatedEntity(em);
            em.persist(machine);
            em.flush();
        } else {
            machine = TestUtil.findAll(em, Machine.class).get(0);
        }
        updatedMachineIncidents.setMachine(machine);
        return updatedMachineIncidents;
    }

    @BeforeEach
    void initTest() {
        machineIncidents = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedMachineIncidents != null) {
            machineIncidentsRepository.delete(insertedMachineIncidents);
            insertedMachineIncidents = null;
        }
    }

    @Test
    @Transactional
    void createMachineIncidents() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the MachineIncidents
        MachineIncidentsDTO machineIncidentsDTO = machineIncidentsMapper.toDto(machineIncidents);
        var returnedMachineIncidentsDTO = om.readValue(
            restMachineIncidentsMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(machineIncidentsDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MachineIncidentsDTO.class
        );

        // Validate the MachineIncidents in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMachineIncidents = machineIncidentsMapper.toEntity(returnedMachineIncidentsDTO);
        assertMachineIncidentsUpdatableFieldsEquals(returnedMachineIncidents, getPersistedMachineIncidents(returnedMachineIncidents));

        insertedMachineIncidents = returnedMachineIncidents;
    }

    @Test
    @Transactional
    void createMachineIncidentsWithExistingId() throws Exception {
        // Create the MachineIncidents with an existing ID
        machineIncidents.setId(1L);
        MachineIncidentsDTO machineIncidentsDTO = machineIncidentsMapper.toDto(machineIncidents);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMachineIncidentsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(machineIncidentsDTO)))
            .andExpect(status().isBadRequest());

        // Validate the MachineIncidents in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllMachineIncidents() throws Exception {
        // Initialize the database
        insertedMachineIncidents = machineIncidentsRepository.saveAndFlush(machineIncidents);

        // Get all the machineIncidentsList
        restMachineIncidentsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(machineIncidents.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_IMAGE))))
            .andExpect(jsonPath("$.[*].video").value(hasItem(DEFAULT_VIDEO)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMachineIncidentsWithEagerRelationshipsIsEnabled() throws Exception {
        when(machineIncidentsServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMachineIncidentsMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(machineIncidentsServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMachineIncidentsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(machineIncidentsServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMachineIncidentsMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(machineIncidentsRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getMachineIncidents() throws Exception {
        // Initialize the database
        insertedMachineIncidents = machineIncidentsRepository.saveAndFlush(machineIncidents);

        // Get the machineIncidents
        restMachineIncidentsMockMvc
            .perform(get(ENTITY_API_URL_ID, machineIncidents.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(machineIncidents.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64.getEncoder().encodeToString(DEFAULT_IMAGE)))
            .andExpect(jsonPath("$.video").value(DEFAULT_VIDEO));
    }

    @Test
    @Transactional
    void getNonExistingMachineIncidents() throws Exception {
        // Get the machineIncidents
        restMachineIncidentsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMachineIncidents() throws Exception {
        // Initialize the database
        insertedMachineIncidents = machineIncidentsRepository.saveAndFlush(machineIncidents);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the machineIncidents
        MachineIncidents updatedMachineIncidents = machineIncidentsRepository.findById(machineIncidents.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMachineIncidents are not directly saved in db
        em.detach(updatedMachineIncidents);
        updatedMachineIncidents
            .description(UPDATED_DESCRIPTION)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .video(UPDATED_VIDEO);
        MachineIncidentsDTO machineIncidentsDTO = machineIncidentsMapper.toDto(updatedMachineIncidents);

        restMachineIncidentsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, machineIncidentsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(machineIncidentsDTO))
            )
            .andExpect(status().isOk());

        // Validate the MachineIncidents in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMachineIncidentsToMatchAllProperties(updatedMachineIncidents);
    }

    @Test
    @Transactional
    void putNonExistingMachineIncidents() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        machineIncidents.setId(longCount.incrementAndGet());

        // Create the MachineIncidents
        MachineIncidentsDTO machineIncidentsDTO = machineIncidentsMapper.toDto(machineIncidents);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMachineIncidentsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, machineIncidentsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(machineIncidentsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MachineIncidents in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMachineIncidents() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        machineIncidents.setId(longCount.incrementAndGet());

        // Create the MachineIncidents
        MachineIncidentsDTO machineIncidentsDTO = machineIncidentsMapper.toDto(machineIncidents);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMachineIncidentsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(machineIncidentsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MachineIncidents in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMachineIncidents() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        machineIncidents.setId(longCount.incrementAndGet());

        // Create the MachineIncidents
        MachineIncidentsDTO machineIncidentsDTO = machineIncidentsMapper.toDto(machineIncidents);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMachineIncidentsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(machineIncidentsDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MachineIncidents in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMachineIncidentsWithPatch() throws Exception {
        // Initialize the database
        insertedMachineIncidents = machineIncidentsRepository.saveAndFlush(machineIncidents);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the machineIncidents using partial update
        MachineIncidents partialUpdatedMachineIncidents = new MachineIncidents();
        partialUpdatedMachineIncidents.setId(machineIncidents.getId());

        partialUpdatedMachineIncidents.image(UPDATED_IMAGE).imageContentType(UPDATED_IMAGE_CONTENT_TYPE);

        restMachineIncidentsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMachineIncidents.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMachineIncidents))
            )
            .andExpect(status().isOk());

        // Validate the MachineIncidents in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMachineIncidentsUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMachineIncidents, machineIncidents),
            getPersistedMachineIncidents(machineIncidents)
        );
    }

    @Test
    @Transactional
    void fullUpdateMachineIncidentsWithPatch() throws Exception {
        // Initialize the database
        insertedMachineIncidents = machineIncidentsRepository.saveAndFlush(machineIncidents);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the machineIncidents using partial update
        MachineIncidents partialUpdatedMachineIncidents = new MachineIncidents();
        partialUpdatedMachineIncidents.setId(machineIncidents.getId());

        partialUpdatedMachineIncidents
            .description(UPDATED_DESCRIPTION)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .video(UPDATED_VIDEO);

        restMachineIncidentsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMachineIncidents.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMachineIncidents))
            )
            .andExpect(status().isOk());

        // Validate the MachineIncidents in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMachineIncidentsUpdatableFieldsEquals(
            partialUpdatedMachineIncidents,
            getPersistedMachineIncidents(partialUpdatedMachineIncidents)
        );
    }

    @Test
    @Transactional
    void patchNonExistingMachineIncidents() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        machineIncidents.setId(longCount.incrementAndGet());

        // Create the MachineIncidents
        MachineIncidentsDTO machineIncidentsDTO = machineIncidentsMapper.toDto(machineIncidents);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMachineIncidentsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, machineIncidentsDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(machineIncidentsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MachineIncidents in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMachineIncidents() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        machineIncidents.setId(longCount.incrementAndGet());

        // Create the MachineIncidents
        MachineIncidentsDTO machineIncidentsDTO = machineIncidentsMapper.toDto(machineIncidents);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMachineIncidentsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(machineIncidentsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MachineIncidents in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMachineIncidents() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        machineIncidents.setId(longCount.incrementAndGet());

        // Create the MachineIncidents
        MachineIncidentsDTO machineIncidentsDTO = machineIncidentsMapper.toDto(machineIncidents);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMachineIncidentsMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(machineIncidentsDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MachineIncidents in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMachineIncidents() throws Exception {
        // Initialize the database
        insertedMachineIncidents = machineIncidentsRepository.saveAndFlush(machineIncidents);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the machineIncidents
        restMachineIncidentsMockMvc
            .perform(delete(ENTITY_API_URL_ID, machineIncidents.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return machineIncidentsRepository.count();
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

    protected MachineIncidents getPersistedMachineIncidents(MachineIncidents machineIncidents) {
        return machineIncidentsRepository.findById(machineIncidents.getId()).orElseThrow();
    }

    protected void assertPersistedMachineIncidentsToMatchAllProperties(MachineIncidents expectedMachineIncidents) {
        assertMachineIncidentsAllPropertiesEquals(expectedMachineIncidents, getPersistedMachineIncidents(expectedMachineIncidents));
    }

    protected void assertPersistedMachineIncidentsToMatchUpdatableProperties(MachineIncidents expectedMachineIncidents) {
        assertMachineIncidentsAllUpdatablePropertiesEquals(
            expectedMachineIncidents,
            getPersistedMachineIncidents(expectedMachineIncidents)
        );
    }
}
