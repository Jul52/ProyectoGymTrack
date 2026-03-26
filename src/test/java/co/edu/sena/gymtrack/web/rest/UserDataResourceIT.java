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

    public static UserData createEntity(EntityManager em) {
        UserData userData = new UserData()
            .firstName(DEFAULT_FIRST_NAME)
            .secondName(DEFAULT_SECOND_NAME)
            .firstLastName(DEFAULT_FIRST_LAST_NAME)
            .secondLastName(DEFAULT_SECOND_LAST_NAME)
            .documentNumber(DEFAULT_DOCUMENT)
            .phone(DEFAULT_PHONE_NUMBER)
            .birthDate(DEFAULT_BIRTH_DATE);
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        userData.setUser(user);
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

    public static UserData createUpdatedEntity(EntityManager em) {
        UserData updatedUserData = new UserData()
            .firstName(UPDATED_FIRST_NAME)
            .secondName(UPDATED_SECOND_NAME)
            .firstLastName(UPDATED_FIRST_LAST_NAME)
            .secondLastName(UPDATED_SECOND_LAST_NAME)
            .documentNumber(UPDATED_DOCUMENT)
            .phone(UPDATED_PHONE_NUMBER)
            .birthDate(UPDATED_BIRTH_DATE);
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedUserData.setUser(user);
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
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedUserData = userDataMapper.toEntity(returnedUserDataDTO);
        assertUserDataUpdatableFieldsEquals(returnedUserData, getPersistedUserData(returnedUserData));
        insertedUserData = returnedUserData;
    }

    @Test
    @Transactional
    void createUserDataWithExistingId() throws Exception {
        userData.setId(1L);
        UserDataDTO userDataDTO = userDataMapper.toDto(userData);
        long databaseSizeBeforeCreate = getRepositoryCount();
        restUserDataMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDataDTO)))
            .andExpect(status().isBadRequest());
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFirstNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        userData.setFirstName(null);
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
        userData.setFirstLastName(null);
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
        userData.setDocumentNumber(null);
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
        userData.setPhone(null);
        UserDataDTO userDataDTO = userDataMapper.toDto(userData);
        restUserDataMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDataDTO)))
            .andExpect(status().isBadRequest());
        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllUserData() throws Exception {
        insertedUserData = userDataRepository.saveAndFlush(userData);
        restUserDataMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userData.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].secondName").value(hasItem(DEFAULT_SECOND_NAME)))
            .andExpect(jsonPath("$.[*].firstLastName").value(hasItem(DEFAULT_FIRST_LAST_NAME)))
            .andExpect(jsonPath("$.[*].secondLastName").value(hasItem(DEFAULT_SECOND_LAST_NAME)))
            .andExpect(jsonPath("$.[*].documentNumber").value(hasItem(DEFAULT_DOCUMENT))) // ✅ corregido
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE_NUMBER))) // ✅ corregido
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
        insertedUserData = userDataRepository.saveAndFlush(userData);
        restUserDataMockMvc
            .perform(get(ENTITY_API_URL_ID, userData.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userData.getId().intValue()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.secondName").value(DEFAULT_SECOND_NAME))
            .andExpect(jsonPath("$.firstLastName").value(DEFAULT_FIRST_LAST_NAME))
            .andExpect(jsonPath("$.secondLastName").value(DEFAULT_SECOND_LAST_NAME))
            .andExpect(jsonPath("$.documentNumber").value(DEFAULT_DOCUMENT)) // ✅ corregido
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE_NUMBER)) // ✅ corregido
            .andExpect(jsonPath("$.birthDate").value(DEFAULT_BIRTH_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingUserData() throws Exception {
        restUserDataMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUserData() throws Exception {
        insertedUserData = userDataRepository.saveAndFlush(userData);
        long databaseSizeBeforeUpdate = getRepositoryCount();
        UserData updatedUserData = userDataRepository.findById(userData.getId()).orElseThrow();
        em.detach(updatedUserData);
        updatedUserData
            .firstName(UPDATED_FIRST_NAME)
            .secondName(UPDATED_SECOND_NAME)
            .firstLastName(UPDATED_FIRST_LAST_NAME)
            .secondLastName(UPDATED_SECOND_LAST_NAME)
            .documentNumber(UPDATED_DOCUMENT)
            .phone(UPDATED_PHONE_NUMBER)
            .birthDate(UPDATED_BIRTH_DATE);
        UserDataDTO userDataDTO = userDataMapper.toDto(updatedUserData);
        restUserDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userDataDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userDataDTO))
            )
            .andExpect(status().isOk());
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUserDataToMatchAllProperties(updatedUserData);
    }

    @Test
    @Transactional
    void putNonExistingUserData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userData.setId(longCount.incrementAndGet());
        UserDataDTO userDataDTO = userDataMapper.toDto(userData);
        restUserDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userDataDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userDataDTO))
            )
            .andExpect(status().isBadRequest());
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userData.setId(longCount.incrementAndGet());
        UserDataDTO userDataDTO = userDataMapper.toDto(userData);
        restUserDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userDataDTO))
            )
            .andExpect(status().isBadRequest());
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userData.setId(longCount.incrementAndGet());
        UserDataDTO userDataDTO = userDataMapper.toDto(userData);
        restUserDataMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDataDTO)))
            .andExpect(status().isMethodNotAllowed());
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUserDataWithPatch() throws Exception {
        insertedUserData = userDataRepository.saveAndFlush(userData);
        long databaseSizeBeforeUpdate = getRepositoryCount();
        UserData partialUpdatedUserData = new UserData();
        partialUpdatedUserData.setId(userData.getId());
        partialUpdatedUserData
            .firstName(UPDATED_FIRST_NAME)
            .firstLastName(UPDATED_FIRST_LAST_NAME)
            .documentNumber(UPDATED_DOCUMENT)
            .phone(UPDATED_PHONE_NUMBER);
        restUserDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserData.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserData))
            )
            .andExpect(status().isOk());
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserDataUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedUserData, userData), getPersistedUserData(userData));
    }

    @Test
    @Transactional
    void fullUpdateUserDataWithPatch() throws Exception {
        insertedUserData = userDataRepository.saveAndFlush(userData);
        long databaseSizeBeforeUpdate = getRepositoryCount();
        UserData partialUpdatedUserData = new UserData();
        partialUpdatedUserData.setId(userData.getId());
        partialUpdatedUserData
            .firstName(UPDATED_FIRST_NAME)
            .secondName(UPDATED_SECOND_NAME)
            .firstLastName(UPDATED_FIRST_LAST_NAME)
            .secondLastName(UPDATED_SECOND_LAST_NAME)
            .documentNumber(UPDATED_DOCUMENT)
            .phone(UPDATED_PHONE_NUMBER)
            .birthDate(UPDATED_BIRTH_DATE);
        restUserDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserData.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserData))
            )
            .andExpect(status().isOk());
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserDataUpdatableFieldsEquals(partialUpdatedUserData, getPersistedUserData(partialUpdatedUserData));
    }

    @Test
    @Transactional
    void patchNonExistingUserData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userData.setId(longCount.incrementAndGet());
        UserDataDTO userDataDTO = userDataMapper.toDto(userData);
        restUserDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userDataDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userDataDTO))
            )
            .andExpect(status().isBadRequest());
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userData.setId(longCount.incrementAndGet());
        UserDataDTO userDataDTO = userDataMapper.toDto(userData);
        restUserDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userDataDTO))
            )
            .andExpect(status().isBadRequest());
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userData.setId(longCount.incrementAndGet());
        UserDataDTO userDataDTO = userDataMapper.toDto(userData);
        restUserDataMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(userDataDTO)))
            .andExpect(status().isMethodNotAllowed());
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUserData() throws Exception {
        insertedUserData = userDataRepository.saveAndFlush(userData);
        long databaseSizeBeforeDelete = getRepositoryCount();
        restUserDataMockMvc
            .perform(delete(ENTITY_API_URL_ID, userData.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
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
