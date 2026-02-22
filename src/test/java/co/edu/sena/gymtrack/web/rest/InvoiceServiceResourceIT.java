package co.edu.sena.gymtrack.web.rest;

import static co.edu.sena.gymtrack.domain.InvoiceServiceAsserts.*;
import static co.edu.sena.gymtrack.web.rest.TestUtil.createUpdateProxyForBean;
import static co.edu.sena.gymtrack.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import co.edu.sena.gymtrack.IntegrationTest;
import co.edu.sena.gymtrack.domain.GymService;
import co.edu.sena.gymtrack.domain.Invoice;
import co.edu.sena.gymtrack.domain.InvoiceService;
import co.edu.sena.gymtrack.repository.InvoiceServiceRepository;
import co.edu.sena.gymtrack.service.dto.InvoiceServiceDTO;
import co.edu.sena.gymtrack.service.mapper.InvoiceServiceMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link InvoiceServiceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class InvoiceServiceResourceIT {

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final BigDecimal DEFAULT_SUBTOTAL = new BigDecimal(1);
    private static final BigDecimal UPDATED_SUBTOTAL = new BigDecimal(2);

    private static final BigDecimal DEFAULT_SALE_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_SALE_PRICE = new BigDecimal(2);

    private static final String ENTITY_API_URL = "/api/invoice-services";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InvoiceServiceRepository invoiceServiceRepository;

    @Autowired
    private InvoiceServiceMapper invoiceServiceMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInvoiceServiceMockMvc;

    private InvoiceService invoiceService;

    private InvoiceService insertedInvoiceService;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InvoiceService createEntity(EntityManager em) {
        InvoiceService invoiceService = new InvoiceService()
            .quantity(DEFAULT_QUANTITY)
            .subtotal(DEFAULT_SUBTOTAL)
            .salePrice(DEFAULT_SALE_PRICE);
        // Add required entity
        Invoice invoice;
        if (TestUtil.findAll(em, Invoice.class).isEmpty()) {
            invoice = InvoiceResourceIT.createEntity(em);
            em.persist(invoice);
            em.flush();
        } else {
            invoice = TestUtil.findAll(em, Invoice.class).get(0);
        }
        invoiceService.setInvoice(invoice);
        // Add required entity
        GymService gymService;
        if (TestUtil.findAll(em, GymService.class).isEmpty()) {
            gymService = GymServiceResourceIT.createEntity(em);
            em.persist(gymService);
            em.flush();
        } else {
            gymService = TestUtil.findAll(em, GymService.class).get(0);
        }
        invoiceService.setService(gymService);
        return invoiceService;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InvoiceService createUpdatedEntity(EntityManager em) {
        InvoiceService updatedInvoiceService = new InvoiceService()
            .quantity(UPDATED_QUANTITY)
            .subtotal(UPDATED_SUBTOTAL)
            .salePrice(UPDATED_SALE_PRICE);
        // Add required entity
        Invoice invoice;
        if (TestUtil.findAll(em, Invoice.class).isEmpty()) {
            invoice = InvoiceResourceIT.createUpdatedEntity(em);
            em.persist(invoice);
            em.flush();
        } else {
            invoice = TestUtil.findAll(em, Invoice.class).get(0);
        }
        updatedInvoiceService.setInvoice(invoice);
        // Add required entity
        GymService gymService;
        if (TestUtil.findAll(em, GymService.class).isEmpty()) {
            gymService = GymServiceResourceIT.createUpdatedEntity(em);
            em.persist(gymService);
            em.flush();
        } else {
            gymService = TestUtil.findAll(em, GymService.class).get(0);
        }
        updatedInvoiceService.setService(gymService);
        return updatedInvoiceService;
    }

    @BeforeEach
    void initTest() {
        invoiceService = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedInvoiceService != null) {
            invoiceServiceRepository.delete(insertedInvoiceService);
            insertedInvoiceService = null;
        }
    }

    @Test
    @Transactional
    void createInvoiceService() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the InvoiceService
        InvoiceServiceDTO invoiceServiceDTO = invoiceServiceMapper.toDto(invoiceService);
        var returnedInvoiceServiceDTO = om.readValue(
            restInvoiceServiceMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(invoiceServiceDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            InvoiceServiceDTO.class
        );

        // Validate the InvoiceService in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedInvoiceService = invoiceServiceMapper.toEntity(returnedInvoiceServiceDTO);
        assertInvoiceServiceUpdatableFieldsEquals(returnedInvoiceService, getPersistedInvoiceService(returnedInvoiceService));

        insertedInvoiceService = returnedInvoiceService;
    }

    @Test
    @Transactional
    void createInvoiceServiceWithExistingId() throws Exception {
        // Create the InvoiceService with an existing ID
        invoiceService.setId(1L);
        InvoiceServiceDTO invoiceServiceDTO = invoiceServiceMapper.toDto(invoiceService);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInvoiceServiceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(invoiceServiceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the InvoiceService in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        invoiceService.setQuantity(null);

        // Create the InvoiceService, which fails.
        InvoiceServiceDTO invoiceServiceDTO = invoiceServiceMapper.toDto(invoiceService);

        restInvoiceServiceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(invoiceServiceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSubtotalIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        invoiceService.setSubtotal(null);

        // Create the InvoiceService, which fails.
        InvoiceServiceDTO invoiceServiceDTO = invoiceServiceMapper.toDto(invoiceService);

        restInvoiceServiceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(invoiceServiceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSalePriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        invoiceService.setSalePrice(null);

        // Create the InvoiceService, which fails.
        InvoiceServiceDTO invoiceServiceDTO = invoiceServiceMapper.toDto(invoiceService);

        restInvoiceServiceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(invoiceServiceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllInvoiceServices() throws Exception {
        // Initialize the database
        insertedInvoiceService = invoiceServiceRepository.saveAndFlush(invoiceService);

        // Get all the invoiceServiceList
        restInvoiceServiceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(invoiceService.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].subtotal").value(hasItem(sameNumber(DEFAULT_SUBTOTAL))))
            .andExpect(jsonPath("$.[*].salePrice").value(hasItem(sameNumber(DEFAULT_SALE_PRICE))));
    }

    @Test
    @Transactional
    void getInvoiceService() throws Exception {
        // Initialize the database
        insertedInvoiceService = invoiceServiceRepository.saveAndFlush(invoiceService);

        // Get the invoiceService
        restInvoiceServiceMockMvc
            .perform(get(ENTITY_API_URL_ID, invoiceService.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(invoiceService.getId().intValue()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.subtotal").value(sameNumber(DEFAULT_SUBTOTAL)))
            .andExpect(jsonPath("$.salePrice").value(sameNumber(DEFAULT_SALE_PRICE)));
    }

    @Test
    @Transactional
    void getNonExistingInvoiceService() throws Exception {
        // Get the invoiceService
        restInvoiceServiceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInvoiceService() throws Exception {
        // Initialize the database
        insertedInvoiceService = invoiceServiceRepository.saveAndFlush(invoiceService);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the invoiceService
        InvoiceService updatedInvoiceService = invoiceServiceRepository.findById(invoiceService.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedInvoiceService are not directly saved in db
        em.detach(updatedInvoiceService);
        updatedInvoiceService.quantity(UPDATED_QUANTITY).subtotal(UPDATED_SUBTOTAL).salePrice(UPDATED_SALE_PRICE);
        InvoiceServiceDTO invoiceServiceDTO = invoiceServiceMapper.toDto(updatedInvoiceService);

        restInvoiceServiceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, invoiceServiceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(invoiceServiceDTO))
            )
            .andExpect(status().isOk());

        // Validate the InvoiceService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInvoiceServiceToMatchAllProperties(updatedInvoiceService);
    }

    @Test
    @Transactional
    void putNonExistingInvoiceService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        invoiceService.setId(longCount.incrementAndGet());

        // Create the InvoiceService
        InvoiceServiceDTO invoiceServiceDTO = invoiceServiceMapper.toDto(invoiceService);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInvoiceServiceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, invoiceServiceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(invoiceServiceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InvoiceService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchInvoiceService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        invoiceService.setId(longCount.incrementAndGet());

        // Create the InvoiceService
        InvoiceServiceDTO invoiceServiceDTO = invoiceServiceMapper.toDto(invoiceService);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInvoiceServiceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(invoiceServiceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InvoiceService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInvoiceService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        invoiceService.setId(longCount.incrementAndGet());

        // Create the InvoiceService
        InvoiceServiceDTO invoiceServiceDTO = invoiceServiceMapper.toDto(invoiceService);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInvoiceServiceMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(invoiceServiceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the InvoiceService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateInvoiceServiceWithPatch() throws Exception {
        // Initialize the database
        insertedInvoiceService = invoiceServiceRepository.saveAndFlush(invoiceService);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the invoiceService using partial update
        InvoiceService partialUpdatedInvoiceService = new InvoiceService();
        partialUpdatedInvoiceService.setId(invoiceService.getId());

        partialUpdatedInvoiceService.quantity(UPDATED_QUANTITY).salePrice(UPDATED_SALE_PRICE);

        restInvoiceServiceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInvoiceService.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInvoiceService))
            )
            .andExpect(status().isOk());

        // Validate the InvoiceService in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInvoiceServiceUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedInvoiceService, invoiceService),
            getPersistedInvoiceService(invoiceService)
        );
    }

    @Test
    @Transactional
    void fullUpdateInvoiceServiceWithPatch() throws Exception {
        // Initialize the database
        insertedInvoiceService = invoiceServiceRepository.saveAndFlush(invoiceService);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the invoiceService using partial update
        InvoiceService partialUpdatedInvoiceService = new InvoiceService();
        partialUpdatedInvoiceService.setId(invoiceService.getId());

        partialUpdatedInvoiceService.quantity(UPDATED_QUANTITY).subtotal(UPDATED_SUBTOTAL).salePrice(UPDATED_SALE_PRICE);

        restInvoiceServiceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInvoiceService.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInvoiceService))
            )
            .andExpect(status().isOk());

        // Validate the InvoiceService in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInvoiceServiceUpdatableFieldsEquals(partialUpdatedInvoiceService, getPersistedInvoiceService(partialUpdatedInvoiceService));
    }

    @Test
    @Transactional
    void patchNonExistingInvoiceService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        invoiceService.setId(longCount.incrementAndGet());

        // Create the InvoiceService
        InvoiceServiceDTO invoiceServiceDTO = invoiceServiceMapper.toDto(invoiceService);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInvoiceServiceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, invoiceServiceDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(invoiceServiceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InvoiceService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInvoiceService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        invoiceService.setId(longCount.incrementAndGet());

        // Create the InvoiceService
        InvoiceServiceDTO invoiceServiceDTO = invoiceServiceMapper.toDto(invoiceService);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInvoiceServiceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(invoiceServiceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InvoiceService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInvoiceService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        invoiceService.setId(longCount.incrementAndGet());

        // Create the InvoiceService
        InvoiceServiceDTO invoiceServiceDTO = invoiceServiceMapper.toDto(invoiceService);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInvoiceServiceMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(invoiceServiceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the InvoiceService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteInvoiceService() throws Exception {
        // Initialize the database
        insertedInvoiceService = invoiceServiceRepository.saveAndFlush(invoiceService);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the invoiceService
        restInvoiceServiceMockMvc
            .perform(delete(ENTITY_API_URL_ID, invoiceService.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return invoiceServiceRepository.count();
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

    protected InvoiceService getPersistedInvoiceService(InvoiceService invoiceService) {
        return invoiceServiceRepository.findById(invoiceService.getId()).orElseThrow();
    }

    protected void assertPersistedInvoiceServiceToMatchAllProperties(InvoiceService expectedInvoiceService) {
        assertInvoiceServiceAllPropertiesEquals(expectedInvoiceService, getPersistedInvoiceService(expectedInvoiceService));
    }

    protected void assertPersistedInvoiceServiceToMatchUpdatableProperties(InvoiceService expectedInvoiceService) {
        assertInvoiceServiceAllUpdatablePropertiesEquals(expectedInvoiceService, getPersistedInvoiceService(expectedInvoiceService));
    }
}
