package co.edu.sena.gymtrack.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class IncidentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Incident getIncidentSample1() {
        return new Incident().id(1L).incidentType("incidentType1").description("description1");
    }

    public static Incident getIncidentSample2() {
        return new Incident().id(2L).incidentType("incidentType2").description("description2");
    }

    public static Incident getIncidentRandomSampleGenerator() {
        return new Incident()
            .id(longCount.incrementAndGet())
            .incidentType(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
