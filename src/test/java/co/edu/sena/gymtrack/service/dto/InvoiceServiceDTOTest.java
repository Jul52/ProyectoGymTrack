package co.edu.sena.gymtrack.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import co.edu.sena.gymtrack.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InvoiceServiceDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(InvoiceServiceDTO.class);
        InvoiceServiceDTO invoiceServiceDTO1 = new InvoiceServiceDTO();
        invoiceServiceDTO1.setId(1L);
        InvoiceServiceDTO invoiceServiceDTO2 = new InvoiceServiceDTO();
        assertThat(invoiceServiceDTO1).isNotEqualTo(invoiceServiceDTO2);
        invoiceServiceDTO2.setId(invoiceServiceDTO1.getId());
        assertThat(invoiceServiceDTO1).isEqualTo(invoiceServiceDTO2);
        invoiceServiceDTO2.setId(2L);
        assertThat(invoiceServiceDTO1).isNotEqualTo(invoiceServiceDTO2);
        invoiceServiceDTO1.setId(null);
        assertThat(invoiceServiceDTO1).isNotEqualTo(invoiceServiceDTO2);
    }
}
