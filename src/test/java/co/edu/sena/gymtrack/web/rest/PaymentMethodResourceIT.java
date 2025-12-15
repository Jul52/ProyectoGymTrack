package co.edu.sena.gymtrack.web.rest;

import static co.edu.sena.gymtrack.domain.PaymentMethodAsserts.*;
import static co.edu.sena.gymtrack.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import co.edu.sena.gymtrack.IntegrationTest;
import co.edu.sena.gymtrack.domain.PaymentMethod;
import co.edu.sena.gymtrack.repository.PaymentMethodRepository;
import co.edu.sena.gymtrack.service.dto.PaymentMethodDTO;
import co.edu.sena.gymtrack.service.mapper.PaymentMethodMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link PaymentMethodResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PaymentMethodResourceIT {

    private static final String DEFAULT_METHOD_NAME = "AAAAAAAAAA";
    private static final String UPDATED_METHOD_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/payment-methods";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private PaymentMethodMapper paymentMethodMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPaymentMethodMockMvc;

    private PaymentMethod paymentMethod;

    private PaymentMethod insertedPaymentMethod;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PaymentMethod createEntity() {
        return new PaymentMethod().methodName(DEFAULT_METHOD_NAME);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PaymentMethod createUpdatedEntity() {
        return new PaymentMethod().methodName(UPDATED_METHOD_NAME);
    }

    @BeforeEach
    void initTest() {
        paymentMethod = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPaymentMethod != null) {
            paymentMethodRepository.delete(insertedPaymentMethod);
            insertedPaymentMethod = null;
        }
    }

    @Test
    @Transactional
    void createPaymentMethod() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PaymentMethod
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(paymentMethod);
        var returnedPaymentMethodDTO = om.readValue(
            restPaymentMethodMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentMethodDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PaymentMethodDTO.class
        );

        // Validate the PaymentMethod in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPaymentMethod = paymentMethodMapper.toEntity(returnedPaymentMethodDTO);
        assertPaymentMethodUpdatableFieldsEquals(returnedPaymentMethod, getPersistedPaymentMethod(returnedPaymentMethod));

        insertedPaymentMethod = returnedPaymentMethod;
    }

    @Test
    @Transactional
    void createPaymentMethodWithExistingId() throws Exception {
        // Create the PaymentMethod with an existing ID
        paymentMethod.setId(1L);
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(paymentMethod);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPaymentMethodMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentMethodDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PaymentMethod in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkMethodNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        paymentMethod.setMethodName(null);

        // Create the PaymentMethod, which fails.
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(paymentMethod);

        restPaymentMethodMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentMethodDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPaymentMethods() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList
        restPaymentMethodMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(paymentMethod.getId().intValue())))
            .andExpect(jsonPath("$.[*].methodName").value(hasItem(DEFAULT_METHOD_NAME)));
    }

    @Test
    @Transactional
    void getPaymentMethod() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get the paymentMethod
        restPaymentMethodMockMvc
            .perform(get(ENTITY_API_URL_ID, paymentMethod.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(paymentMethod.getId().intValue()))
            .andExpect(jsonPath("$.methodName").value(DEFAULT_METHOD_NAME));
    }

    @Test
    @Transactional
    void getNonExistingPaymentMethod() throws Exception {
        // Get the paymentMethod
        restPaymentMethodMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPaymentMethod() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the paymentMethod
        PaymentMethod updatedPaymentMethod = paymentMethodRepository.findById(paymentMethod.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPaymentMethod are not directly saved in db
        em.detach(updatedPaymentMethod);
        updatedPaymentMethod.methodName(UPDATED_METHOD_NAME);
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(updatedPaymentMethod);

        restPaymentMethodMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paymentMethodDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentMethodDTO))
            )
            .andExpect(status().isOk());

        // Validate the PaymentMethod in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPaymentMethodToMatchAllProperties(updatedPaymentMethod);
    }

    @Test
    @Transactional
    void putNonExistingPaymentMethod() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentMethod.setId(longCount.incrementAndGet());

        // Create the PaymentMethod
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(paymentMethod);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaymentMethodMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paymentMethodDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentMethodDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentMethod in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPaymentMethod() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentMethod.setId(longCount.incrementAndGet());

        // Create the PaymentMethod
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(paymentMethod);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentMethodMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentMethodDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentMethod in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPaymentMethod() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentMethod.setId(longCount.incrementAndGet());

        // Create the PaymentMethod
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(paymentMethod);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentMethodMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentMethodDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PaymentMethod in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePaymentMethodWithPatch() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the paymentMethod using partial update
        PaymentMethod partialUpdatedPaymentMethod = new PaymentMethod();
        partialUpdatedPaymentMethod.setId(paymentMethod.getId());

        restPaymentMethodMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPaymentMethod.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPaymentMethod))
            )
            .andExpect(status().isOk());

        // Validate the PaymentMethod in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPaymentMethodUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPaymentMethod, paymentMethod),
            getPersistedPaymentMethod(paymentMethod)
        );
    }

    @Test
    @Transactional
    void fullUpdatePaymentMethodWithPatch() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the paymentMethod using partial update
        PaymentMethod partialUpdatedPaymentMethod = new PaymentMethod();
        partialUpdatedPaymentMethod.setId(paymentMethod.getId());

        partialUpdatedPaymentMethod.methodName(UPDATED_METHOD_NAME);

        restPaymentMethodMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPaymentMethod.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPaymentMethod))
            )
            .andExpect(status().isOk());

        // Validate the PaymentMethod in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPaymentMethodUpdatableFieldsEquals(partialUpdatedPaymentMethod, getPersistedPaymentMethod(partialUpdatedPaymentMethod));
    }

    @Test
    @Transactional
    void patchNonExistingPaymentMethod() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentMethod.setId(longCount.incrementAndGet());

        // Create the PaymentMethod
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(paymentMethod);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaymentMethodMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, paymentMethodDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(paymentMethodDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentMethod in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPaymentMethod() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentMethod.setId(longCount.incrementAndGet());

        // Create the PaymentMethod
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(paymentMethod);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentMethodMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(paymentMethodDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentMethod in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPaymentMethod() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentMethod.setId(longCount.incrementAndGet());

        // Create the PaymentMethod
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(paymentMethod);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentMethodMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(paymentMethodDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PaymentMethod in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePaymentMethod() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the paymentMethod
        restPaymentMethodMockMvc
            .perform(delete(ENTITY_API_URL_ID, paymentMethod.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return paymentMethodRepository.count();
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

    protected PaymentMethod getPersistedPaymentMethod(PaymentMethod paymentMethod) {
        return paymentMethodRepository.findById(paymentMethod.getId()).orElseThrow();
    }

    protected void assertPersistedPaymentMethodToMatchAllProperties(PaymentMethod expectedPaymentMethod) {
        assertPaymentMethodAllPropertiesEquals(expectedPaymentMethod, getPersistedPaymentMethod(expectedPaymentMethod));
    }

    protected void assertPersistedPaymentMethodToMatchUpdatableProperties(PaymentMethod expectedPaymentMethod) {
        assertPaymentMethodAllUpdatablePropertiesEquals(expectedPaymentMethod, getPersistedPaymentMethod(expectedPaymentMethod));
    }
}
