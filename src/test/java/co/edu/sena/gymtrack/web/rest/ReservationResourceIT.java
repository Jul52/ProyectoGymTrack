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

@IntegrationTest
@Disabled("Cyclic required relationships detected")
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ReservationResourceIT {

    private static final Boolean DEFAULT_STATUS = false;
    private static final Boolean UPDATED_STATUS = true;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

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
        Reservation reservation = new Reservation().status(DEFAULT_STATUS).description(DEFAULT_DESCRIPTION);

        GymService gymService;
        if (TestUtil.findAll(em, GymService.class).isEmpty()) {
            gymService = GymServiceResourceIT.createEntity(em);
            em.persist(gymService);
            em.flush();
        } else {
            gymService = TestUtil.findAll(em, GymService.class).get(0);
        }
        reservation.setGymService(gymService);

        UserData userData;
        if (TestUtil.findAll(em, UserData.class).isEmpty()) {
            userData = UserDataResourceIT.createEntity(em);
            em.persist(userData);
            em.flush();
        } else {
            userData = TestUtil.findAll(em, UserData.class).get(0);
        }
        reservation.setRegisteredBy(userData);

        return reservation;
    }

    public static Reservation createUpdatedEntity(EntityManager em) {
        Reservation updatedReservation = new Reservation().status(UPDATED_STATUS).description(UPDATED_DESCRIPTION);

        GymService gymService;
        if (TestUtil.findAll(em, GymService.class).isEmpty()) {
            gymService = GymServiceResourceIT.createUpdatedEntity(em);
            em.persist(gymService);
            em.flush();
        } else {
            gymService = TestUtil.findAll(em, GymService.class).get(0);
        }
        updatedReservation.setGymService(gymService);

        UserData userData;
        if (TestUtil.findAll(em, UserData.class).isEmpty()) {
            userData = UserDataResourceIT.createUpdatedEntity(em);
            em.persist(userData);
            em.flush();
        } else {
            userData = TestUtil.findAll(em, UserData.class).get(0);
        }
        updatedReservation.setRegisteredBy(userData);

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
        long databaseSizeBeforeCreate = reservationRepository.count();

        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);

        restReservationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reservationDTO)))
            .andExpect(status().isCreated());

        assertThat(reservationRepository.count()).isEqualTo(databaseSizeBeforeCreate + 1);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = reservationRepository.count();
        reservation.setStatus(null);

        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);

        restReservationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reservationDTO)))
            .andExpect(status().isBadRequest());

        assertThat(reservationRepository.count()).isEqualTo(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllReservations() throws Exception {
        insertedReservation = reservationRepository.saveAndFlush(reservation);

        restReservationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[*].id").value(hasItem(reservation.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void deleteReservation() throws Exception {
        insertedReservation = reservationRepository.saveAndFlush(reservation);
        long databaseSizeBeforeDelete = reservationRepository.count();

        restReservationMockMvc
            .perform(delete(ENTITY_API_URL_ID, reservation.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        assertThat(reservationRepository.count()).isEqualTo(databaseSizeBeforeDelete - 1);
    }
}
