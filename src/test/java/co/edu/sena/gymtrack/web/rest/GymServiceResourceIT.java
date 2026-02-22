package co.edu.sena.gymtrack.web.rest;

import static co.edu.sena.gymtrack.domain.GymServiceAsserts.*;
import static co.edu.sena.gymtrack.web.rest.TestUtil.createUpdateProxyForBean;
import static co.edu.sena.gymtrack.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import co.edu.sena.gymtrack.IntegrationTest;
import co.edu.sena.gymtrack.domain.Category;
import co.edu.sena.gymtrack.domain.GymService;
import co.edu.sena.gymtrack.repository.GymServiceRepository;
import co.edu.sena.gymtrack.service.GymServiceService;
import co.edu.sena.gymtrack.service.dto.GymServiceDTO;
import co.edu.sena.gymtrack.service.mapper.GymServiceMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link GymServiceResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class GymServiceResourceIT {

    private static final String DEFAULT_SERVICE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SERVICE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SERVICE_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_SERVICE_DESCRIPTION = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(2);

    private static final Boolean DEFAULT_STATUS = false;
    private static final Boolean UPDATED_STATUS = true;

    private static final String ENTITY_API_URL = "/api/gym-services";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private GymServiceRepository gymServiceRepository;

    @Mock
    private GymServiceRepository gymServiceRepositoryMock;

    @Autowired
    private GymServiceMapper gymServiceMapper;

    @Mock
    private GymServiceService gymServiceServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGymServiceMockMvc;

    private GymService gymService;

    private GymService insertedGymService;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GymService createEntity(EntityManager em) {
        GymService gymService = new GymService()
            .serviceName(DEFAULT_SERVICE_NAME)
            .serviceDescription(DEFAULT_SERVICE_DESCRIPTION)
            .price(DEFAULT_PRICE)
            .status(DEFAULT_STATUS);
        // Add required entity
        Category category;
        if (TestUtil.findAll(em, Category.class).isEmpty()) {
            category = CategoryResourceIT.createEntity();
            em.persist(category);
            em.flush();
        } else {
            category = TestUtil.findAll(em, Category.class).get(0);
        }
        gymService.setCategory(category);
        return gymService;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GymService createUpdatedEntity(EntityManager em) {
        GymService updatedGymService = new GymService()
            .serviceName(UPDATED_SERVICE_NAME)
            .serviceDescription(UPDATED_SERVICE_DESCRIPTION)
            .price(UPDATED_PRICE)
            .status(UPDATED_STATUS);
        // Add required entity
        Category category;
        if (TestUtil.findAll(em, Category.class).isEmpty()) {
            category = CategoryResourceIT.createUpdatedEntity();
            em.persist(category);
            em.flush();
        } else {
            category = TestUtil.findAll(em, Category.class).get(0);
        }
        updatedGymService.setCategory(category);
        return updatedGymService;
    }

    @BeforeEach
    void initTest() {
        gymService = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedGymService != null) {
            gymServiceRepository.delete(insertedGymService);
            insertedGymService = null;
        }
    }

    @Test
    @Transactional
    void createGymService() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the GymService
        GymServiceDTO gymServiceDTO = gymServiceMapper.toDto(gymService);
        var returnedGymServiceDTO = om.readValue(
            restGymServiceMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(gymServiceDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            GymServiceDTO.class
        );

        // Validate the GymService in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedGymService = gymServiceMapper.toEntity(returnedGymServiceDTO);
        assertGymServiceUpdatableFieldsEquals(returnedGymService, getPersistedGymService(returnedGymService));

        insertedGymService = returnedGymService;
    }

    @Test
    @Transactional
    void createGymServiceWithExistingId() throws Exception {
        // Create the GymService with an existing ID
        gymService.setId(1L);
        GymServiceDTO gymServiceDTO = gymServiceMapper.toDto(gymService);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restGymServiceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(gymServiceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the GymService in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkServiceNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        gymService.setServiceName(null);

        // Create the GymService, which fails.
        GymServiceDTO gymServiceDTO = gymServiceMapper.toDto(gymService);

        restGymServiceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(gymServiceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        gymService.setPrice(null);

        // Create the GymService, which fails.
        GymServiceDTO gymServiceDTO = gymServiceMapper.toDto(gymService);

        restGymServiceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(gymServiceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        gymService.setStatus(null);

        // Create the GymService, which fails.
        GymServiceDTO gymServiceDTO = gymServiceMapper.toDto(gymService);

        restGymServiceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(gymServiceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllGymServices() throws Exception {
        // Initialize the database
        insertedGymService = gymServiceRepository.saveAndFlush(gymService);

        // Get all the gymServiceList
        restGymServiceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(gymService.getId().intValue())))
            .andExpect(jsonPath("$.[*].serviceName").value(hasItem(DEFAULT_SERVICE_NAME)))
            .andExpect(jsonPath("$.[*].serviceDescription").value(hasItem(DEFAULT_SERVICE_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(sameNumber(DEFAULT_PRICE))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllGymServicesWithEagerRelationshipsIsEnabled() throws Exception {
        when(gymServiceServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restGymServiceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(gymServiceServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllGymServicesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(gymServiceServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restGymServiceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(gymServiceRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getGymService() throws Exception {
        // Initialize the database
        insertedGymService = gymServiceRepository.saveAndFlush(gymService);

        // Get the gymService
        restGymServiceMockMvc
            .perform(get(ENTITY_API_URL_ID, gymService.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(gymService.getId().intValue()))
            .andExpect(jsonPath("$.serviceName").value(DEFAULT_SERVICE_NAME))
            .andExpect(jsonPath("$.serviceDescription").value(DEFAULT_SERVICE_DESCRIPTION))
            .andExpect(jsonPath("$.price").value(sameNumber(DEFAULT_PRICE)))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS));
    }

    @Test
    @Transactional
    void getNonExistingGymService() throws Exception {
        // Get the gymService
        restGymServiceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingGymService() throws Exception {
        // Initialize the database
        insertedGymService = gymServiceRepository.saveAndFlush(gymService);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the gymService
        GymService updatedGymService = gymServiceRepository.findById(gymService.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedGymService are not directly saved in db
        em.detach(updatedGymService);
        updatedGymService
            .serviceName(UPDATED_SERVICE_NAME)
            .serviceDescription(UPDATED_SERVICE_DESCRIPTION)
            .price(UPDATED_PRICE)
            .status(UPDATED_STATUS);
        GymServiceDTO gymServiceDTO = gymServiceMapper.toDto(updatedGymService);

        restGymServiceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, gymServiceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(gymServiceDTO))
            )
            .andExpect(status().isOk());

        // Validate the GymService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedGymServiceToMatchAllProperties(updatedGymService);
    }

    @Test
    @Transactional
    void putNonExistingGymService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        gymService.setId(longCount.incrementAndGet());

        // Create the GymService
        GymServiceDTO gymServiceDTO = gymServiceMapper.toDto(gymService);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGymServiceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, gymServiceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(gymServiceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GymService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchGymService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        gymService.setId(longCount.incrementAndGet());

        // Create the GymService
        GymServiceDTO gymServiceDTO = gymServiceMapper.toDto(gymService);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGymServiceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(gymServiceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GymService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamGymService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        gymService.setId(longCount.incrementAndGet());

        // Create the GymService
        GymServiceDTO gymServiceDTO = gymServiceMapper.toDto(gymService);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGymServiceMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(gymServiceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the GymService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateGymServiceWithPatch() throws Exception {
        // Initialize the database
        insertedGymService = gymServiceRepository.saveAndFlush(gymService);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the gymService using partial update
        GymService partialUpdatedGymService = new GymService();
        partialUpdatedGymService.setId(gymService.getId());

        partialUpdatedGymService.serviceName(UPDATED_SERVICE_NAME).price(UPDATED_PRICE);

        restGymServiceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGymService.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedGymService))
            )
            .andExpect(status().isOk());

        // Validate the GymService in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertGymServiceUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedGymService, gymService),
            getPersistedGymService(gymService)
        );
    }

    @Test
    @Transactional
    void fullUpdateGymServiceWithPatch() throws Exception {
        // Initialize the database
        insertedGymService = gymServiceRepository.saveAndFlush(gymService);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the gymService using partial update
        GymService partialUpdatedGymService = new GymService();
        partialUpdatedGymService.setId(gymService.getId());

        partialUpdatedGymService
            .serviceName(UPDATED_SERVICE_NAME)
            .serviceDescription(UPDATED_SERVICE_DESCRIPTION)
            .price(UPDATED_PRICE)
            .status(UPDATED_STATUS);

        restGymServiceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGymService.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedGymService))
            )
            .andExpect(status().isOk());

        // Validate the GymService in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertGymServiceUpdatableFieldsEquals(partialUpdatedGymService, getPersistedGymService(partialUpdatedGymService));
    }

    @Test
    @Transactional
    void patchNonExistingGymService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        gymService.setId(longCount.incrementAndGet());

        // Create the GymService
        GymServiceDTO gymServiceDTO = gymServiceMapper.toDto(gymService);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGymServiceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, gymServiceDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(gymServiceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GymService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchGymService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        gymService.setId(longCount.incrementAndGet());

        // Create the GymService
        GymServiceDTO gymServiceDTO = gymServiceMapper.toDto(gymService);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGymServiceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(gymServiceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GymService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamGymService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        gymService.setId(longCount.incrementAndGet());

        // Create the GymService
        GymServiceDTO gymServiceDTO = gymServiceMapper.toDto(gymService);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGymServiceMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(gymServiceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the GymService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteGymService() throws Exception {
        // Initialize the database
        insertedGymService = gymServiceRepository.saveAndFlush(gymService);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the gymService
        restGymServiceMockMvc
            .perform(delete(ENTITY_API_URL_ID, gymService.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return gymServiceRepository.count();
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

    protected GymService getPersistedGymService(GymService gymService) {
        return gymServiceRepository.findById(gymService.getId()).orElseThrow();
    }

    protected void assertPersistedGymServiceToMatchAllProperties(GymService expectedGymService) {
        assertGymServiceAllPropertiesEquals(expectedGymService, getPersistedGymService(expectedGymService));
    }

    protected void assertPersistedGymServiceToMatchUpdatableProperties(GymService expectedGymService) {
        assertGymServiceAllUpdatablePropertiesEquals(expectedGymService, getPersistedGymService(expectedGymService));
    }
}
