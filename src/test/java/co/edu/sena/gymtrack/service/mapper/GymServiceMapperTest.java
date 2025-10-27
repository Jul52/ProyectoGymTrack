package co.edu.sena.gymtrack.service.mapper;

import static co.edu.sena.gymtrack.domain.GymServiceAsserts.*;
import static co.edu.sena.gymtrack.domain.GymServiceTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GymServiceMapperTest {

    private GymServiceMapper gymServiceMapper;

    @BeforeEach
    void setUp() {
        gymServiceMapper = new GymServiceMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getGymServiceSample1();
        var actual = gymServiceMapper.toEntity(gymServiceMapper.toDto(expected));
        assertGymServiceAllPropertiesEquals(expected, actual);
    }
}
