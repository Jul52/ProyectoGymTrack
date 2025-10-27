package co.edu.sena.gymtrack.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MachineIncidentsTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static MachineIncidents getMachineIncidentsSample1() {
        return new MachineIncidents().id(1L).description("description1").video("video1");
    }

    public static MachineIncidents getMachineIncidentsSample2() {
        return new MachineIncidents().id(2L).description("description2").video("video2");
    }

    public static MachineIncidents getMachineIncidentsRandomSampleGenerator() {
        return new MachineIncidents()
            .id(longCount.incrementAndGet())
            .description(UUID.randomUUID().toString())
            .video(UUID.randomUUID().toString());
    }
}
