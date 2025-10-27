package co.edu.sena.gymtrack.service.mapper;

import static co.edu.sena.gymtrack.domain.InvoiceServiceAsserts.*;
import static co.edu.sena.gymtrack.domain.InvoiceServiceTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InvoiceServiceMapperTest {

    private InvoiceServiceMapper invoiceServiceMapper;

    @BeforeEach
    void setUp() {
        invoiceServiceMapper = new InvoiceServiceMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getInvoiceServiceSample1();
        var actual = invoiceServiceMapper.toEntity(invoiceServiceMapper.toDto(expected));
        assertInvoiceServiceAllPropertiesEquals(expected, actual);
    }
}
