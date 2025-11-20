package co.edu.sena.gymtrack.domain;

import static co.edu.sena.gymtrack.domain.CourseTestSamples.*;
import static co.edu.sena.gymtrack.domain.ReservationTestSamples.*;
import static co.edu.sena.gymtrack.domain.ScheduleTestSamples.*;
import static co.edu.sena.gymtrack.domain.UserDataTestSamples.*;
import static co.edu.sena.gymtrack.domain.ZoneTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import co.edu.sena.gymtrack.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CourseTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Course.class);
        Course course1 = getCourseSample1();
        Course course2 = new Course();
        assertThat(course1).isNotEqualTo(course2);

        course2.setId(course1.getId());
        assertThat(course1).isEqualTo(course2);

        course2 = getCourseSample2();
        assertThat(course1).isNotEqualTo(course2);
    }

    @Test
    void scheduleTest() {
        Course course = getCourseRandomSampleGenerator();
        Schedule scheduleBack = getScheduleRandomSampleGenerator();

        course.addSchedule(scheduleBack);
        assertThat(course.getSchedules()).containsOnly(scheduleBack);
        assertThat(scheduleBack.getCourse()).isEqualTo(course);

        course.removeSchedule(scheduleBack);
        assertThat(course.getSchedules()).doesNotContain(scheduleBack);
        assertThat(scheduleBack.getCourse()).isNull();

        course.schedules(new HashSet<>(Set.of(scheduleBack)));
        assertThat(course.getSchedules()).containsOnly(scheduleBack);
        assertThat(scheduleBack.getCourse()).isEqualTo(course);

        course.setSchedules(new HashSet<>());
        assertThat(course.getSchedules()).doesNotContain(scheduleBack);
        assertThat(scheduleBack.getCourse()).isNull();
    }

    @Test
    void zoneTest() {
        Course course = getCourseRandomSampleGenerator();
        Zone zoneBack = getZoneRandomSampleGenerator();

        course.addZone(zoneBack);
        assertThat(course.getZones()).containsOnly(zoneBack);

        course.removeZone(zoneBack);
        assertThat(course.getZones()).doesNotContain(zoneBack);

        course.zones(new HashSet<>(Set.of(zoneBack)));
        assertThat(course.getZones()).containsOnly(zoneBack);

        course.setZones(new HashSet<>());
        assertThat(course.getZones()).doesNotContain(zoneBack);
    }

    @Test
    void trainerTest() {
        Course course = getCourseRandomSampleGenerator();
        UserData userDataBack = getUserDataRandomSampleGenerator();

        course.setTrainer(userDataBack);
        assertThat(course.getTrainer()).isEqualTo(userDataBack);

        course.trainer(null);
        assertThat(course.getTrainer()).isNull();
    }

    @Test
    void reservationsTest() {
        Course course = getCourseRandomSampleGenerator();
        Reservation reservationBack = getReservationRandomSampleGenerator();

        course.addReservations(reservationBack);
        assertThat(course.getReservations()).containsOnly(reservationBack);
        assertThat(reservationBack.getCourse()).isEqualTo(course);

        course.removeReservations(reservationBack);
        assertThat(course.getReservations()).doesNotContain(reservationBack);
        assertThat(reservationBack.getCourse()).isNull();

        course.reservations(new HashSet<>(Set.of(reservationBack)));
        assertThat(course.getReservations()).containsOnly(reservationBack);
        assertThat(reservationBack.getCourse()).isEqualTo(course);

        course.setReservations(new HashSet<>());
        assertThat(course.getReservations()).doesNotContain(reservationBack);
        assertThat(reservationBack.getCourse()).isNull();
    }
}
