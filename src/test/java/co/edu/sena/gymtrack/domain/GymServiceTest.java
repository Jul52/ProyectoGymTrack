package co.edu.sena.gymtrack.domain;

import static co.edu.sena.gymtrack.domain.CategoryTestSamples.*;
import static co.edu.sena.gymtrack.domain.GymServiceTestSamples.*;
import static co.edu.sena.gymtrack.domain.InvoiceServiceTestSamples.*;
import static co.edu.sena.gymtrack.domain.ReservationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import co.edu.sena.gymtrack.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class GymServiceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(GymService.class);
        GymService gymService1 = getGymServiceSample1();
        GymService gymService2 = new GymService();
        assertThat(gymService1).isNotEqualTo(gymService2);

        gymService2.setId(gymService1.getId());
        assertThat(gymService1).isEqualTo(gymService2);

        gymService2 = getGymServiceSample2();
        assertThat(gymService1).isNotEqualTo(gymService2);
    }

    @Test
    void invoiceServiceTest() {
        GymService gymService = getGymServiceRandomSampleGenerator();
        InvoiceService invoiceServiceBack = getInvoiceServiceRandomSampleGenerator();

        gymService.addInvoiceService(invoiceServiceBack);
        assertThat(gymService.getInvoiceServices()).containsOnly(invoiceServiceBack);
        assertThat(invoiceServiceBack.getService()).isEqualTo(gymService);

        gymService.removeInvoiceService(invoiceServiceBack);
        assertThat(gymService.getInvoiceServices()).doesNotContain(invoiceServiceBack);
        assertThat(invoiceServiceBack.getService()).isNull();

        gymService.invoiceServices(new HashSet<>(Set.of(invoiceServiceBack)));
        assertThat(gymService.getInvoiceServices()).containsOnly(invoiceServiceBack);
        assertThat(invoiceServiceBack.getService()).isEqualTo(gymService);

        gymService.setInvoiceServices(new HashSet<>());
        assertThat(gymService.getInvoiceServices()).doesNotContain(invoiceServiceBack);
        assertThat(invoiceServiceBack.getService()).isNull();
    }

    @Test
    void categoryTest() {
        GymService gymService = getGymServiceRandomSampleGenerator();
        Category categoryBack = getCategoryRandomSampleGenerator();

        gymService.setCategory(categoryBack);
        assertThat(gymService.getCategory()).isEqualTo(categoryBack);

        gymService.category(null);
        assertThat(gymService.getCategory()).isNull();
    }

    @Test
    void reservationsTest() {
        GymService gymService = getGymServiceRandomSampleGenerator();
        Reservation reservationBack = getReservationRandomSampleGenerator();

        gymService.addReservations(reservationBack);
        assertThat(gymService.getReservations()).containsOnly(reservationBack);
        assertThat(reservationBack.getGymService()).isEqualTo(gymService);

        gymService.removeReservations(reservationBack);
        assertThat(gymService.getReservations()).doesNotContain(reservationBack);
        assertThat(reservationBack.getGymService()).isNull();

        gymService.reservations(new HashSet<>(Set.of(reservationBack)));
        assertThat(gymService.getReservations()).containsOnly(reservationBack);
        assertThat(reservationBack.getGymService()).isEqualTo(gymService);

        gymService.setReservations(new HashSet<>());
        assertThat(gymService.getReservations()).doesNotContain(reservationBack);
        assertThat(reservationBack.getGymService()).isNull();
    }
}
