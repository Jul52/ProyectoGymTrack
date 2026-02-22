package co.edu.sena.gymtrack.web.rest;

import static co.edu.sena.gymtrack.domain.ReservationAsserts.*;
import static co.edu.sena.gymtrack.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import co.edu.sena.gymtrack.IntegrationTest;
import co.edu.sena.gymtrack.domain.GymService;
import co.edu.sena.gymtrack.domain.Reservation;
import co.edu.sena.gymtrack.domain.Schedule;
import co.edu.sena.gymtrack.domain.UserData;
import co.edu.sena.gymtrack.repository.ReservationRepository;
import co.edu.sena.gymtrack.service.ReservationService;
import co.edu.sena.gymtrack.service.dto.ReservationDTO;
import co.edu.sena.gymtrack.service.mapper.ReservationMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link ReservationResource} REST controller.
 */
@IntegrationTest
@Disabled("Cyclic required relationships detected")
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ReservationResourceIT {

    private static final Boolean DEFAULT_STATUS = false;
    private static final Boolean UPDATED_STATUS = true;

    private static final String ENTITY_API_URL = "/api/reservations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationRepository reservationRepositoryMock;

    @Autowired
    private ReservationMapper reservationMapper;

    @Mock
    private ReservationService reservationServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReservationMockMvc;

    private Reservation reservation;

    private Reservation insertedReservation;

    public static Reservation createEntity(EntityManager em) {
        Reservation reservation = new Reservation().status(DEFAULT_STATUS);

        // Add required entity GymService
        GymService gymService;
        if (TestUtil.findAll(em, GymService.class).isEmpty()) {
            gymService = GymServiceResourceIT.createEntity(em);
            em.persist(gymService);
            em.flush();
        } else {
            gymService = TestUtil.findAll(em, GymService.class).get(0);
        }
        reservation.setGymService(gymService);

        // Add required entity UserData (RegisteredBy)
        UserData registeredBy;
        if (TestUtil.findAll(em, UserData.class).isEmpty()) {
            registeredBy = UserDataResourceIT.createEntity(em);
            em.persist(registeredBy);
            em.flush();
        } else {
            registeredBy = TestUtil.findAll(em, UserData.class).get(0);
        }
        reservation.setRegisteredBy(registeredBy);

        // Add required entity Schedule
        Schedule schedule;
        if (TestUtil.findAll(em, Schedule.class).isEmpty()) {
            schedule = new Schedule(); // O usa ScheduleResourceIT.createEntity(em) si existe
            em.persist(schedule);
            em.flush();
        } else {
            schedule = TestUtil.findAll(em, Schedule.class).get(0);
        }
        reservation.setSchedule(schedule);

        return reservation;
    }

    public static Reservation createUpdatedEntity(EntityManager em) {
        Reservation updatedReservation = new Reservation().status(UPDATED_STATUS);

        GymService gymService = GymServiceResourceIT.createUpdatedEntity(em);
        em.persist(gymService);
        em.flush();
        updatedReservation.setGymService(gymService);

        UserData registeredBy = UserDataResourceIT.createUpdatedEntity(em);
        em.persist(registeredBy);
        em.flush();
        updatedReservation.setRegisteredBy(registeredBy);

        Schedule schedule = new Schedule();
        em.persist(schedule);
        em.flush();
        updatedReservation.setSchedule(schedule);

        return updatedReservation;
    }

    @BeforeEach
    void initTest() {
        reservation = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedReservation != null) {
            reservationRepository.delete(insertedReservation);
            insertedReservation = null;
        }
    }

    @Test
    @Transactional
    void createReservation() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);
        var returnedReservationDTO = om.readValue(
            restReservationMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reservationDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ReservationDTO.class
        );

        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedReservation = reservationMapper.toEntity(returnedReservationDTO);
        insertedReservation = returnedReservation;
    }

    @Test
    @Transactional
    void getAllReservations() throws Exception {
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        restReservationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(reservation.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));
    }

    @Test
    @Transactional
    void getReservation() throws Exception {
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        restReservationMockMvc
            .perform(get(ENTITY_API_URL_ID, reservation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(reservation.getId().intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS));
    }

    @Test
    @Transactional
    void putExistingReservation() throws Exception {
        insertedReservation = reservationRepository.saveAndFlush(reservation);
        long databaseSizeBeforeUpdate = getRepositoryCount();

        Reservation updatedReservation = reservationRepository.findById(reservation.getId()).orElseThrow();
        em.detach(updatedReservation);
        updatedReservation.status(UPDATED_STATUS);
        ReservationDTO reservationDTO = reservationMapper.toDto(updatedReservation);

        restReservationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, reservationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(reservationDTO))
            )
            .andExpect(status().isOk());

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteReservation() throws Exception {
        insertedReservation = reservationRepository.saveAndFlush(reservation);
        long databaseSizeBeforeDelete = getRepositoryCount();

        restReservationMockMvc
            .perform(delete(ENTITY_API_URL_ID, reservation.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return reservationRepository.count();
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
}
