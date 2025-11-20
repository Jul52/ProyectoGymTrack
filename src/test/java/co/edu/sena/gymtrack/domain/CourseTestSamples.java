package co.edu.sena.gymtrack.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CourseTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Course getCourseSample1() {
        return new Course().id(1L).courseName("courseName1").capacity(1);
    }

    public static Course getCourseSample2() {
        return new Course().id(2L).courseName("courseName2").capacity(2);
    }

    public static Course getCourseRandomSampleGenerator() {
        return new Course().id(longCount.incrementAndGet()).courseName(UUID.randomUUID().toString()).capacity(intCount.incrementAndGet());
    }
}
