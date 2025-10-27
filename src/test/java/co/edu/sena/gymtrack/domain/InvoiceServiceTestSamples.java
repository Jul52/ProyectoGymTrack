package co.edu.sena.gymtrack.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class InvoiceServiceTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static InvoiceService getInvoiceServiceSample1() {
        return new InvoiceService().id(1L).quantity(1);
    }

    public static InvoiceService getInvoiceServiceSample2() {
        return new InvoiceService().id(2L).quantity(2);
    }

    public static InvoiceService getInvoiceServiceRandomSampleGenerator() {
        return new InvoiceService().id(longCount.incrementAndGet()).quantity(intCount.incrementAndGet());
    }
}
