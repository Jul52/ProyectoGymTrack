package co.edu.sena.gymtrack.domain;

import static co.edu.sena.gymtrack.domain.GymServiceTestSamples.*;
import static co.edu.sena.gymtrack.domain.InvoiceServiceTestSamples.*;
import static co.edu.sena.gymtrack.domain.InvoiceTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import co.edu.sena.gymtrack.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InvoiceServiceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(InvoiceService.class);
        InvoiceService invoiceService1 = getInvoiceServiceSample1();
        InvoiceService invoiceService2 = new InvoiceService();
        assertThat(invoiceService1).isNotEqualTo(invoiceService2);

        invoiceService2.setId(invoiceService1.getId());
        assertThat(invoiceService1).isEqualTo(invoiceService2);

        invoiceService2 = getInvoiceServiceSample2();
        assertThat(invoiceService1).isNotEqualTo(invoiceService2);
    }

    @Test
    void invoiceTest() {
        InvoiceService invoiceService = getInvoiceServiceRandomSampleGenerator();
        Invoice invoiceBack = getInvoiceRandomSampleGenerator();

        invoiceService.setInvoice(invoiceBack);
        assertThat(invoiceService.getInvoice()).isEqualTo(invoiceBack);

        invoiceService.invoice(null);
        assertThat(invoiceService.getInvoice()).isNull();
    }

    @Test
    void serviceTest() {
        InvoiceService invoiceService = getInvoiceServiceRandomSampleGenerator();
        GymService gymServiceBack = getGymServiceRandomSampleGenerator();

        invoiceService.setService(gymServiceBack);
        assertThat(invoiceService.getService()).isEqualTo(gymServiceBack);

        invoiceService.service(null);
        assertThat(invoiceService.getService()).isNull();
    }
}
