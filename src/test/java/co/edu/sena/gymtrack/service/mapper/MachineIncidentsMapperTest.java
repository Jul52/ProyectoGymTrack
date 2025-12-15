package co.edu.sena.gymtrack.service.mapper;

import static co.edu.sena.gymtrack.domain.MachineIncidentsAsserts.*;
import static co.edu.sena.gymtrack.domain.MachineIncidentsTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MachineIncidentsMapperTest {

    private MachineIncidentsMapper machineIncidentsMapper;

    @BeforeEach
    void setUp() {
        machineIncidentsMapper = new MachineIncidentsMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMachineIncidentsSample1();
        var actual = machineIncidentsMapper.toEntity(machineIncidentsMapper.toDto(expected));
        assertMachineIncidentsAllPropertiesEquals(expected, actual);
    }
}
