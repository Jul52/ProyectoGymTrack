package co.edu.sena.gymtrack.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class GymServiceTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static GymService getGymServiceSample1() {
        return new GymService().id(1L).serviceName("serviceName1").serviceDescription("serviceDescription1");
    }

    public static GymService getGymServiceSample2() {
        return new GymService().id(2L).serviceName("serviceName2").serviceDescription("serviceDescription2");
    }

    public static GymService getGymServiceRandomSampleGenerator() {
        return new GymService()
            .id(longCount.incrementAndGet())
            .serviceName(UUID.randomUUID().toString())
            .serviceDescription(UUID.randomUUID().toString());
    }
}
