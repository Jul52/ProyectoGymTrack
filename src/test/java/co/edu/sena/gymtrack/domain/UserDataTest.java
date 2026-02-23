package co.edu.sena.gymtrack.domain;

import static co.edu.sena.gymtrack.domain.CourseTestSamples.*;
import static co.edu.sena.gymtrack.domain.DocumentTypeTestSamples.*;
import static co.edu.sena.gymtrack.domain.InvoiceTestSamples.*;
import static co.edu.sena.gymtrack.domain.MachineTestSamples.*;
import static co.edu.sena.gymtrack.domain.PaymentTestSamples.*;
import static co.edu.sena.gymtrack.domain.ReservationTestSamples.*;
import static co.edu.sena.gymtrack.domain.UserDataTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import co.edu.sena.gymtrack.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class UserDataTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserData.class);
        UserData userData1 = getUserDataSample1();
        UserData userData2 = new UserData();
        assertThat(userData1).isNotEqualTo(userData2);

        userData2.setId(userData1.getId());
        assertThat(userData1).isEqualTo(userData2);

        userData2 = getUserDataSample2();
        assertThat(userData1).isNotEqualTo(userData2);
    }

    @Test
    void reservationTest() {
        UserData userData = getUserDataRandomSampleGenerator();
        Reservation reservationBack = getReservationRandomSampleGenerator();

        userData.addReservation(reservationBack);
        assertThat(userData.getReservations()).containsOnly(reservationBack);
        // CAMBIO: getUserData() -> getRegisteredBy()
        assertThat(reservationBack.getRegisteredBy()).isEqualTo(userData);

        userData.removeReservation(reservationBack);
        assertThat(userData.getReservations()).doesNotContain(reservationBack);
        // CAMBIO: getUserData() -> getRegisteredBy()
        assertThat(reservationBack.getRegisteredBy()).isNull();

        userData.reservations(new HashSet<>(Set.of(reservationBack)));
        assertThat(userData.getReservations()).containsOnly(reservationBack);
        // CAMBIO: getUserData() -> getRegisteredBy()
        assertThat(reservationBack.getRegisteredBy()).isEqualTo(userData);

        userData.setReservations(new HashSet<>());
        assertThat(userData.getReservations()).doesNotContain(reservationBack);
        // CAMBIO: getUserData() -> getRegisteredBy()
        assertThat(reservationBack.getRegisteredBy()).isNull();
    }

    @Test
    void machineTest() {
        UserData userData = getUserDataRandomSampleGenerator();
        Machine machineBack = getMachineRandomSampleGenerator();

        userData.addMachine(machineBack);
        assertThat(userData.getMachines()).containsOnly(machineBack);
        assertThat(machineBack.getAdmin()).isEqualTo(userData);

        userData.removeMachine(machineBack);
        assertThat(userData.getMachines()).doesNotContain(machineBack);
        assertThat(machineBack.getAdmin()).isNull();

        userData.machines(new HashSet<>(Set.of(machineBack)));
        assertThat(userData.getMachines()).containsOnly(machineBack);
        assertThat(machineBack.getAdmin()).isEqualTo(userData);

        userData.setMachines(new HashSet<>());
        assertThat(userData.getMachines()).doesNotContain(machineBack);
        assertThat(machineBack.getAdmin()).isNull();
    }

    @Test
    void invoiceTest() {
        UserData userData = getUserDataRandomSampleGenerator();
        Invoice invoiceBack = getInvoiceRandomSampleGenerator();

        userData.addInvoice(invoiceBack);
        assertThat(userData.getInvoices()).containsOnly(invoiceBack);
        assertThat(invoiceBack.getUserData()).isEqualTo(userData);

        userData.removeInvoice(invoiceBack);
        assertThat(userData.getInvoices()).doesNotContain(invoiceBack);
        assertThat(invoiceBack.getUserData()).isNull();

        userData.invoices(new HashSet<>(Set.of(invoiceBack)));
        assertThat(userData.getInvoices()).containsOnly(invoiceBack);
        assertThat(invoiceBack.getUserData()).isEqualTo(userData);

        userData.setInvoices(new HashSet<>());
        assertThat(userData.getInvoices()).doesNotContain(invoiceBack);
        assertThat(invoiceBack.getUserData()).isNull();
    }

    @Test
    void courseTest() {
        UserData userData = getUserDataRandomSampleGenerator();
        Course courseBack = getCourseRandomSampleGenerator();

        userData.addCourse(courseBack);
        assertThat(userData.getCourses()).containsOnly(courseBack);
        assertThat(courseBack.getTrainer()).isEqualTo(userData);

        userData.removeCourse(courseBack);
        assertThat(userData.getCourses()).doesNotContain(courseBack);
        assertThat(courseBack.getTrainer()).isNull();

        userData.courses(new HashSet<>(Set.of(courseBack)));
        assertThat(userData.getCourses()).containsOnly(courseBack);
        assertThat(courseBack.getTrainer()).isEqualTo(userData);

        userData.setCourses(new HashSet<>());
        assertThat(userData.getCourses()).doesNotContain(courseBack);
        assertThat(courseBack.getTrainer()).isNull();
    }

    @Test
    void paymentTest() {
        UserData userData = getUserDataRandomSampleGenerator();
        Payment paymentBack = getPaymentRandomSampleGenerator();

        userData.addPayment(paymentBack);
        assertThat(userData.getPayments()).containsOnly(paymentBack);
        assertThat(paymentBack.getRegisteredBy()).isEqualTo(userData);

        userData.removePayment(paymentBack);
        assertThat(userData.getPayments()).doesNotContain(paymentBack);
        assertThat(paymentBack.getRegisteredBy()).isNull();

        userData.payments(new HashSet<>(Set.of(paymentBack)));
        assertThat(userData.getPayments()).containsOnly(paymentBack);
        assertThat(paymentBack.getRegisteredBy()).isEqualTo(userData);

        userData.setPayments(new HashSet<>());
        assertThat(userData.getPayments()).doesNotContain(paymentBack);
        assertThat(paymentBack.getRegisteredBy()).isNull();
    }

    @Test
    void documentTypeTest() {
        UserData userData = getUserDataRandomSampleGenerator();
        DocumentType documentTypeBack = getDocumentTypeRandomSampleGenerator();

        userData.setDocumentType(documentTypeBack);
        assertThat(userData.getDocumentType()).isEqualTo(documentTypeBack);

        userData.documentType(null);
        assertThat(userData.getDocumentType()).isNull();
    }
}
