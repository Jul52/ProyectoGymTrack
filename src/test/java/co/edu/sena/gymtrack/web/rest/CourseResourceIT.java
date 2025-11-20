package co.edu.sena.gymtrack.web.rest;

import static co.edu.sena.gymtrack.domain.CourseAsserts.*;
import static co.edu.sena.gymtrack.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import co.edu.sena.gymtrack.IntegrationTest;
import co.edu.sena.gymtrack.domain.Course;
import co.edu.sena.gymtrack.domain.UserData;
import co.edu.sena.gymtrack.repository.CourseRepository;
import co.edu.sena.gymtrack.service.CourseService;
import co.edu.sena.gymtrack.service.dto.CourseDTO;
import co.edu.sena.gymtrack.service.mapper.CourseMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
 * Integration tests for the {@link CourseResource} REST controller.
 */
@IntegrationTest
@Disabled("Cyclic required relationships detected")
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CourseResourceIT {

    private static final String DEFAULT_COURSE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_COURSE_NAME = "BBBBBBBBBB";

    private static final Boolean DEFAULT_STATUS = false;
    private static final Boolean UPDATED_STATUS = true;

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Integer DEFAULT_CAPACITY = 1;
    private static final Integer UPDATED_CAPACITY = 2;

    private static final String ENTITY_API_URL = "/api/courses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CourseRepository courseRepository;

    @Mock
    private CourseRepository courseRepositoryMock;

    @Autowired
    private CourseMapper courseMapper;

    @Mock
    private CourseService courseServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCourseMockMvc;

    private Course course;

    private Course insertedCourse;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Course createEntity(EntityManager em) {
        Course course = new Course()
            .courseName(DEFAULT_COURSE_NAME)
            .status(DEFAULT_STATUS)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .capacity(DEFAULT_CAPACITY);
        // Add required entity
        UserData userData;
        if (TestUtil.findAll(em, UserData.class).isEmpty()) {
            userData = UserDataResourceIT.createEntity(em);
            em.persist(userData);
            em.flush();
        } else {
            userData = TestUtil.findAll(em, UserData.class).get(0);
        }
        course.setTrainer(userData);
        return course;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Course createUpdatedEntity(EntityManager em) {
        Course updatedCourse = new Course()
            .courseName(UPDATED_COURSE_NAME)
            .status(UPDATED_STATUS)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .capacity(UPDATED_CAPACITY);
        // Add required entity
        UserData userData;
        if (TestUtil.findAll(em, UserData.class).isEmpty()) {
            userData = UserDataResourceIT.createUpdatedEntity(em);
            em.persist(userData);
            em.flush();
        } else {
            userData = TestUtil.findAll(em, UserData.class).get(0);
        }
        updatedCourse.setTrainer(userData);
        return updatedCourse;
    }

    @BeforeEach
    void initTest() {
        course = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedCourse != null) {
            courseRepository.delete(insertedCourse);
            insertedCourse = null;
        }
    }

    @Test
    @Transactional
    void createCourse() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Course
        CourseDTO courseDTO = courseMapper.toDto(course);
        var returnedCourseDTO = om.readValue(
            restCourseMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CourseDTO.class
        );

        // Validate the Course in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCourse = courseMapper.toEntity(returnedCourseDTO);
        assertCourseUpdatableFieldsEquals(returnedCourse, getPersistedCourse(returnedCourse));

        insertedCourse = returnedCourse;
    }

    @Test
    @Transactional
    void createCourseWithExistingId() throws Exception {
        // Create the Course with an existing ID
        course.setId(1L);
        CourseDTO courseDTO = courseMapper.toDto(course);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCourseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Course in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCourseNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        course.setCourseName(null);

        // Create the Course, which fails.
        CourseDTO courseDTO = courseMapper.toDto(course);

        restCourseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        course.setStatus(null);

        // Create the Course, which fails.
        CourseDTO courseDTO = courseMapper.toDto(course);

        restCourseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStartDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        course.setStartDate(null);

        // Create the Course, which fails.
        CourseDTO courseDTO = courseMapper.toDto(course);

        restCourseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEndDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        course.setEndDate(null);

        // Create the Course, which fails.
        CourseDTO courseDTO = courseMapper.toDto(course);

        restCourseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCourses() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get all the courseList
        restCourseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(course.getId().intValue())))
            .andExpect(jsonPath("$.[*].courseName").value(hasItem(DEFAULT_COURSE_NAME)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].capacity").value(hasItem(DEFAULT_CAPACITY)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCoursesWithEagerRelationshipsIsEnabled() throws Exception {
        when(courseServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCourseMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(courseServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCoursesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(courseServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCourseMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(courseRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCourse() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        // Get the course
        restCourseMockMvc
            .perform(get(ENTITY_API_URL_ID, course.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(course.getId().intValue()))
            .andExpect(jsonPath("$.courseName").value(DEFAULT_COURSE_NAME))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.capacity").value(DEFAULT_CAPACITY));
    }

    @Test
    @Transactional
    void getNonExistingCourse() throws Exception {
        // Get the course
        restCourseMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCourse() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the course
        Course updatedCourse = courseRepository.findById(course.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCourse are not directly saved in db
        em.detach(updatedCourse);
        updatedCourse
            .courseName(UPDATED_COURSE_NAME)
            .status(UPDATED_STATUS)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .capacity(UPDATED_CAPACITY);
        CourseDTO courseDTO = courseMapper.toDto(updatedCourse);

        restCourseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, courseDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseDTO))
            )
            .andExpect(status().isOk());

        // Validate the Course in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCourseToMatchAllProperties(updatedCourse);
    }

    @Test
    @Transactional
    void putNonExistingCourse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        course.setId(longCount.incrementAndGet());

        // Create the Course
        CourseDTO courseDTO = courseMapper.toDto(course);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, courseDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Course in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCourse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        course.setId(longCount.incrementAndGet());

        // Create the Course
        CourseDTO courseDTO = courseMapper.toDto(course);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(courseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Course in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCourse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        course.setId(longCount.incrementAndGet());

        // Create the Course
        CourseDTO courseDTO = courseMapper.toDto(course);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Course in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCourseWithPatch() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the course using partial update
        Course partialUpdatedCourse = new Course();
        partialUpdatedCourse.setId(course.getId());

        partialUpdatedCourse.capacity(UPDATED_CAPACITY);

        restCourseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCourse.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCourse))
            )
            .andExpect(status().isOk());

        // Validate the Course in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCourseUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCourse, course), getPersistedCourse(course));
    }

    @Test
    @Transactional
    void fullUpdateCourseWithPatch() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the course using partial update
        Course partialUpdatedCourse = new Course();
        partialUpdatedCourse.setId(course.getId());

        partialUpdatedCourse
            .courseName(UPDATED_COURSE_NAME)
            .status(UPDATED_STATUS)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .capacity(UPDATED_CAPACITY);

        restCourseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCourse.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCourse))
            )
            .andExpect(status().isOk());

        // Validate the Course in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCourseUpdatableFieldsEquals(partialUpdatedCourse, getPersistedCourse(partialUpdatedCourse));
    }

    @Test
    @Transactional
    void patchNonExistingCourse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        course.setId(longCount.incrementAndGet());

        // Create the Course
        CourseDTO courseDTO = courseMapper.toDto(course);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, courseDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(courseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Course in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCourse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        course.setId(longCount.incrementAndGet());

        // Create the Course
        CourseDTO courseDTO = courseMapper.toDto(course);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(courseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Course in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCourse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        course.setId(longCount.incrementAndGet());

        // Create the Course
        CourseDTO courseDTO = courseMapper.toDto(course);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(courseDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Course in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCourse() throws Exception {
        // Initialize the database
        insertedCourse = courseRepository.saveAndFlush(course);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the course
        restCourseMockMvc
            .perform(delete(ENTITY_API_URL_ID, course.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return courseRepository.count();
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

    protected Course getPersistedCourse(Course course) {
        return courseRepository.findById(course.getId()).orElseThrow();
    }

    protected void assertPersistedCourseToMatchAllProperties(Course expectedCourse) {
        assertCourseAllPropertiesEquals(expectedCourse, getPersistedCourse(expectedCourse));
    }

    protected void assertPersistedCourseToMatchUpdatableProperties(Course expectedCourse) {
        assertCourseAllUpdatablePropertiesEquals(expectedCourse, getPersistedCourse(expectedCourse));
    }
}
