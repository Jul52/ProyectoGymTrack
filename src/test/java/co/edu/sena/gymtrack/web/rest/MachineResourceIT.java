package co.edu.sena.gymtrack.web.rest;

import static co.edu.sena.gymtrack.domain.MachineAsserts.*;
import static co.edu.sena.gymtrack.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import co.edu.sena.gymtrack.IntegrationTest;
import co.edu.sena.gymtrack.domain.Machine;
import co.edu.sena.gymtrack.domain.UserData;
import co.edu.sena.gymtrack.repository.MachineRepository;
import co.edu.sena.gymtrack.service.MachineService;
import co.edu.sena.gymtrack.service.dto.MachineDTO;
import co.edu.sena.gymtrack.service.mapper.MachineMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
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
 * Integration tests for the {@link MachineResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class MachineResourceIT {

    private static final String DEFAULT_SERIAL = "AAAAAAAAAA";
    private static final String UPDATED_SERIAL = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_STATUS = false;
    private static final Boolean UPDATED_STATUS = true;

    private static final String ENTITY_API_URL = "/api/machines";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MachineRepository machineRepository;

    @Mock
    private MachineRepository machineRepositoryMock;

    @Autowired
    private MachineMapper machineMapper;

    @Mock
    private MachineService machineServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMachineMockMvc;

    private Machine machine;

    private Machine insertedMachine;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Machine createEntity(EntityManager em) {
        Machine machine = new Machine().serial(DEFAULT_SERIAL).description(DEFAULT_DESCRIPTION).status(DEFAULT_STATUS);
        // Add required entity
        UserData userData;
        if (TestUtil.findAll(em, UserData.class).isEmpty()) {
            userData = UserDataResourceIT.createEntity(em);
            em.persist(userData);
            em.flush();
        } else {
            userData = TestUtil.findAll(em, UserData.class).get(0);
        }
        machine.setAdmin(userData);
        return machine;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Machine createUpdatedEntity(EntityManager em) {
        Machine updatedMachine = new Machine().serial(UPDATED_SERIAL).description(UPDATED_DESCRIPTION).status(UPDATED_STATUS);
        // Add required entity
        UserData userData;
        if (TestUtil.findAll(em, UserData.class).isEmpty()) {
            userData = UserDataResourceIT.createUpdatedEntity(em);
            em.persist(userData);
            em.flush();
        } else {
            userData = TestUtil.findAll(em, UserData.class).get(0);
        }
        updatedMachine.setAdmin(userData);
        return updatedMachine;
    }

    @BeforeEach
    void initTest() {
        machine = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedMachine != null) {
            machineRepository.delete(insertedMachine);
            insertedMachine = null;
        }
    }

    @Test
    @Transactional
    void createMachine() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Machine
        MachineDTO machineDTO = machineMapper.toDto(machine);
        var returnedMachineDTO = om.readValue(
            restMachineMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(machineDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MachineDTO.class
        );

        // Validate the Machine in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMachine = machineMapper.toEntity(returnedMachineDTO);
        assertMachineUpdatableFieldsEquals(returnedMachine, getPersistedMachine(returnedMachine));

        insertedMachine = returnedMachine;
    }

    @Test
    @Transactional
    void createMachineWithExistingId() throws Exception {
        // Create the Machine with an existing ID
        machine.setId(1L);
        MachineDTO machineDTO = machineMapper.toDto(machine);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMachineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(machineDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Machine in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkSerialIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        machine.setSerial(null);

        // Create the Machine, which fails.
        MachineDTO machineDTO = machineMapper.toDto(machine);

        restMachineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(machineDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDescriptionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        machine.setDescription(null);

        // Create the Machine, which fails.
        MachineDTO machineDTO = machineMapper.toDto(machine);

        restMachineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(machineDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        machine.setStatus(null);

        // Create the Machine, which fails.
        MachineDTO machineDTO = machineMapper.toDto(machine);

        restMachineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(machineDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMachines() throws Exception {
        // Initialize the database
        insertedMachine = machineRepository.saveAndFlush(machine);

        // Get all the machineList
        restMachineMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(machine.getId().intValue())))
            .andExpect(jsonPath("$.[*].serial").value(hasItem(DEFAULT_SERIAL)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMachinesWithEagerRelationshipsIsEnabled() throws Exception {
        when(machineServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMachineMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(machineServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMachinesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(machineServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMachineMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(machineRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getMachine() throws Exception {
        // Initialize the database
        insertedMachine = machineRepository.saveAndFlush(machine);

        // Get the machine
        restMachineMockMvc
            .perform(get(ENTITY_API_URL_ID, machine.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(machine.getId().intValue()))
            .andExpect(jsonPath("$.serial").value(DEFAULT_SERIAL))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS));
    }

    @Test
    @Transactional
    void getNonExistingMachine() throws Exception {
        // Get the machine
        restMachineMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMachine() throws Exception {
        // Initialize the database
        insertedMachine = machineRepository.saveAndFlush(machine);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the machine
        Machine updatedMachine = machineRepository.findById(machine.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMachine are not directly saved in db
        em.detach(updatedMachine);
        updatedMachine.serial(UPDATED_SERIAL).description(UPDATED_DESCRIPTION).status(UPDATED_STATUS);
        MachineDTO machineDTO = machineMapper.toDto(updatedMachine);

        restMachineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, machineDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(machineDTO))
            )
            .andExpect(status().isOk());

        // Validate the Machine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMachineToMatchAllProperties(updatedMachine);
    }

    @Test
    @Transactional
    void putNonExistingMachine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        machine.setId(longCount.incrementAndGet());

        // Create the Machine
        MachineDTO machineDTO = machineMapper.toDto(machine);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMachineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, machineDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(machineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Machine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMachine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        machine.setId(longCount.incrementAndGet());

        // Create the Machine
        MachineDTO machineDTO = machineMapper.toDto(machine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMachineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(machineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Machine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMachine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        machine.setId(longCount.incrementAndGet());

        // Create the Machine
        MachineDTO machineDTO = machineMapper.toDto(machine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMachineMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(machineDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Machine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMachineWithPatch() throws Exception {
        // Initialize the database
        insertedMachine = machineRepository.saveAndFlush(machine);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the machine using partial update
        Machine partialUpdatedMachine = new Machine();
        partialUpdatedMachine.setId(machine.getId());

        partialUpdatedMachine.description(UPDATED_DESCRIPTION);

        restMachineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMachine.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMachine))
            )
            .andExpect(status().isOk());

        // Validate the Machine in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMachineUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedMachine, machine), getPersistedMachine(machine));
    }

    @Test
    @Transactional
    void fullUpdateMachineWithPatch() throws Exception {
        // Initialize the database
        insertedMachine = machineRepository.saveAndFlush(machine);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the machine using partial update
        Machine partialUpdatedMachine = new Machine();
        partialUpdatedMachine.setId(machine.getId());

        partialUpdatedMachine.serial(UPDATED_SERIAL).description(UPDATED_DESCRIPTION).status(UPDATED_STATUS);

        restMachineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMachine.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMachine))
            )
            .andExpect(status().isOk());

        // Validate the Machine in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMachineUpdatableFieldsEquals(partialUpdatedMachine, getPersistedMachine(partialUpdatedMachine));
    }

    @Test
    @Transactional
    void patchNonExistingMachine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        machine.setId(longCount.incrementAndGet());

        // Create the Machine
        MachineDTO machineDTO = machineMapper.toDto(machine);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMachineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, machineDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(machineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Machine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMachine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        machine.setId(longCount.incrementAndGet());

        // Create the Machine
        MachineDTO machineDTO = machineMapper.toDto(machine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMachineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(machineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Machine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMachine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        machine.setId(longCount.incrementAndGet());

        // Create the Machine
        MachineDTO machineDTO = machineMapper.toDto(machine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMachineMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(machineDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Machine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMachine() throws Exception {
        // Initialize the database
        insertedMachine = machineRepository.saveAndFlush(machine);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the machine
        restMachineMockMvc
            .perform(delete(ENTITY_API_URL_ID, machine.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return machineRepository.count();
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

    protected Machine getPersistedMachine(Machine machine) {
        return machineRepository.findById(machine.getId()).orElseThrow();
    }

    protected void assertPersistedMachineToMatchAllProperties(Machine expectedMachine) {
        assertMachineAllPropertiesEquals(expectedMachine, getPersistedMachine(expectedMachine));
    }

    protected void assertPersistedMachineToMatchUpdatableProperties(Machine expectedMachine) {
        assertMachineAllUpdatablePropertiesEquals(expectedMachine, getPersistedMachine(expectedMachine));
    }
}
