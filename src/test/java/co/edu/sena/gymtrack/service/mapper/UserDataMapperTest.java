package co.edu.sena.gymtrack.service.mapper;

import static co.edu.sena.gymtrack.domain.UserDataAsserts.*;
import static co.edu.sena.gymtrack.domain.UserDataTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserDataMapperTest {

    private UserDataMapper userDataMapper;

    @BeforeEach
    void setUp() {
        userDataMapper = new UserDataMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getUserDataSample1();
        var actual = userDataMapper.toEntity(userDataMapper.toDto(expected));
        assertUserDataAllPropertiesEquals(expected, actual);
    }
}
