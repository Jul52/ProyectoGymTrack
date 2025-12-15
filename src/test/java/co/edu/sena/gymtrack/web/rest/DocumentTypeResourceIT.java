package co.edu.sena.gymtrack.web.rest;

import static co.edu.sena.gymtrack.domain.DocumentTypeAsserts.*;
import static co.edu.sena.gymtrack.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import co.edu.sena.gymtrack.IntegrationTest;
import co.edu.sena.gymtrack.domain.DocumentType;
import co.edu.sena.gymtrack.repository.DocumentTypeRepository;
import co.edu.sena.gymtrack.service.dto.DocumentTypeDTO;
import co.edu.sena.gymtrack.service.mapper.DocumentTypeMapper;
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
 * Integration tests for the {@link DocumentTypeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DocumentTypeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/document-types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DocumentTypeRepository documentTypeRepository;

    @Autowired
    private DocumentTypeMapper documentTypeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDocumentTypeMockMvc;

    private DocumentType documentType;

    private DocumentType insertedDocumentType;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DocumentType createEntity() {
        return new DocumentType().name(DEFAULT_NAME).code(DEFAULT_CODE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DocumentType createUpdatedEntity() {
        return new DocumentType().name(UPDATED_NAME).code(UPDATED_CODE);
    }

    @BeforeEach
    void initTest() {
        documentType = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedDocumentType != null) {
            documentTypeRepository.delete(insertedDocumentType);
            insertedDocumentType = null;
        }
    }

    @Test
    @Transactional
    void createDocumentType() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the DocumentType
        DocumentTypeDTO documentTypeDTO = documentTypeMapper.toDto(documentType);
        var returnedDocumentTypeDTO = om.readValue(
            restDocumentTypeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(documentTypeDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DocumentTypeDTO.class
        );

        // Validate the DocumentType in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDocumentType = documentTypeMapper.toEntity(returnedDocumentTypeDTO);
        assertDocumentTypeUpdatableFieldsEquals(returnedDocumentType, getPersistedDocumentType(returnedDocumentType));

        insertedDocumentType = returnedDocumentType;
    }

    @Test
    @Transactional
    void createDocumentTypeWithExistingId() throws Exception {
        // Create the DocumentType with an existing ID
        documentType.setId(1L);
        DocumentTypeDTO documentTypeDTO = documentTypeMapper.toDto(documentType);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDocumentTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(documentTypeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DocumentType in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        documentType.setName(null);

        // Create the DocumentType, which fails.
        DocumentTypeDTO documentTypeDTO = documentTypeMapper.toDto(documentType);

        restDocumentTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(documentTypeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        documentType.setCode(null);

        // Create the DocumentType, which fails.
        DocumentTypeDTO documentTypeDTO = documentTypeMapper.toDto(documentType);

        restDocumentTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(documentTypeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDocumentTypes() throws Exception {
        // Initialize the database
        insertedDocumentType = documentTypeRepository.saveAndFlush(documentType);

        // Get all the documentTypeList
        restDocumentTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(documentType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)));
    }

    @Test
    @Transactional
    void getDocumentType() throws Exception {
        // Initialize the database
        insertedDocumentType = documentTypeRepository.saveAndFlush(documentType);

        // Get the documentType
        restDocumentTypeMockMvc
            .perform(get(ENTITY_API_URL_ID, documentType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(documentType.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE));
    }

    @Test
    @Transactional
    void getNonExistingDocumentType() throws Exception {
        // Get the documentType
        restDocumentTypeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDocumentType() throws Exception {
        // Initialize the database
        insertedDocumentType = documentTypeRepository.saveAndFlush(documentType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the documentType
        DocumentType updatedDocumentType = documentTypeRepository.findById(documentType.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDocumentType are not directly saved in db
        em.detach(updatedDocumentType);
        updatedDocumentType.name(UPDATED_NAME).code(UPDATED_CODE);
        DocumentTypeDTO documentTypeDTO = documentTypeMapper.toDto(updatedDocumentType);

        restDocumentTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, documentTypeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(documentTypeDTO))
            )
            .andExpect(status().isOk());

        // Validate the DocumentType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDocumentTypeToMatchAllProperties(updatedDocumentType);
    }

    @Test
    @Transactional
    void putNonExistingDocumentType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        documentType.setId(longCount.incrementAndGet());

        // Create the DocumentType
        DocumentTypeDTO documentTypeDTO = documentTypeMapper.toDto(documentType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDocumentTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, documentTypeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(documentTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DocumentType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDocumentType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        documentType.setId(longCount.incrementAndGet());

        // Create the DocumentType
        DocumentTypeDTO documentTypeDTO = documentTypeMapper.toDto(documentType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDocumentTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(documentTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DocumentType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDocumentType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        documentType.setId(longCount.incrementAndGet());

        // Create the DocumentType
        DocumentTypeDTO documentTypeDTO = documentTypeMapper.toDto(documentType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDocumentTypeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(documentTypeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DocumentType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDocumentTypeWithPatch() throws Exception {
        // Initialize the database
        insertedDocumentType = documentTypeRepository.saveAndFlush(documentType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the documentType using partial update
        DocumentType partialUpdatedDocumentType = new DocumentType();
        partialUpdatedDocumentType.setId(documentType.getId());

        restDocumentTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDocumentType.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDocumentType))
            )
            .andExpect(status().isOk());

        // Validate the DocumentType in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDocumentTypeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDocumentType, documentType),
            getPersistedDocumentType(documentType)
        );
    }

    @Test
    @Transactional
    void fullUpdateDocumentTypeWithPatch() throws Exception {
        // Initialize the database
        insertedDocumentType = documentTypeRepository.saveAndFlush(documentType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the documentType using partial update
        DocumentType partialUpdatedDocumentType = new DocumentType();
        partialUpdatedDocumentType.setId(documentType.getId());

        partialUpdatedDocumentType.name(UPDATED_NAME).code(UPDATED_CODE);

        restDocumentTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDocumentType.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDocumentType))
            )
            .andExpect(status().isOk());

        // Validate the DocumentType in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDocumentTypeUpdatableFieldsEquals(partialUpdatedDocumentType, getPersistedDocumentType(partialUpdatedDocumentType));
    }

    @Test
    @Transactional
    void patchNonExistingDocumentType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        documentType.setId(longCount.incrementAndGet());

        // Create the DocumentType
        DocumentTypeDTO documentTypeDTO = documentTypeMapper.toDto(documentType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDocumentTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, documentTypeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(documentTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DocumentType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDocumentType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        documentType.setId(longCount.incrementAndGet());

        // Create the DocumentType
        DocumentTypeDTO documentTypeDTO = documentTypeMapper.toDto(documentType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDocumentTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(documentTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DocumentType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDocumentType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        documentType.setId(longCount.incrementAndGet());

        // Create the DocumentType
        DocumentTypeDTO documentTypeDTO = documentTypeMapper.toDto(documentType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDocumentTypeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(documentTypeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DocumentType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDocumentType() throws Exception {
        // Initialize the database
        insertedDocumentType = documentTypeRepository.saveAndFlush(documentType);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the documentType
        restDocumentTypeMockMvc
            .perform(delete(ENTITY_API_URL_ID, documentType.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return documentTypeRepository.count();
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

    protected DocumentType getPersistedDocumentType(DocumentType documentType) {
        return documentTypeRepository.findById(documentType.getId()).orElseThrow();
    }

    protected void assertPersistedDocumentTypeToMatchAllProperties(DocumentType expectedDocumentType) {
        assertDocumentTypeAllPropertiesEquals(expectedDocumentType, getPersistedDocumentType(expectedDocumentType));
    }

    protected void assertPersistedDocumentTypeToMatchUpdatableProperties(DocumentType expectedDocumentType) {
        assertDocumentTypeAllUpdatablePropertiesEquals(expectedDocumentType, getPersistedDocumentType(expectedDocumentType));
    }
}
