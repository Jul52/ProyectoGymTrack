package co.edu.sena.gymtrack.service.mapper;

import static co.edu.sena.gymtrack.domain.InvoiceAsserts.*;
import static co.edu.sena.gymtrack.domain.InvoiceTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InvoiceMapperTest {

    private InvoiceMapper invoiceMapper;

    @BeforeEach
    void setUp() {
        invoiceMapper = new InvoiceMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getInvoiceSample1();
        var actual = invoiceMapper.toEntity(invoiceMapper.toDto(expected));
        assertInvoiceAllPropertiesEquals(expected, actual);
    }
}
