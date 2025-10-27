package co.edu.sena.gymtrack.web.rest;

import static co.edu.sena.gymtrack.domain.UserDataAsserts.*;
import static co.edu.sena.gymtrack.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import co.edu.sena.gymtrack.IntegrationTest;
import co.edu.sena.gymtrack.domain.DocumentType;
import co.edu.sena.gymtrack.domain.User;
import co.edu.sena.gymtrack.domain.UserData;
import co.edu.sena.gymtrack.repository.UserDataRepository;
import co.edu.sena.gymtrack.repository.UserRepository;
import co.edu.sena.gymtrack.service.UserDataService;
import co.edu.sena.gymtrack.service.dto.UserDataDTO;
import co.edu.sena.gymtrack.service.mapper.UserDataMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link UserDataResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class UserDataResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SECOND_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SECOND_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_FIRST_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SECOND_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SECOND_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DOCUMENT = "AAAAAAAAAA";
    private static final String UPDATED_DOCUMENT = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_BIRTH_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_BIRTH_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/user-data";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserDataRepository userDataRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private UserDataRepository userDataRepositoryMock;

    @Autowired
    private UserDataMapper userDataMapper;

    @Mock
    private UserDataService userDataServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserDataMockMvc;

    private UserData userData;

    private UserData insertedUserData;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserData createEntity(EntityManager em) {
        UserData userData = new UserData()
            .firstName(DEFAULT_FIRST_NAME)
            .secondName(DEFAULT_SECOND_NAME)
            .firstLastName(DEFAULT_FIRST_LAST_NAME)
            .secondLastName(DEFAULT_SECOND_LAST_NAME)
            .document(DEFAULT_DOCUMENT)
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .birthDate(DEFAULT_BIRTH_DATE);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        userData.setUser(user);
        // Add required entity
        DocumentType documentType;
        if (TestUtil.findAll(em, DocumentType.class).isEmpty()) {
            documentType = DocumentTypeResourceIT.createEntity();
            em.persist(documentType);
            em.flush();
        } else {
            documentType = TestUtil.findAll(em, DocumentType.class).get(0);
        }
        userData.setDocumentType(documentType);
        return userData;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserData createUpdatedEntity(EntityManager em) {
        UserData updatedUserData = new UserData()
            .firstName(UPDATED_FIRST_NAME)
            .secondName(UPDATED_SECOND_NAME)
            .firstLastName(UPDATED_FIRST_LAST_NAME)
            .secondLastName(UPDATED_SECOND_LAST_NAME)
            .document(UPDATED_DOCUMENT)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .birthDate(UPDATED_BIRTH_DATE);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedUserData.setUser(user);
        // Add required entity
        DocumentType documentType;
        if (TestUtil.findAll(em, DocumentType.class).isEmpty()) {
            documentType = DocumentTypeResourceIT.createUpdatedEntity();
            em.persist(documentType);
            em.flush();
        } else {
            documentType = TestUtil.findAll(em, DocumentType.class).get(0);
        }
        updatedUserData.setDocumentType(documentType);
        return updatedUserData;
    }

    @BeforeEach
    void initTest() {
        userData = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedUserData != null) {
            userDataRepository.delete(insertedUserData);
            insertedUserData = null;
        }
    }

    @Test
    @Transactional
    void createUserData() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the UserData
        UserDataDTO userDataDTO = userDataMapper.toDto(userData);
        var returnedUserDataDTO = om.readValue(
            restUserDataMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDataDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            UserDataDTO.class
        );

        // Validate the UserData in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedUserData = userDataMapper.toEntity(returnedUserDataDTO);
        assertUserDataUpdatableFieldsEquals(returnedUserData, getPersistedUserData(returnedUserData));

        insertedUserData = returnedUserData;
    }

    @Test
    @Transactional
    void createUserDataWithExistingId() throws Exception {
        // Create the UserData with an existing ID
        userData.setId(1L);
        UserDataDTO userDataDTO = userDataMapper.toDto(userData);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserDataMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDataDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserData in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFirstNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userData.setFirstName(null);

        // Create the UserData, which fails.
        UserDataDTO userDataDTO = userDataMapper.toDto(userData);

        restUserDataMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDataDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFirstLastNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userData.setFirstLastName(null);

        // Create the UserData, which fails.
        UserDataDTO userDataDTO = userDataMapper.toDto(userData);

        restUserDataMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDataDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDocumentIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userData.setDocument(null);

        // Create the UserData, which fails.
        UserDataDTO userDataDTO = userDataMapper.toDto(userData);

        restUserDataMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDataDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPhoneNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userData.setPhoneNumber(null);

        // Create the UserData, which fails.
        UserDataDTO userDataDTO = userDataMapper.toDto(userData);

        restUserDataMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDataDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllUserData() throws Exception {
        // Initialize the database
        insertedUserData = userDataRepository.saveAndFlush(userData);

        // Get all the userDataList
        restUserDataMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userData.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].secondName").value(hasItem(DEFAULT_SECOND_NAME)))
            .andExpect(jsonPath("$.[*].firstLastName").value(hasItem(DEFAULT_FIRST_LAST_NAME)))
            .andExpect(jsonPath("$.[*].secondLastName").value(hasItem(DEFAULT_SECOND_LAST_NAME)))
            .andExpect(jsonPath("$.[*].document").value(hasItem(DEFAULT_DOCUMENT)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].birthDate").value(hasItem(DEFAULT_BIRTH_DATE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllUserDataWithEagerRelationshipsIsEnabled() throws Exception {
        when(userDataServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restUserDataMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(userDataServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllUserDataWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(userDataServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restUserDataMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(userDataRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getUserData() throws Exception {
        // Initialize the database
        insertedUserData = userDataRepository.saveAndFlush(userData);

        // Get the userData
        restUserDataMockMvc
            .perform(get(ENTITY_API_URL_ID, userData.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userData.getId().intValue()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.secondName").value(DEFAULT_SECOND_NAME))
            .andExpect(jsonPath("$.firstLastName").value(DEFAULT_FIRST_LAST_NAME))
            .andExpect(jsonPath("$.secondLastName").value(DEFAULT_SECOND_LAST_NAME))
            .andExpect(jsonPath("$.document").value(DEFAULT_DOCUMENT))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER))
            .andExpect(jsonPath("$.birthDate").value(DEFAULT_BIRTH_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingUserData() throws Exception {
        // Get the userData
        restUserDataMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUserData() throws Exception {
        // Initialize the database
        insertedUserData = userDataRepository.saveAndFlush(userData);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userData
        UserData updatedUserData = userDataRepository.findById(userData.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedUserData are not directly saved in db
        em.detach(updatedUserData);
        updatedUserData
            .firstName(UPDATED_FIRST_NAME)
            .secondName(UPDATED_SECOND_NAME)
            .firstLastName(UPDATED_FIRST_LAST_NAME)
            .secondLastName(UPDATED_SECOND_LAST_NAME)
            .document(UPDATED_DOCUMENT)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .birthDate(UPDATED_BIRTH_DATE);
        UserDataDTO userDataDTO = userDataMapper.toDto(updatedUserData);

        restUserDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userDataDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userDataDTO))
            )
            .andExpect(status().isOk());

        // Validate the UserData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUserDataToMatchAllProperties(updatedUserData);
    }

    @Test
    @Transactional
    void putNonExistingUserData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userData.setId(longCount.incrementAndGet());

        // Create the UserData
        UserDataDTO userDataDTO = userDataMapper.toDto(userData);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userDataDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userDataDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userData.setId(longCount.incrementAndGet());

        // Create the UserData
        UserDataDTO userDataDTO = userDataMapper.toDto(userData);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userDataDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userData.setId(longCount.incrementAndGet());

        // Create the UserData
        UserDataDTO userDataDTO = userDataMapper.toDto(userData);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserDataMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDataDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUserDataWithPatch() throws Exception {
        // Initialize the database
        insertedUserData = userDataRepository.saveAndFlush(userData);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userData using partial update
        UserData partialUpdatedUserData = new UserData();
        partialUpdatedUserData.setId(userData.getId());

        partialUpdatedUserData.firstLastName(UPDATED_FIRST_LAST_NAME).secondLastName(UPDATED_SECOND_LAST_NAME).document(UPDATED_DOCUMENT);

        restUserDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserData.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserData))
            )
            .andExpect(status().isOk());

        // Validate the UserData in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserDataUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedUserData, userData), getPersistedUserData(userData));
    }

    @Test
    @Transactional
    void fullUpdateUserDataWithPatch() throws Exception {
        // Initialize the database
        insertedUserData = userDataRepository.saveAndFlush(userData);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userData using partial update
        UserData partialUpdatedUserData = new UserData();
        partialUpdatedUserData.setId(userData.getId());

        partialUpdatedUserData
            .firstName(UPDATED_FIRST_NAME)
            .secondName(UPDATED_SECOND_NAME)
            .firstLastName(UPDATED_FIRST_LAST_NAME)
            .secondLastName(UPDATED_SECOND_LAST_NAME)
            .document(UPDATED_DOCUMENT)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .birthDate(UPDATED_BIRTH_DATE);

        restUserDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserData.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserData))
            )
            .andExpect(status().isOk());

        // Validate the UserData in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserDataUpdatableFieldsEquals(partialUpdatedUserData, getPersistedUserData(partialUpdatedUserData));
    }

    @Test
    @Transactional
    void patchNonExistingUserData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userData.setId(longCount.incrementAndGet());

        // Create the UserData
        UserDataDTO userDataDTO = userDataMapper.toDto(userData);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userDataDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userDataDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userData.setId(longCount.incrementAndGet());

        // Create the UserData
        UserDataDTO userDataDTO = userDataMapper.toDto(userData);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userDataDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userData.setId(longCount.incrementAndGet());

        // Create the UserData
        UserDataDTO userDataDTO = userDataMapper.toDto(userData);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserDataMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(userDataDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUserData() throws Exception {
        // Initialize the database
        insertedUserData = userDataRepository.saveAndFlush(userData);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the userData
        restUserDataMockMvc
            .perform(delete(ENTITY_API_URL_ID, userData.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return userDataRepository.count();
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

    protected UserData getPersistedUserData(UserData userData) {
        return userDataRepository.findById(userData.getId()).orElseThrow();
    }

    protected void assertPersistedUserDataToMatchAllProperties(UserData expectedUserData) {
        assertUserDataAllPropertiesEquals(expectedUserData, getPersistedUserData(expectedUserData));
    }

    protected void assertPersistedUserDataToMatchUpdatableProperties(UserData expectedUserData) {
        assertUserDataAllUpdatablePropertiesEquals(expectedUserData, getPersistedUserData(expectedUserData));
    }
}
