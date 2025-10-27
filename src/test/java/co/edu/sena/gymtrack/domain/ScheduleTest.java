package co.edu.sena.gymtrack.domain;

import static co.edu.sena.gymtrack.domain.CourseTestSamples.*;
import static co.edu.sena.gymtrack.domain.ScheduleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import co.edu.sena.gymtrack.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ScheduleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Schedule.class);
        Schedule schedule1 = getScheduleSample1();
        Schedule schedule2 = new Schedule();
        assertThat(schedule1).isNotEqualTo(schedule2);

        schedule2.setId(schedule1.getId());
        assertThat(schedule1).isEqualTo(schedule2);

        schedule2 = getScheduleSample2();
        assertThat(schedule1).isNotEqualTo(schedule2);
    }

    @Test
    void courseTest() {
        Schedule schedule = getScheduleRandomSampleGenerator();
        Course courseBack = getCourseRandomSampleGenerator();

        schedule.setCourse(courseBack);
        assertThat(schedule.getCourse()).isEqualTo(courseBack);

        schedule.course(null);
        assertThat(schedule.getCourse()).isNull();
    }
}
