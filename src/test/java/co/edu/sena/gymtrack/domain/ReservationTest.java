package co.edu.sena.gymtrack.domain;

import static co.edu.sena.gymtrack.domain.CourseTestSamples.*;
import static co.edu.sena.gymtrack.domain.GymServiceTestSamples.*;
import static co.edu.sena.gymtrack.domain.ReservationTestSamples.*;
import static co.edu.sena.gymtrack.domain.UserDataTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import co.edu.sena.gymtrack.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReservationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Reservation.class);
        Reservation reservation1 = getReservationSample1();
        Reservation reservation2 = new Reservation();
        assertThat(reservation1).isNotEqualTo(reservation2);

        reservation2.setId(reservation1.getId());
        assertThat(reservation1).isEqualTo(reservation2);

        reservation2 = getReservationSample2();
        assertThat(reservation1).isNotEqualTo(reservation2);
    }

    @Test
    void courseTest() {
        Reservation reservation = getReservationRandomSampleGenerator();
        Course courseBack = getCourseRandomSampleGenerator();

        reservation.setCourse(courseBack);
        assertThat(reservation.getCourse()).isEqualTo(courseBack);

        reservation.course(null);
        assertThat(reservation.getCourse()).isNull();
    }

    @Test
    void gymServiceTest() {
        Reservation reservation = getReservationRandomSampleGenerator();
        GymService gymServiceBack = getGymServiceRandomSampleGenerator();

        reservation.setGymService(gymServiceBack);
        assertThat(reservation.getGymService()).isEqualTo(gymServiceBack);

        reservation.gymService(null);
        assertThat(reservation.getGymService()).isNull();
    }

    @Test
    void userDataTest() {
        Reservation reservation = getReservationRandomSampleGenerator();
        UserData userDataBack = getUserDataRandomSampleGenerator();

        reservation.setRegisteredBy(userDataBack);
        assertThat(reservation.getRegisteredBy()).isEqualTo(userDataBack);

        reservation.registeredBy(null);
        assertThat(reservation.getRegisteredBy()).isNull();
    }
}
