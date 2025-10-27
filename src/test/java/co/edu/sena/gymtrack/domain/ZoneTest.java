package co.edu.sena.gymtrack.domain;

import static co.edu.sena.gymtrack.domain.CourseTestSamples.*;
import static co.edu.sena.gymtrack.domain.ZoneTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import co.edu.sena.gymtrack.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ZoneTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Zone.class);
        Zone zone1 = getZoneSample1();
        Zone zone2 = new Zone();
        assertThat(zone1).isNotEqualTo(zone2);

        zone2.setId(zone1.getId());
        assertThat(zone1).isEqualTo(zone2);

        zone2 = getZoneSample2();
        assertThat(zone1).isNotEqualTo(zone2);
    }

    @Test
    void courseTest() {
        Zone zone = getZoneRandomSampleGenerator();
        Course courseBack = getCourseRandomSampleGenerator();

        zone.addCourse(courseBack);
        assertThat(zone.getCourses()).containsOnly(courseBack);
        assertThat(courseBack.getZones()).containsOnly(zone);

        zone.removeCourse(courseBack);
        assertThat(zone.getCourses()).doesNotContain(courseBack);
        assertThat(courseBack.getZones()).doesNotContain(zone);

        zone.courses(new HashSet<>(Set.of(courseBack)));
        assertThat(zone.getCourses()).containsOnly(courseBack);
        assertThat(courseBack.getZones()).containsOnly(zone);

        zone.setCourses(new HashSet<>());
        assertThat(zone.getCourses()).doesNotContain(courseBack);
        assertThat(courseBack.getZones()).doesNotContain(zone);
    }
}
