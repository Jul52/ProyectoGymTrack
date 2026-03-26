package co.edu.sena.gymtrack.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class UserDataTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static UserData getUserDataSample1() {
        return new UserData()
            .id(1L)
            .firstName("firstName1")
            .secondName("secondName1")
            .firstLastName("firstLastName1")
            .secondLastName("secondLastName1")
            .documentNumber("document1")
            .phone("phoneNumber1");
    }

    public static UserData getUserDataSample2() {
        return new UserData()
            .id(2L)
            .firstName("firstName2")
            .secondName("secondName2")
            .firstLastName("firstLastName2")
            .secondLastName("secondLastName2")
            .documentNumber("document2")
            .phone("phoneNumber2");
    }

    public static UserData getUserDataRandomSampleGenerator() {
        return new UserData()
            .id(longCount.incrementAndGet())
            .firstName(UUID.randomUUID().toString())
            .secondName(UUID.randomUUID().toString())
            .firstLastName(UUID.randomUUID().toString())
            .secondLastName(UUID.randomUUID().toString())
            .documentNumber(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString());
    }
}
