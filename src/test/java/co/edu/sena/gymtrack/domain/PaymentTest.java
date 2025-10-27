package co.edu.sena.gymtrack.domain;

import static co.edu.sena.gymtrack.domain.InvoiceTestSamples.*;
import static co.edu.sena.gymtrack.domain.PaymentMethodTestSamples.*;
import static co.edu.sena.gymtrack.domain.PaymentTestSamples.*;
import static co.edu.sena.gymtrack.domain.UserDataTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import co.edu.sena.gymtrack.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PaymentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Payment.class);
        Payment payment1 = getPaymentSample1();
        Payment payment2 = new Payment();
        assertThat(payment1).isNotEqualTo(payment2);

        payment2.setId(payment1.getId());
        assertThat(payment1).isEqualTo(payment2);

        payment2 = getPaymentSample2();
        assertThat(payment1).isNotEqualTo(payment2);
    }

    @Test
    void paymentMethodTest() {
        Payment payment = getPaymentRandomSampleGenerator();
        PaymentMethod paymentMethodBack = getPaymentMethodRandomSampleGenerator();

        payment.setPaymentMethod(paymentMethodBack);
        assertThat(payment.getPaymentMethod()).isEqualTo(paymentMethodBack);

        payment.paymentMethod(null);
        assertThat(payment.getPaymentMethod()).isNull();
    }

    @Test
    void invoiceTest() {
        Payment payment = getPaymentRandomSampleGenerator();
        Invoice invoiceBack = getInvoiceRandomSampleGenerator();

        payment.setInvoice(invoiceBack);
        assertThat(payment.getInvoice()).isEqualTo(invoiceBack);
        assertThat(invoiceBack.getPayment()).isEqualTo(payment);

        payment.invoice(null);
        assertThat(payment.getInvoice()).isNull();
        assertThat(invoiceBack.getPayment()).isNull();
    }

    @Test
    void registeredByTest() {
        Payment payment = getPaymentRandomSampleGenerator();
        UserData userDataBack = getUserDataRandomSampleGenerator();

        payment.setRegisteredBy(userDataBack);
        assertThat(payment.getRegisteredBy()).isEqualTo(userDataBack);

        payment.registeredBy(null);
        assertThat(payment.getRegisteredBy()).isNull();
    }
}
