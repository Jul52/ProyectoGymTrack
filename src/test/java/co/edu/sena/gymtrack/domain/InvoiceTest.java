package co.edu.sena.gymtrack.domain;

import static co.edu.sena.gymtrack.domain.GymServiceTestSamples.*;
import static co.edu.sena.gymtrack.domain.InvoiceServiceTestSamples.*;
import static co.edu.sena.gymtrack.domain.InvoiceTestSamples.*;
import static co.edu.sena.gymtrack.domain.PaymentMethodTestSamples.*;
import static co.edu.sena.gymtrack.domain.PaymentTestSamples.*;
import static co.edu.sena.gymtrack.domain.UserDataTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import co.edu.sena.gymtrack.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class InvoiceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Invoice.class);
        Invoice invoice1 = getInvoiceSample1();
        Invoice invoice2 = new Invoice();
        assertThat(invoice1).isNotEqualTo(invoice2);

        invoice2.setId(invoice1.getId());
        assertThat(invoice1).isEqualTo(invoice2);

        invoice2 = getInvoiceSample2();
        assertThat(invoice1).isNotEqualTo(invoice2);
    }

    @Test
    void paymentTest() {
        Invoice invoice = getInvoiceRandomSampleGenerator();
        Payment paymentBack = getPaymentRandomSampleGenerator();

        invoice.setPayment(paymentBack);
        assertThat(invoice.getPayment()).isEqualTo(paymentBack);

        invoice.payment(null);
        assertThat(invoice.getPayment()).isNull();
    }

    @Test
    void invoiceServiceTest() {
        Invoice invoice = getInvoiceRandomSampleGenerator();
        InvoiceService invoiceServiceBack = getInvoiceServiceRandomSampleGenerator();

        invoice.addInvoiceService(invoiceServiceBack);
        assertThat(invoice.getInvoiceServices()).containsOnly(invoiceServiceBack);
        assertThat(invoiceServiceBack.getInvoice()).isEqualTo(invoice);

        invoice.removeInvoiceService(invoiceServiceBack);
        assertThat(invoice.getInvoiceServices()).doesNotContain(invoiceServiceBack);
        assertThat(invoiceServiceBack.getInvoice()).isNull();

        invoice.invoiceServices(new HashSet<>(Set.of(invoiceServiceBack)));
        assertThat(invoice.getInvoiceServices()).containsOnly(invoiceServiceBack);
        assertThat(invoiceServiceBack.getInvoice()).isEqualTo(invoice);

        invoice.setInvoiceServices(new HashSet<>());
        assertThat(invoice.getInvoiceServices()).doesNotContain(invoiceServiceBack);
        assertThat(invoiceServiceBack.getInvoice()).isNull();
    }

    @Test
    void paymentMethodTest() {
        Invoice invoice = getInvoiceRandomSampleGenerator();
        PaymentMethod paymentMethodBack = getPaymentMethodRandomSampleGenerator();

        invoice.setPaymentMethod(paymentMethodBack);
        assertThat(invoice.getPaymentMethod()).isEqualTo(paymentMethodBack);

        invoice.paymentMethod(null);
        assertThat(invoice.getPaymentMethod()).isNull();
    }

    @Test
    void userDataTest() {
        Invoice invoice = getInvoiceRandomSampleGenerator();
        UserData userDataBack = getUserDataRandomSampleGenerator();

        invoice.setUserData(userDataBack);
        assertThat(invoice.getUserData()).isEqualTo(userDataBack);

        invoice.userData(null);
        assertThat(invoice.getUserData()).isNull();
    }

    @Test
    void serviceTest() {
        Invoice invoice = getInvoiceRandomSampleGenerator();
        GymService gymServiceBack = getGymServiceRandomSampleGenerator();

        invoice.setService(gymServiceBack);
        assertThat(invoice.getService()).isEqualTo(gymServiceBack);

        invoice.service(null);
        assertThat(invoice.getService()).isNull();
    }
}
