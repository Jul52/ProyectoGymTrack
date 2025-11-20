package co.edu.sena.gymtrack.service.mapper;

import static co.edu.sena.gymtrack.domain.MachineAsserts.*;
import static co.edu.sena.gymtrack.domain.MachineTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MachineMapperTest {

    private MachineMapper machineMapper;

    @BeforeEach
    void setUp() {
        machineMapper = new MachineMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMachineSample1();
        var actual = machineMapper.toEntity(machineMapper.toDto(expected));
        assertMachineAllPropertiesEquals(expected, actual);
    }
}
