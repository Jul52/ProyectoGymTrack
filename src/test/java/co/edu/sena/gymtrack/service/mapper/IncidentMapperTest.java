package co.edu.sena.gymtrack.service.mapper;

import static co.edu.sena.gymtrack.domain.IncidentAsserts.*;
import static co.edu.sena.gymtrack.domain.IncidentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IncidentMapperTest {

    private IncidentMapper incidentMapper;

    @BeforeEach
    void setUp() {
        incidentMapper = new IncidentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getIncidentSample1();
        var actual = incidentMapper.toEntity(incidentMapper.toDto(expected));
        assertIncidentAllPropertiesEquals(expected, actual);
    }
}
