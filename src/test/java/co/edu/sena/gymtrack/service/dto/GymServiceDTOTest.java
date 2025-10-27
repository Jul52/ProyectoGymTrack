package co.edu.sena.gymtrack.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import co.edu.sena.gymtrack.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class GymServiceDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(GymServiceDTO.class);
        GymServiceDTO gymServiceDTO1 = new GymServiceDTO();
        gymServiceDTO1.setId(1L);
        GymServiceDTO gymServiceDTO2 = new GymServiceDTO();
        assertThat(gymServiceDTO1).isNotEqualTo(gymServiceDTO2);
        gymServiceDTO2.setId(gymServiceDTO1.getId());
        assertThat(gymServiceDTO1).isEqualTo(gymServiceDTO2);
        gymServiceDTO2.setId(2L);
        assertThat(gymServiceDTO1).isNotEqualTo(gymServiceDTO2);
        gymServiceDTO1.setId(null);
        assertThat(gymServiceDTO1).isNotEqualTo(gymServiceDTO2);
    }
}
